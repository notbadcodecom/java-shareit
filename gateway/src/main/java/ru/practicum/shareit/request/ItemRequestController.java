package ru.practicum.shareit.request;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Item requests", description = "requests API")
public class ItemRequestController {
	private final ItemRequestClient itemRequestClient;

	@Operation(summary = "Create a new item request",
			description = "Creating a new item request by the user")
	@ApiResponse(responseCode = "200", description = "Successful",
			content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = ItemRequestDto.class))})
	@PostMapping
	public ResponseEntity<Object> createRequest(
			@Valid @RequestBody ItemRequestDto itemRequestDto,
			@RequestHeader(name = "X-Sharer-User-Id") Long requesterId
	) {
		log.info("POST /requests : {}", itemRequestDto);
		return itemRequestClient.createRequest(itemRequestDto, requesterId);
	}

	@Operation(summary = "Get all requests by requester",
			description = "Get all requests of item by requester id")
	@ApiResponse(responseCode = "200", description = "Successful",
			content = {@Content(mediaType = "application/json",
					array = @ArraySchema(schema = @Schema(implementation = ItemRequestDto.class)))})
	@GetMapping
	public ResponseEntity<Object> getRequestsByRequesterId(
			@RequestHeader(name = "X-Sharer-User-Id") Long requesterId
	) {
		log.info("GET /requests");
		return itemRequestClient.getItemRequestsByRequesterId(requesterId);
	}

	@Operation(summary = "Get all requests of other users",
			description = "Get all requests of other users exclude requester requests")
	@ApiResponse(responseCode = "200", description = "Successful",
			content = {@Content(mediaType = "application/json",
					array = @ArraySchema(schema = @Schema(implementation = ItemRequestDto.class)))})
	@GetMapping("/all")
	public ResponseEntity<Object> getAllRequests(
			@RequestParam(name = "from", defaultValue = "0") int from,
			@RequestParam(name = "size", defaultValue = "20") int size,
			@RequestHeader(name = "X-Sharer-User-Id") Long requesterId
	) {
		log.info("GET /requests/all?from={}&size={}", from, size);
		return itemRequestClient.getAllRequests(from, size, requesterId);
	}

	@Operation(summary = "Get item request",
			description = "Get item request by the request id")
	@ApiResponse(responseCode = "200", description = "Successful",
			content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = ItemRequestDto.class))})
	@GetMapping("/{requestId}")
	public ResponseEntity<Object> getRequestById(
			@PathVariable Long requestId,
			@RequestHeader(name = "X-Sharer-User-Id") Long requesterId
	) {
		log.info("GET /requests/{}", requestId);
		return itemRequestClient.getRequestById(requestId, requesterId);
	}
}
