package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.enumeration.*;
import ru.practicum.shareit.handler.exception.BadRequestException;
import ru.practicum.shareit.handler.exception.NotFoundException;
import ru.practicum.shareit.handler.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public Booking getBookingOrNotFoundError(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("not found booking #" + bookingId));
    }

    @Override
    public BookingAdvancedDto create(BookingDto bookingDto, Long bookerId) {
        Optional<Item> itemOptional = itemRepository.findById(bookingDto.getItemId());
        if (itemOptional.isEmpty()) {
            throw new NotFoundException("not found item #" + bookingDto.getItemId());
        } else if (!itemOptional.get().getAvailable()) {
            throw new BadRequestException("not found unavailable item #" + bookingDto.getItemId());
        }
        User booker = userService.getByIdOrNotFoundError(bookerId);
        if (itemOptional.get().getOwner().getId().equals(bookerId)) {
            throw new NotFoundException("not found item of user");
        }
        Booking booking = BookingMapper.toBooking(
                bookingDto,
                itemOptional.get(),
                booker,
                BookingStatus.WAITING
        );
        return BookingMapper.toBookingAdvancedDto(bookingRepository.save(booking));
    }

    @Override
    public BookingAdvancedDto approve(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = getBookingOrNotFoundError(bookingId);
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BadRequestException("booking already approved");
        }
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("not found booking #" + bookingId);
        }
        booking.setStatus((approved) ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        bookingRepository.save(booking);
        return BookingMapper.toBookingAdvancedDto(booking);
    }

    @Override
    public BookingAdvancedDto getByOwnerId(Long ownerId, Long bookingId) {
        Booking booking = getBookingOrNotFoundError(bookingId);
        Long itemOwnerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();
        if (!(ownerId.equals(itemOwnerId)) && !(bookerId.equals(ownerId))) {
            throw new NotFoundException("not found booking #" + bookingId);
        }
        return BookingMapper.toBookingAdvancedDto(booking);
    }

    @Override
    public BookingDto getLast(Long itemId, LocalDateTime now) {
        Optional<Booking> bookingOptional = bookingRepository
                .findFirstByItem_IdAndEndLessThanEqualOrderByEndDesc(itemId, now);
        return bookingOptional.map(BookingMapper::toBookingDto).orElse(null);
    }

    @Override
    public BookingDto getNext(Long itemId, LocalDateTime now) {
        Optional<Booking> bookingOptional = bookingRepository
                .findFirstByItem_IdAndEndGreaterThanEqualOrderByEndDesc(itemId, now);
        return bookingOptional.map(BookingMapper::toBookingDto).orElse(null);
    }

    @Override
    public List<BookingAdvancedDto> getAllOfBookerByState(Long bookerId, String stateText) {
        userService.getByIdOrNotFoundError(bookerId);
        BookingState state = stateFromString(stateText);
        List<Booking> booking;
        switch (state) {
            case ALL:
                booking = bookingRepository.findByBooker_IdOrderByEndDesc(bookerId);
                break;
            case CURRENT:
                booking = bookingRepository.findAllByBookerIdCurrent(bookerId, LocalDateTime.now());
                break;
            case PAST:
                booking = bookingRepository.findByBooker_IdAndEndLessThanOrderByEndDesc(
                        bookerId, LocalDateTime.now()
                );
                break;
            case FUTURE:
                booking = bookingRepository.findByBooker_IdAndStartGreaterThanOrderByEndDesc(
                        bookerId, LocalDateTime.now()
                );
                break;
            case UNSUPPORTED:
                throw new UnsupportedStatusException("Unknown state: " + stateText);
            default:
                booking = bookingRepository.findByBooker_IdAndStatusOrderByEndDesc(
                        bookerId, BookingStatus.valueOf(state.name())
                );
        }
        return booking.stream().map(BookingMapper::toBookingAdvancedDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingAdvancedDto> getAllOfOwnerByState(Long ownerId, String stateText) {
        userService.getByIdOrNotFoundError(ownerId);
        BookingState state = stateFromString(stateText);
        List<Booking> booking;
        switch (state) {
            case ALL:
                booking = bookingRepository.findByItem_Owner_IdOrderByEndDesc(ownerId);
                break;
            case CURRENT:
                booking = bookingRepository.findAllByOwnerIdCurrent(ownerId, LocalDateTime.now());
                break;
            case PAST:
                booking = bookingRepository.findByItem_Owner_IdAndEndLessThanOrderByEndDesc(
                        ownerId, LocalDateTime.now()
                );
                break;
            case FUTURE:
                booking = bookingRepository.findByItem_Owner_IdAndStartGreaterThanOrderByEndDesc(
                        ownerId, LocalDateTime.now()
                );
                break;
            case UNSUPPORTED:
                throw new UnsupportedStatusException("Unknown state: " + stateText);
            default:
                booking = bookingRepository.findByItem_Owner_IdAndStatusOrderByEndDesc(
                        ownerId, BookingStatus.valueOf(state.name())
                );
        }
        return booking.stream().map(BookingMapper::toBookingAdvancedDto).collect(Collectors.toList());
    }

    @Override
    public boolean isBookerOfItem(Long bookerId, Long itemId) {
        return bookingRepository.existsByItem_IdAndBooker_Id(itemId, bookerId);
    }

    @Override
    public Booking findApprovedOrNotAvailableError(Long bookerId, Long itemId) {
        return bookingRepository.findFirstByItem_IdAndBooker_IdAndStatusOrderByStartAsc(
                itemId, bookerId, BookingStatus.APPROVED
        ).orElseThrow(() -> new BadRequestException("has not approved booking"));
    }

    @Override
    public BookingState stateFromString(String stateText) {
        for (BookingState s : BookingState.values()) {
            if (s.name().equalsIgnoreCase(stateText)) {
                return BookingState.valueOf(stateText.toUpperCase());
            }
        }
        return BookingState.UNSUPPORTED;
    }
}
