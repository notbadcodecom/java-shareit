package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enumeration.BookingStatus;
import ru.practicum.shareit.item.dto.ItemAdvancedDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {
    Item item;
    User owner;
    ItemRequest request;
    User requester;
    BookingDto lastBooking;
    BookingDto nextBooking;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .name("Owner")
                .email("owner@mail.com")
                .build();
        requester = User.builder()
                .name("Requester")
                .email("requester@mail.com")
                .build();
        request = ItemRequest
                .builder()
                .id(3L)
                .description("text")
                .requester(requester)
                .created(LocalDateTime.now())
                .build();
        item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .owner(owner)
                .available(true)
                .itemRequest(request)
                .comments(new ArrayList<>())
                .build();
        lastBooking = BookingDto.builder()
                .id(1L)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusDays(10))
                .end(LocalDateTime.now().minusDays(5))
                .build();
        nextBooking = BookingDto.builder()
                .id(1L)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(5))
                .build();
    }

    @Test
    @DisplayName("Item to itemDto mapping")
    void toItemDto() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        assertThat(itemDto).isNotNull();
        assertThat(itemDto.getId()).isEqualTo(item.getId());
        assertThat(itemDto.getName()).isEqualTo(item.getName());
        assertThat(itemDto.getDescription()).isEqualTo(item.getDescription());
        assertThat(itemDto.getRequestId()).isEqualTo(item.getItemRequest().getId());
    }

    @Test
    void toItemBookingDto() {
        ItemAdvancedDto itemDto = ItemMapper.toItemAdvancedDto(item, lastBooking, nextBooking);
        assertThat(itemDto).isNotNull();
        assertThat(itemDto.getId()).isEqualTo(item.getId());
        assertThat(itemDto.getName()).isEqualTo(item.getName());
        assertThat(itemDto.getDescription()).isEqualTo(item.getDescription());
        assertThat(itemDto.getRequestId()).isEqualTo(item.getItemRequest().getId());
        assertThat(itemDto.getComments()).isNotNull();
        assertThat(itemDto.getLastBooking()).isNotNull();
        assertThat(itemDto.getNextBooking()).isNotNull();
    }
}