package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.handler.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemAdvancedDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class ItemServiceTest {
    private ItemService itemService;
    private final Long stupidId = 1L;

    @BeforeEach
    void setUp() {
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        Mockito.when(itemRepository.save(ArgumentMatchers.any()))
                .then(invocation -> {
                    Item item = invocation.getArgument(0);
                    if (item.getId() == null) {
                        item.setId(stupidId);
                    }
                    return item;
                });
        Mockito.when(itemRepository.findById(stupidId))
                .thenReturn(Optional.ofNullable(Item.builder()
                        .id(stupidId)
                        .name("Portal Gun")
                        .description("Gadget that allows to travel between universes/dimensions/realities")
                        .available(true)
                        .owner(User.builder().id(stupidId).build())
                        .comments(new ArrayList<>())
                        .build()));
        UserService userService = Mockito.mock(UserService.class);
        Mockito.when(userService.getByIdOrNotFoundError(ArgumentMatchers.anyLong()))
                .thenReturn(User.builder().id(stupidId).build());
        ItemRequestService itemRequestService = Mockito.mock(ItemRequestService.class);
        Mockito.when(itemRequestService.getByIdOrNotFoundError(ArgumentMatchers.anyLong()))
                .thenReturn(ItemRequest.builder().id(stupidId).build());
        itemService = new ItemServiceImpl(
                itemRepository,
                userService,
                Mockito.mock(BookingService.class),
                itemRequestService
        );
    }

    @Test
    @DisplayName("Create new item")
    void whenCreateNewItemByItemDto_returnNewItemDto() {
        // Arrange
        ItemDto itemDto = ItemDto.builder()
                .name("Portal Gun")
                .description("Gadget that allows to travel between universes/dimensions/realities")
                .available(true)
                .requestId(99L)
                .build();

        // Act
        ItemDto returnedItemDto = itemService.create(itemDto, stupidId);

        // Asserts
        assertThat(returnedItemDto).isNotNull();
        assertThat(returnedItemDto.getName()).isEqualTo(itemDto.getName());
        assertThat(returnedItemDto.getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(returnedItemDto.getAvailable()).isEqualTo(itemDto.getAvailable());
        assertThat(returnedItemDto.getRequestId()).isEqualTo(stupidId);
    }

    @Test
    @DisplayName("Update all fields of item")
    void whenUpdateItemByItemDto_returnNewItemDto() {
        // Arrange
        ItemDto itemDto = ItemDto.builder()
                .name("Portal Gun")
                .description("Gadget that allows to travel between universes/dimensions/realities")
                .available(true)
                .build();
        ItemDto createdItemDto = itemService.create(itemDto, stupidId);
        ItemDto newItemDto = ItemDto.builder()
                .id(createdItemDto.getId())
                .name("Mr.Meeseeks Box")
                .description("Gadget that creates a Mr. Meeseeks for the purpose of completing one given objective")
                .available(false)
                .build();

        // Act
        ItemDto updatedItemDto = itemService.update(createdItemDto.getId(), stupidId, newItemDto);

        // Asserts
        assertThat(updatedItemDto).isNotNull();
        assertThat(updatedItemDto.getId()).isEqualTo(newItemDto.getId());
        assertThat(updatedItemDto.getName()).isEqualTo(newItemDto.getName());
        assertThat(updatedItemDto.getDescription()).isEqualTo(newItemDto.getDescription());
        assertThat(updatedItemDto.getAvailable()).isEqualTo(newItemDto.getAvailable());
    }

    @Test
    @DisplayName("Get ItemAdvancedDto of item")
    void whenGetItemById_returnItemAdvancedDto() {
        // Arrange
        ItemDto itemDto = ItemDto.builder()
                .name("Portal Gun")
                .description("Gadget that allows to travel between universes/dimensions/realities")
                .available(true)
                .build();

        // Act
        ItemDto createdItemDto = itemService.create(itemDto, stupidId);
        ItemAdvancedDto itemAdvancedDto = itemService.getById(createdItemDto.getId(), stupidId);

        // Asserts
        assertThat(itemAdvancedDto).isNotNull();
        assertThat(itemAdvancedDto.getId())
                .isEqualTo(createdItemDto.getId());
        assertThat(itemAdvancedDto.getName())
                .isEqualTo(createdItemDto.getName());
        assertThat(itemAdvancedDto.getDescription())
                .isEqualTo(createdItemDto.getDescription());
        assertThat(itemAdvancedDto.getAvailable())
                .isEqualTo(createdItemDto.getAvailable());
        assertThat(itemAdvancedDto)
                .hasFieldOrProperty("lastBooking");
        assertThat(itemAdvancedDto)
                .hasFieldOrProperty("nextBooking");
        assertThat(itemAdvancedDto.getComments())
                .isInstanceOf(List.class);
    }

    @Test
    @DisplayName("Update new item with wrong ID")
    void whenUpdateItemWithWrongId_trow404Error() {
        // Arrange
        ItemDto itemDto = ItemDto.builder()
                .name("Portal Gun")
                .description("Gadget that allows to travel between universes/dimensions/realities")
                .available(true)
                .build();
        Long wrongId = 999L;

        // Act
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.update(wrongId, stupidId, itemDto),
                "not found item #" + wrongId
        );

        // Asserts
        assertThat(exception.getMessage()).isEqualTo("not found item #" + wrongId);
    }
}