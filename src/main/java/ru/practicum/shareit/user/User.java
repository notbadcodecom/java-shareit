package ru.practicum.shareit.user;

import lombok.*;

@Data
@Builder
public class User {
    Long id;
    String name;
    String email;
}
