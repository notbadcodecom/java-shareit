package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.Create;
import ru.practicum.shareit.util.Update;


@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
	private final UserClient userClient;

	@PostMapping
	public ResponseEntity<Object> create(
			@Validated(Create.class) @RequestBody UserDto userDto
	) {
		log.info("POST /users, create user {}", userDto);
		return userClient.createUser(userDto);
	}

	@PatchMapping("/{userId}")
	public ResponseEntity<Object> update(
			@Validated(Update.class) @RequestBody UserDto userDto,
			@PathVariable Long userId
	) {
		log.info("PATCH /users/{}, update user {}", userId, userDto);
		return userClient.updateUser(userId, userDto);
	}

	@GetMapping("/{userId}")
	public ResponseEntity<Object> getById(@PathVariable Long userId) {
		log.info("GET /users/{}", userId);
		return userClient.getUserById(userId);
	}

	@GetMapping
	public ResponseEntity<Object> getAll() {
		log.info("PATCH /users");
		return userClient.getAllUsers();
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<Object> delete(@PathVariable Long userId) {
		log.info("DELETE /users/{userId}");
		return userClient.deleteUser(userId);
	}
}
