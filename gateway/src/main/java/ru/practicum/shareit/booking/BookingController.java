package ru.practicum.shareit.booking;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingAdvancedDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatus;

import javax.validation.Valid;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Bookings", description = "bookings API")
public class BookingController {
	private final BookingClient bookingClient;

	@Operation(summary = "Create a new booking",
			description = "Creating a new item booking by the booker for a certain period")
	@ApiResponse(responseCode = "200", description = "Successful",
			content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = BookingAdvancedDto.class))})
	@PostMapping
	public ResponseEntity<Object> createBooking(
			@Valid @RequestBody BookingDto bookingDto,
			@RequestHeader(name = "X-Sharer-User-Id") Long bookerId
	) {
		log.info("POST /bookings {}", bookingDto);
		return bookingClient.createBooking(bookingDto, bookerId);
	}

	@Operation(summary = "Update booking by id",
			description = "Update item booking by the booking id")
	@ApiResponse(responseCode = "200", description = "Successful",
			content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = BookingAdvancedDto.class))})
	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveBooking(
			@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
			@RequestParam boolean approved,
			@PathVariable Long bookingId
	) {
		log.info("PATCH /bookings/{}?approved={}", bookingId, approved);
		return bookingClient.approveBooking(ownerId, bookingId, approved);
	}

	@Operation(summary = "Get booking by id",
			description = "Get item booking by the booking id")
	@ApiResponse(responseCode = "200", description = "Successful",
			content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = BookingAdvancedDto.class))})
	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBookingByOwnerId(
			@RequestHeader(name = "X-Sharer-User-Id") Long userId,
			@PathVariable Long bookingId
	) {
		log.info("GET /bookings/{}", bookingId);
		return bookingClient.getBookingByOwnerId(userId, bookingId);
	}

	@Operation(summary = "Get bookings of booker",
			description = "Get bookings of booker by state with pagination")
	@ApiResponse(responseCode = "200", description = "Successful",
			content = {@Content(mediaType = "application/json",
					array = @ArraySchema(schema = @Schema(implementation = BookingAdvancedDto.class)))})
	@GetMapping
	public ResponseEntity<Object> getBookingsByStateOfBooker(
			@RequestHeader(name = "X-Sharer-User-Id") Long bookerId,
			@Parameter(allowEmptyValue = true, schema = @Schema(implementation = BookingStatus.class))
			@RequestParam(name = "state", required = false, defaultValue = "all") String state,
			@RequestParam(name = "from", defaultValue = "0") int from,
			@RequestParam(name = "size", defaultValue = "20") int size
	) {
		log.info("GET /bookings?state={}&from{}&size{}", state, from, size);
		return bookingClient.getBookingsByStateOfBooker(from, size, bookerId, state);
	}

	@Operation(summary = "Get bookings of item owner",
			description = "Get bookings of item owner by state with pagination")
	@ApiResponse(responseCode = "200", description = "Successful",
			content = {@Content(mediaType = "application/json",
					array = @ArraySchema(schema = @Schema(implementation = BookingAdvancedDto.class)))})
	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingsByStateOfOwner(
			@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
			@Parameter(allowEmptyValue = true,
					description = "all bookings are output if no state in query",
					schema = @Schema(implementation = BookingStatus.class))
			@RequestParam(name = "state", required = false, defaultValue = "all") String state,
			@RequestParam(name = "from", defaultValue = "0") int from,
			@RequestParam(name = "size", defaultValue = "20") int size

	) {
		log.info("GET /bookings/owner?state={}&from{}&size{}", state, from, size);
		return bookingClient.getBookingsByStateOfOwner(from, size, ownerId, state);
	}
}
