package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.handler.exception.ForbiddenException;
import ru.practicum.shareit.handler.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemAdvancedDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.FromSizeRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingService bookingService;
    private final ItemRequestService itemRequestService;

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long userId) {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userService.getByIdOrNotFoundError(userId));
        Optional.ofNullable(itemDto.getRequestId()).ifPresent(
                i -> item.setItemRequest(itemRequestService.getByIdOrNotFoundError(i))
        );
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(Long itemId, Long userId, ItemDto itemDto) {
        Item item = getByIdOrNotFoundError(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("forbidden item #" + itemId);
        }
        Optional<String> name = Optional.ofNullable(itemDto.getName());
        if (name.isPresent()) {
            if (!name.get().isBlank()) {
                item.setName(name.get());
            }
        }
        Optional<String> description = Optional.ofNullable(itemDto.getDescription());
        if (description.isPresent()) {
            if (!description.get().isBlank()) {
                item.setDescription(description.get());
            }
        }
        Optional<Boolean> available = Optional.ofNullable(itemDto.getAvailable());
        if (available.isPresent()) {
            item.setAvailable(itemDto.getAvailable());
        }
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemAdvancedDto getById(Long itemId, Long userId) {
        Item item = getByIdOrNotFoundError(itemId);
        LocalDateTime now = LocalDateTime.now();
        boolean isBooker = bookingService.isBookerOfItem(userId, itemId);
        BookingDto lastBooking = (!isBooker) ? bookingService.getLast(item.getId(), now) : null;
        BookingDto nextBooking = (!isBooker) ? bookingService.getNext(item.getId(), now) : null;
        return ItemMapper.toItemAdvancedDto(item, lastBooking, nextBooking);
    }

    @Override
    public List<ItemAdvancedDto> getAllByOwnerId(int from, int size, Long ownerId) {
        Pageable pageable = FromSizeRequest.of(from, size);
        LocalDateTime now = LocalDateTime.now();
        return itemRepository.findByOwner_IdOrderByIdAsc(ownerId, pageable).stream()
                .map(i -> ItemMapper.toItemAdvancedDto(
                        i,
                        bookingService.getLast(i.getId(), now),
                        bookingService.getNext(i.getId(), now)
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findAvailableByNameOrDescription(int from, int size, String text) {
        Pageable pageable = FromSizeRequest.of(from, size);
        return (text.isBlank())
                ? new ArrayList<>()
                : itemRepository.findAvailableByNameOrDescription(text, pageable).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Item getByIdOrNotFoundError(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("not found item #" + itemId));
    }
}
