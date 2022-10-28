package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.util.Create;
import ru.practicum.shareit.util.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
public class UserDto {
    Long id;

    @NotNull(message = "name/login is required", groups = {Create.class})
    @NotBlank(message = "name/login is required", groups = {Create.class})
    String name;

    @NotNull(message = "email is required", groups = {Create.class})
    @Email(message = "email is invalid", groups = {Create.class, Update.class})
    String email;
}
