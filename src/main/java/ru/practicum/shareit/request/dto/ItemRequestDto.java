package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@Builder
public class ItemRequestDto {
    private Long id;

    @NotNull(message = "description are required")
    private String description;

    private List<ItemDto> items;

    private LocalDateTime created;
}
