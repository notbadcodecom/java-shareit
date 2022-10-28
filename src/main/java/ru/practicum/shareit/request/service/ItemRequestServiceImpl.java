package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.handler.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.FromSizeRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;

    @Override
    @Transactional
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long requesterId) {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequester(userService.getByIdOrNotFoundError(requesterId));
        return ItemRequestMapper.toItemRequestDto(
                itemRequestRepository.save(itemRequest)
        );
    }

    @Override
    public List<ItemRequestDto> getItemRequestsByRequesterId(Long requesterId) {
        userService.getByIdOrNotFoundError(requesterId);
        return itemRequestRepository.findByRequester_IdOrderByCreatedDesc(requesterId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(int from, int size, Long requesterId) {
        Pageable pageable = FromSizeRequest.of(from, size);
        return itemRequestRepository
                .findByRequester_IdNotOrderByCreatedDesc(requesterId, pageable)
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long requestId, Long requesterId) {
        userService.getByIdOrNotFoundError(requesterId);
        return ItemRequestMapper.toItemRequestDto(getByIdOrNotFoundError(requestId));
    }

    @Override
    public ItemRequest getByIdOrNotFoundError(Long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("not found request #" + requestId));
    }
}
