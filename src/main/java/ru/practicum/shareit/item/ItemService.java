package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemDto create(ItemDto itemDto, Long userId) {
        Item item = ItemMapper.toItem(itemDto);
        Optional<User> ownerOptional = userRepository.getById(userId);
        if (ownerOptional.isEmpty()) {
            throw new NotFoundException("user not found");
        }
        item.setOwner(ownerOptional.get());
        return ItemMapper.toItemDto(itemRepository.add(item));
    }

    public ItemDto update(Long itemId, Long userId, ItemDto itemDto) {
        Item item = getByIdOrNotFoundException(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("item belongs to other user");
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
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        itemRepository.add(item);
        return ItemMapper.toItemDto(item);
    }

    public ItemDto getById(Long itemId) {
        return ItemMapper.toItemDto(getByIdOrNotFoundException(itemId));
    }

    public List<ItemDto> getByOwnerId(Long ownerId) {
        return itemRepository.getAll().stream()
                .filter(i -> Objects.equals(i.getOwner().getId(), ownerId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> getAvailableByNameOrDescription(String text) {
        if (text.isBlank()) return new ArrayList<>();
        return itemRepository.getAll().stream()
                .filter(i -> i.getName().toLowerCase().contains(text.toLowerCase())
                        || i.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(i -> i.getAvailable().equals(Boolean.TRUE))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private Item getByIdOrNotFoundException(Long itemId) {
        Optional<Item> itemOptional = itemRepository.getById(itemId);
        if (itemOptional.isEmpty()) {
            throw new NotFoundException("item by id " + itemId);
        }
        return itemOptional.get();
    }
}
