package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestDto itemRequestDto, Long requesterId);

    List<ItemRequestDto> getItemRequestsByRequesterId(Long requesterId);

    List<ItemRequestDto> getAllRequests(int from, int size, Long requesterId);

    ItemRequestDto getRequestById(Long requestId, Long requesterId);

    ItemRequest getByIdOrNotFoundError(Long requestId);
}
