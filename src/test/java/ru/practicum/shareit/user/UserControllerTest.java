package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class UserControllerTest {
    UserController userController;
    UserService userService;
    UserDto userDto;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        userController = new UserController(userService);
        userDto = UserDto.builder()
                .name("User")
                .email("user@mail.com")
                .build();
    }

    @Test
    @DisplayName("Create user")
    void create() {
        // Assign
        Mockito.when(userService.create(ArgumentMatchers.any(UserDto.class)))
                .then(invocation -> {
                    UserDto user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

        // Act
        UserDto testedDto = userService.create(userDto);

        // Asserts
        assertThat(testedDto).isNotNull();
        assertThat(testedDto.getId()).isNotNull();
        assertThat(testedDto.getName()).isEqualTo(userDto.getName());
        assertThat(testedDto.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    @DisplayName("Update user")
    void update() {
        // Assign
        Mockito.when(userService.update(ArgumentMatchers.any(UserDto.class), ArgumentMatchers.anyLong()))
                .then(invocation -> {
                    UserDto user = invocation.getArgument(0);
                    Long userId = invocation.getArgument(1);
                    user.setId(userId);
                    return user;
                });
        userService.create(userDto);
        UserDto updateDto = UserDto.builder()
                .name("New user")
                .email("new-user@mail.com")
                .build();

        // Act
        UserDto testedDto = userService.update(updateDto, 1L);

        // Asserts
        assertThat(testedDto).isNotNull();
        assertThat(testedDto.getId()).isNotNull();
        assertThat(testedDto.getName()).isNotEqualTo(userDto.getName());
        assertThat(testedDto.getName()).isEqualTo(updateDto.getName());
        assertThat(testedDto.getEmail()).isNotEqualTo(userDto.getEmail());
        assertThat(testedDto.getEmail()).isEqualTo(updateDto.getEmail());
    }

    @Test
    @DisplayName("Get user by id")
    void getById() {
        // Assign
        Mockito.when(userService.getById(ArgumentMatchers.anyLong()))
                .then(invocation -> {
                    Long userId = invocation.getArgument(0);
                    userDto.setId(userId);
                    return userDto;
                });

        // Act
        UserDto testedDto = userService.getById(1L);

        // Asserts
        assertThat(testedDto).isNotNull();
        assertThat(testedDto.getId()).isNotNull();
        assertThat(testedDto.getName()).isNotNull();
        assertThat(testedDto.getEmail()).isNotNull();
    }

    @Test
    @DisplayName("Get all users")
    void getAll() {
        // Assign
        Mockito.when(userService.getAll())
                .then(invocation -> {
                    userDto.setId(1L);
                    return Collections.singletonList(userDto);
                });

        // Act
        Collection<UserDto> testedDtoList = userService.getAll();

        // Asserts
        assertThat(testedDtoList).isNotNull();
        assertThat(testedDtoList.size()).isNotNull();
    }

    @Test
    @DisplayName("Delete user by id")
    void delete() {
        userService.delete(1L);
        Mockito.verify(userService, Mockito.times(1)).delete(1L);
    }
}