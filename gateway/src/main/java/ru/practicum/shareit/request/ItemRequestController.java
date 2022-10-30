package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;


@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
	private final ItemRequestClient itemRequestClient;

	@PostMapping
	public ResponseEntity<Object> createRequest(
			@Valid @RequestBody ItemRequestDto itemRequestDto,
			@RequestHeader(name = "X-Sharer-User-Id") Long requesterId
	) {
		log.info("POST /requests : {}", itemRequestDto);
		return itemRequestClient.createRequest(itemRequestDto, requesterId);
	}

	@GetMapping
	public ResponseEntity<Object> getRequestsByRequesterId(
			@RequestHeader(name = "X-Sharer-User-Id") Long requesterId
	) {
		log.info("GET /requests");
		return itemRequestClient.getItemRequestsByRequesterId(requesterId);
	}

	@GetMapping("/all")
	public ResponseEntity<Object> getAllRequests(
			@RequestParam(name = "from", defaultValue = "0") int from,
			@RequestParam(name = "size", defaultValue = "20") int size,
			@RequestHeader(name = "X-Sharer-User-Id") Long requesterId
	) {
		log.info("GET /requests/all?from={}&size={}", from, size);
		return itemRequestClient.getAllRequests(from, size, requesterId);
	}

	@GetMapping("/{requestId}")
	public ResponseEntity<Object> getRequestById(
			@PathVariable Long requestId,
			@RequestHeader(name = "X-Sharer-User-Id") Long requesterId
	) {
		log.info("GET /requests/{}", requestId);
		return itemRequestClient.getRequestById(requestId, requesterId);
	}
}
