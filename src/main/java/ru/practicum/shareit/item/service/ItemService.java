package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemAdvancedDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto update(Long itemId, Long userId, ItemDto itemDto);

    ItemAdvancedDto getById(Long itemId, Long userId);

    List<ItemAdvancedDto> getAllByOwnerId(Long ownerId);

    List<ItemDto> findAvailableByNameOrDescription(String text);

    Item getByIdOrNotFoundError(Long itemId);
}
