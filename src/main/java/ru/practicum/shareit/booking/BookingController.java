package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingAdvancedDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public BookingAdvancedDto create(
            @Valid @RequestBody BookingDto bookingDto,
            @RequestHeader(name = "X-Sharer-User-Id") Long bookerId
    ) {
        log.info("POST /bookings {}", bookingDto);
        return bookingService.create(bookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingAdvancedDto approve(
            @RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
            @RequestParam boolean approved,
            @PathVariable Long bookingId
    ) {
        log.info("PATCH /bookings/{}?approved={}", bookingId, approved);
        return bookingService.approve(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingAdvancedDto getById(
            @RequestHeader(name = "X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId
    ) {
        log.info("GET /bookings/{}", bookingId);
        return bookingService.getByOwnerId(userId, bookingId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingAdvancedDto> getBookingsByStateOfBooker(
            @RequestHeader(name = "X-Sharer-User-Id") Long bookerId,
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        log.info("GET /bookings?state={}&from{}&size{}", state, from, size);
        return bookingService.getAllOfBookerByState(from, size, bookerId, state);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingAdvancedDto> getBookingsByStateOfOwner(
            @RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "20") int size

    ) {
        log.info("GET /bookings/owner?state={}&from{}&size{}", state, from, size);
        return bookingService.getAllOfOwnerByState(from, size, ownerId, state);
    }
}
