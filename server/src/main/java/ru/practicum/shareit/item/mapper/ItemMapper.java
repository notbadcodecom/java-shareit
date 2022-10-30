package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemAdvancedDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;
import java.util.stream.Collectors;

@UtilityClass
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(Optional.ofNullable(item.getItemRequest()).isPresent()
                                ? item.getItemRequest().getId()
                                : null)
                .build();
    }

    public static ItemAdvancedDto toItemAdvancedDto(
            Item item, BookingDto lastBooking, BookingDto nextBooking
    ) {
        return ItemAdvancedDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(item.getComments().stream()
                                .map(CommentMapper::toCommentDto)
                                .collect(Collectors.toList()))
                .requestId(Optional.ofNullable(item.getItemRequest()).isPresent()
                        ? item.getItemRequest().getId()
                        : null)
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }
}
