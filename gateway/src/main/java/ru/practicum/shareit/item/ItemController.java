package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;


@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
	private final ItemClient itemClient;

	@PostMapping
	public ResponseEntity<Object> createItem(
			@Valid @RequestBody ItemDto itemDto,
			@RequestHeader(name = "X-Sharer-User-Id") Long userId
	) {
		log.info("POST /items {}", itemDto);
		return itemClient.createItem(itemDto, userId);
	}

	@PatchMapping("/{itemId}")
	public ResponseEntity<Object> updateItem(
			@RequestBody ItemDto itemDto,
			@PathVariable Long itemId,
			@RequestHeader(name = "X-Sharer-User-Id") Long userId
	) {
		log.info("PATCH /items/{} {}", itemId, itemDto);
		return itemClient.updateItem(itemId, userId, itemDto);
	}

	@GetMapping("/{itemId}")
	public ResponseEntity<Object> getItemById(
			@PathVariable Long itemId,
			@RequestHeader(name = "X-Sharer-User-Id") Long userId
	) {
		log.info("GET /items/{}", itemId);
		return itemClient.getItemById(itemId, userId);
	}

	@GetMapping
	public ResponseEntity<Object> getItemsByOwnerId(
			@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
			@RequestParam(name = "from", defaultValue = "0") int from,
			@RequestParam(name = "size", defaultValue = "20") int size
	) {
		log.info("GET /items");
		return itemClient.getAllByOwnerId(from, size, ownerId);
	}

	@GetMapping("/search")
	public ResponseEntity<Object> searchItems(
			@RequestParam(name = "text", defaultValue = "") String text,
			@RequestParam(name = "from", defaultValue = "0") int from,
			@RequestParam(name = "size", defaultValue = "20") int size
	) {
		log.info("GET /items/search?text={}", text);
		return itemClient.searchItems(from, size, text);
	}

	@PostMapping("/{itemId}/comment")
	public ResponseEntity<Object> createComment(
			@Valid @RequestBody CommentDto commentDto,
			@PathVariable Long itemId,
			@RequestHeader(name = "X-Sharer-User-Id") Long authorId
	) {
		log.info("POST /items/{}/comment '{}'", itemId, commentDto.getText());
		return itemClient.createComment(commentDto, itemId, authorId);
	}
}
