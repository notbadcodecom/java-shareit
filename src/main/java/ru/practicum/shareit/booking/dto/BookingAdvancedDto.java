package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.booking.enumeration.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Getter
@Builder
public class BookingAdvancedDto {
    private Long id;

    private BookingStatus status;

    private LocalDateTime start;

    private LocalDateTime end;

    private UserDto booker;

    private ItemDto item;
}
