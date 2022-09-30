package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ExistEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDto create(UserDto userDto) {
        ifEmailExistThrowException(userDto.getEmail());
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.add(user));
    }

    public UserDto getById(Long userId) {
        return UserMapper.toUserDto(getByIdOrNotFoundException(userId));
    }

    public Collection<UserDto> getAll() {
        return userRepository.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto update(UserDto userDto, Long userId) {
        User user = getByIdOrNotFoundException(userId);
        if (!user.getEmail().equals(userDto.getEmail())) {
            ifEmailExistThrowException(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        userRepository.add(user);
        return UserMapper.toUserDto(user);
    }

    public void delete(Long userId) {
        userRepository.delete(userId);
    }

    private void ifEmailExistThrowException(String email) {
        if (userRepository.isEmailExist(email)) {
            throw new ExistEmailException(email);
        }
    }

    private User getByIdOrNotFoundException(Long userId) {
        Optional<User> user = userRepository.getById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("user by id " + userId);
        }
        return user.get();
    }
}
