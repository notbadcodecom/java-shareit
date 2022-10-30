package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingAdvancedDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    Booking getByIdOrNotFoundError(Long bookingId);

    BookingAdvancedDto create(BookingDto bookingDto, Long bookerId);

    BookingAdvancedDto approve(Long ownerId, Long bookingId, boolean approved);

    BookingAdvancedDto getByOwnerId(Long ownerId, Long bookingId);

    BookingDto getLast(Long itemId, LocalDateTime now);

    BookingDto getNext(Long itemId, LocalDateTime now);

    List<BookingAdvancedDto> getAllOfBookerByState(int from, int size, Long bookerId, String stateText);

    List<BookingAdvancedDto> getAllOfOwnerByState(int from, int size, Long ownerId, String stateText);

    boolean isBookerOfItem(Long bookerId, Long itemId);

    Booking findApprovedOrNotAvailableError(Long bookerId, Long itemId);
}
