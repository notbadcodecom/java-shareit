package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;


@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    @NotBlank(message = "name is required")
    String name;

    @NotBlank(message = "description is required")
    String description;

    @NotNull(message = "availability is required")
    Boolean available;

    @Positive(message = "request should be positive")
    Long requestId;
}
