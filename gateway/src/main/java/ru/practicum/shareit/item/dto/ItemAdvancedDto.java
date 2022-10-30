package ru.practicum.shareit.item.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;


@Getter
public class ItemAdvancedDto {
    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "description is required")
    private String description;

    @NotNull(message = "availability is required")
    private Boolean available;

    @Positive(message = "request is should be positive")
    private Long requestId;
}
