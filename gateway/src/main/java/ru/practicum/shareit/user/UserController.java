package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.Create;
import ru.practicum.shareit.util.Update;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "users API")
public class UserController {
	private final UserClient userClient;

	@Operation(summary = "Create a new user", description = "Creating a new user")
	@ApiResponse(responseCode = "200", description = "Successful",
			content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = UserDto.class))})
	@PostMapping
	public ResponseEntity<Object> create(
			@Validated(Create.class) @RequestBody UserDto userDto
	) {
		log.info("POST /users, create user {}", userDto);
		return userClient.createUser(userDto);
	}

	@Operation(summary = "Update user by id", description = "Update user by id")
	@ApiResponse(responseCode = "200", description = "Successful",
			content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = UserDto.class))})
	@PatchMapping("/{userId}")
	public ResponseEntity<Object> update(
			@Validated(Update.class) @RequestBody UserDto userDto,
			@PathVariable Long userId
	) {
		log.info("PATCH /users/{}, update user {}", userId, userDto);
		return userClient.updateUser(userId, userDto);
	}

	@Operation(summary = "Get user by id", description = "Get user by id")
	@ApiResponse(responseCode = "200", description = "Successful",
			content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = UserDto.class))})
	@GetMapping("/{userId}")
	public ResponseEntity<Object> getById(@PathVariable Long userId) {
		log.info("GET /users/{}", userId);
		return userClient.getUserById(userId);
	}

	@Operation(summary = "Get all users", description = "Get all users")
	@ApiResponse(responseCode = "200", description = "Successful",
			content = {@Content(mediaType = "application/json",
					array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))})
	@GetMapping
	public ResponseEntity<Object> getAll() {
		log.info("PATCH /users");
		return userClient.getAllUsers();
	}

	@Operation(summary = "Delete user by id", description = "Delete user by id")
	@ApiResponse(responseCode = "204", description = "No content")
	@DeleteMapping("/{userId}")
	public ResponseEntity<Object> delete(@PathVariable Long userId) {
		log.info("DELETE /users/{userId}");
		return userClient.deleteUser(userId);
	}
}
