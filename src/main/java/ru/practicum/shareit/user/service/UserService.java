package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto getById(Long userId);

    Collection<UserDto> getAll();

    UserDto update(UserDto userDto, Long userId);

    void delete(Long userId);

    User getByIdOrNotFoundError(Long userId);
}
