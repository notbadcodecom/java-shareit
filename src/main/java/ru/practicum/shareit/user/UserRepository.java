package ru.practicum.shareit.user;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    User add(User user);

    Optional<User> getById(Long userId);

    void delete(Long userId);

    Collection<User> getAll();

    boolean isEmailExist(String email);
}
