package ru.practicum.shareit.item;

import io.swagger.v3.oas.annotations.Operation;
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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAdvancedDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;


@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Items", description = "items API")
public class ItemController {
	private final ItemClient itemClient;

	@Operation(summary = "Create a new item",
			description = "Creating a new item for booking by the owner")
	@ApiResponse(responseCode = "200", description = "Successful",
			content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = ItemAdvancedDto.class))})
	@PostMapping
	public ResponseEntity<Object> createItem(
			@Valid @RequestBody ItemDto itemDto,
			@RequestHeader(name = "X-Sharer-User-Id") Long userId
	) {
		log.info("POST /items {}", itemDto);
		return itemClient.createItem(itemDto, userId);
	}

	@Operation(summary = "Update item by id",
			description = "Update item by id")
	@ApiResponse(responseCode = "200", description = "Successful",
			content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = ItemAdvancedDto.class))})
	@PatchMapping("/{itemId}")
	public ResponseEntity<Object> updateItem(
			@RequestBody ItemDto itemDto,
			@PathVariable Long itemId,
			@RequestHeader(name = "X-Sharer-User-Id") Long userId
	) {
		log.info("PATCH /items/{} {}", itemId, itemDto);
		return itemClient.updateItem(itemId, userId, itemDto);
	}

	@Operation(summary = "Get item by id",
			description = "Get item by id")
	@ApiResponse(responseCode = "200", description = "Successful",
			content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = ItemAdvancedDto.class))})
	@GetMapping("/{itemId}")
	public ResponseEntity<Object> getItemById(
			@PathVariable Long itemId,
			@RequestHeader(name = "X-Sharer-User-Id") Long userId
	) {
		log.info("GET /items/{}", itemId);
		return itemClient.getItemById(itemId, userId);
	}

	@Operation(summary = "Get all items by owner id",
			description = "Get all items of owner by his id")
	@ApiResponse(responseCode = "200", description = "Successful",
			content = {@Content(mediaType = "application/json",
					array = @ArraySchema(schema = @Schema(implementation = ItemAdvancedDto.class)))})
	@GetMapping
	public ResponseEntity<Object> getItemsByOwnerId(
			@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
			@RequestParam(name = "from", defaultValue = "0") int from,
			@RequestParam(name = "size", defaultValue = "20") int size
	) {
		log.info("GET /items");
		return itemClient.getAllByOwnerId(from, size, ownerId);
	}

	@Operation(summary = "Search items",
			description = "Search items by name or description")
	@ApiResponse(responseCode = "200", description = "Successful",
			content = {@Content(mediaType = "application/json",
					array = @ArraySchema(schema = @Schema(implementation = ItemAdvancedDto.class)))})
	@GetMapping("/search")
	public ResponseEntity<Object> searchItems(
			@RequestParam(name = "text", defaultValue = "") String text,
			@RequestParam(name = "from", defaultValue = "0") int from,
			@RequestParam(name = "size", defaultValue = "20") int size
	) {
		log.info("GET /items/search?text={}", text);
		return itemClient.searchItems(from, size, text);
	}

	@Operation(summary = "Add comment",
			description = "Add comment for items by booker after using one")
	@ApiResponse(responseCode = "200", description = "Successful",
			content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = CommentDto.class))})
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
