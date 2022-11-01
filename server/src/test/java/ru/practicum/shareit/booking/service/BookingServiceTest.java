package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingAdvancedDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.handler.exception.BadRequestException;
import ru.practicum.shareit.handler.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class BookingServiceTest {
    private BookingRepository bookingRepository;
    private ItemService itemService;
    private UserService userService;
    private BookingService bookingService;

    private Long id;
    private Map<Long, Object> longObjectMap;
    UserDto owner;
    UserDto booker;
    ItemDto item;
    LocalDateTime start;
    LocalDateTime end;

    @BeforeEach
    void setUp() {
        id = 0L;
        longObjectMap = new HashMap<>();
        userService = Mockito.mock(UserService.class);
        itemService = Mockito.mock(ItemService.class);
        bookingRepository = Mockito.mock(BookingRepository.class);
        Mockito.when(bookingRepository.save(ArgumentMatchers.any()))
                .then(invocation -> {
                    Booking booking = invocation.getArgument(0);
                    if (booking.getId() == null) {
                        booking.setId(++id);
                    }
                    longObjectMap.put(booking.getId(), booking);
                    return booking;
                });
        Mockito.when(bookingRepository.findById(ArgumentMatchers.anyLong()))
                .then(invocation -> {
                    Long id = invocation.getArgument(0);
                    return Optional.ofNullable(longObjectMap.get(id));
                });
        Mockito.when(itemService.create(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .then(invocation -> {
                    ItemDto item = invocation.getArgument(0);
                    if (item.getId() == null) {
                        item.setId(++id);
                    }
                    longObjectMap.put(item.getId(), item);
                    return item;
                });
        Mockito.when(itemService.getByIdOrNotFoundError(ArgumentMatchers.anyLong()))
                .then(invocation -> {
                    Long id = invocation.getArgument(0);
                    Item item = Optional.ofNullable(ItemMapper.toItem((ItemDto) longObjectMap.get(id)))
                            .orElseThrow(() -> new NotFoundException("not found item #" + id));
                    item.setId(id);
                    item.setOwner(UserMapper.toUser(owner));
                    return item;
                });
        Mockito.when(userService.create(ArgumentMatchers.any()))
                .then(invocation -> {
                    UserDto user = invocation.getArgument(0);
                    if (user.getId() == null) {
                        user.setId(++id);
                    }
                    longObjectMap.put(user.getId(), user);
                    return user;
                });
        Mockito.when(userService.getByIdOrNotFoundError(ArgumentMatchers.anyLong()))
                .then(invocation -> {
                    Long id = invocation.getArgument(0);
                    return Optional.ofNullable(UserMapper.toUser((UserDto) longObjectMap.get(id)))
                            .orElseThrow(() -> new NotFoundException("not found user #" + id));
                });
        bookingService = new BookingServiceImpl(
                bookingRepository, userService, itemService
        );
        owner = userService.create(UserDto.builder()
                .name("Item Owner")
                .email("item@owner.org")
                .build());
        booker = userService.create(UserDto.builder()
                .name("Item Booker")
                .email("item@booker.org")
                .build());
        item = itemService.create(ItemDto.builder()
                .name("Portal Gun")
                .description("Gadget that allows to travel")
                .available(true)
                .build(),
                owner.getId());
        start = LocalDateTime.now();
        end = start.plusDays(10);
    }

    @Test
    @DisplayName("Get booking by wrong id")
    void whenGetBookingByWrongId_throw404Error() {
        // Arrange
        Long wrongId = 999L;

        // Act
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.getByIdOrNotFoundError(wrongId),
                "not found booking #" + wrongId
        );

        // Asserts
        assertThat(exception.getMessage())
                .isEqualTo("not found booking #" + wrongId);
    }

    @Test
    @DisplayName("Create new booking")
    void whenCreateNewBookingByDto_returnBookingAdvancedDto() {
        // Arrange
        BookingDto bookingDto = BookingDto.builder()
                .itemId(item.getId())
                .start(start)
                .end(end)
                .build();

        // Act
        BookingAdvancedDto createdBookingDto = bookingService.create(
                bookingDto, booker.getId()
        );

        // Asserts
        assertThat(createdBookingDto).isNotNull();
        assertThat(createdBookingDto.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(createdBookingDto.getStart()).isEqualTo(bookingDto.getStart());
        assertThat(createdBookingDto.getEnd()).isEqualTo(bookingDto.getEnd());
        assertThat(createdBookingDto.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(createdBookingDto.getBooker().getName()).isEqualTo(booker.getName());
        assertThat(createdBookingDto.getBooker().getEmail()).isEqualTo(booker.getEmail());
        assertThat(createdBookingDto.getItem().getId()).isEqualTo(item.getId());
        assertThat(createdBookingDto.getItem().getName()).isEqualTo(item.getName());
        assertThat(createdBookingDto.getItem().getDescription()).isEqualTo(item.getDescription());
        assertThat(createdBookingDto.getItem().getAvailable()).isEqualTo(item.getAvailable());
    }

    @Test
    @DisplayName("Create new booking for not available item")
    void whenCreateNewBookingForNotAvailableItem_throw400Error() {
        // Arrange
        item.setAvailable(false);
        BookingDto bookingDto = BookingDto.builder()
                .itemId(item.getId())
                .start(start)
                .end(end)
                .bookerId(booker.getId())
                .status(BookingStatus.REJECTED)
                .build();
        Mockito.when(userService.getByIdOrNotFoundError(booker.getId()))
                .thenReturn(UserMapper.toUser(booker));

        // Act
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> bookingService.create(bookingDto, booker.getId()),
                "not found unavailable item #" + item.getId()
        );
        // Asserts
        assertThat(exception.getMessage())
                .isEqualTo("not found unavailable item #" + item.getId());
    }

    @Test
    @DisplayName("Create new booking when owner is booker")
    void createBookingWhenOwnerIsBooker_throw404Error() {
        // Arrange
        BookingDto bookingDto = BookingDto.builder()
                .itemId(item.getId())
                .start(start)
                .end(end)
                .bookerId(owner.getId())
                .status(BookingStatus.WAITING)
                .build();
        Mockito.when(userService.getByIdOrNotFoundError(owner.getId()))
                .thenReturn(UserMapper.toUser(owner));

        // Act
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.create(bookingDto, owner.getId()),
                "not found item of user"
        );
        // Asserts
        assertThat(exception.getMessage()).isEqualTo("not found item of user");
    }

    @Test
    @DisplayName("Approve booking")
    void approveBooking() {
        // Arrange
        Item currentItem = ItemMapper.toItem(item);
        currentItem.setOwner(User.builder().id(99L).build());
        Booking booking = Booking.builder()
                .item(currentItem)
                .start(start)
                .end(end)
                .booker(UserMapper.toUser(booker))
                .status(BookingStatus.WAITING)
                .build();
        Mockito.when(bookingRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        // Act
        BookingAdvancedDto bookingDto = bookingService.approve(99L, 1L, true);

        // Asserts
        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    @DisplayName("Get approved but not created booking")
    void getApprovedButNotCreatedBooking() {
        // Arrange
        Mockito.when(bookingRepository.findFirstByItem_IdAndBooker_IdAndStatusOrderByStartAsc(
                    item.getId(), booker.getId(), BookingStatus.APPROVED
                )).thenReturn(Optional.empty());

        // Act
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> bookingService.findApprovedOrNotAvailableError(booker.getId(), item.getId()),
                "has not approved booking"
        );

        // Asserts
        assertThat(exception.getMessage()).isEqualTo("has not approved booking");
    }

    @Test
    @DisplayName("Get approved booking")
    void getApprovedBooking() {
        // Arrange
        Item currentItem = ItemMapper.toItem(item);
        currentItem.setId(88L);
        currentItem.setOwner(User.builder().id(99L).build());
        Booking booking = Booking.builder()
                .id(99L)
                .item(currentItem)
                .start(start)
                .end(end)
                .booker(UserMapper.toUser(booker))
                .status(BookingStatus.APPROVED)
                .build();
        Mockito.when(bookingRepository.findFirstByItem_IdAndBooker_IdAndStatusOrderByStartAsc(
                currentItem.getId(), booker.getId(), BookingStatus.APPROVED
        )).thenReturn(Optional.ofNullable(booking));

        // Act
        Booking returnedBooking = bookingService.findApprovedOrNotAvailableError(booker.getId(), currentItem.getId());

        // Asserts
        assertThat(returnedBooking).isNotNull();
        assertThat(returnedBooking.getId()).isEqualTo(booking.getId());
        assertThat(returnedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    @DisplayName("Rejected booking")
    void rejectedBooking() {
        // Arrange
        Item currentItem = ItemMapper.toItem(item);
        currentItem.setOwner(User.builder().id(99L).build());
        Booking booking = Booking.builder()
                .id(1L)
                .item(currentItem)
                .start(start)
                .end(end)
                .booker(UserMapper.toUser(booker))
                .status(BookingStatus.WAITING)
                .build();
        Mockito.when(bookingRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        // Act
        BookingAdvancedDto bookingDto = bookingService.approve(99L, 1L, false);

        // Asserts
        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    @DisplayName("Approve booking when booking already approved")
    void approveBookingIfAlreadyApproved_throw400Error() {
        // Arrange
        Booking booking = Booking.builder()
                .item(ItemMapper.toItem(item))
                .start(start)
                .end(end)
                .booker(UserMapper.toUser(booker))
                .status(BookingStatus.APPROVED)
                .build();
        Mockito.when(bookingRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        // Act
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> bookingService.approve(owner.getId(), 1L, true),
                "booking already approved/rejected/canceled"
        );

        // Asserts
        assertThat(exception.getMessage()).isEqualTo("booking already approved/rejected/canceled");
    }

    @Test
    @DisplayName("Approve booking when item of other owner")
    void approveBookingWhenItemOfOtherOwner_throw404Error() {
        // Arrange
        Booking booking = Booking.builder()
                .id(10L)
                .item(Item.builder().owner(UserMapper.toUser(booker)).build())
                .start(start)
                .end(end)
                .booker(UserMapper.toUser(booker))
                .status(BookingStatus.WAITING)
                .build();
        Mockito.when(bookingRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        // Act
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.approve(owner.getId(), 10L, true),
                "not found booking #" + booking.getId()
        );

        // Asserts
        assertThat(exception.getMessage()).isEqualTo("not found booking #" + booking.getId());
    }

    @Test
    @DisplayName("Get by owner id")
    void getByOwnerId_returnBookingDto() {
        // Arrange
        Item currentItem = Item.builder()
                .id(item.getId())
                .owner(UserMapper.toUser(owner))
                .build();
        Booking booking = Booking.builder()
                .item(currentItem)
                .start(start)
                .end(end)
                .booker(UserMapper.toUser(booker))
                .status(BookingStatus.WAITING)
                .build();
        BookingAdvancedDto bookingDto = bookingService.create(
                BookingMapper.toBookingDto(booking), booker.getId()
        );

        // Act
        BookingAdvancedDto bookingByOwner = bookingService.getByOwnerId(
                owner.getId(), bookingDto.getId()
        );

        // Asserts
        assertThat(bookingByOwner).isNotNull();
        assertThat(bookingByOwner.getId()).isEqualTo(bookingDto.getId());
    }

    @Test
    void getByOwnerId() {
        // Arrange
        Item currentItem = Item.builder()
                .id(item.getId())
                .owner(UserMapper.toUser(owner))
                .build();
        Booking booking = Booking.builder()
                .item(currentItem)
                .start(start)
                .end(end)
                .booker(UserMapper.toUser(booker))
                .status(BookingStatus.WAITING)
                .build();
        BookingAdvancedDto bookingDto = bookingService.create(
                BookingMapper.toBookingDto(booking), booker.getId()
        );

        // Act
        BookingAdvancedDto bookingByOwner = bookingService.getByOwnerId(
                owner.getId(), bookingDto.getId()
        );

        // Asserts
        assertThat(bookingByOwner).isNotNull();
        assertThat(bookingByOwner.getId()).isEqualTo(bookingDto.getId());
    }
}
