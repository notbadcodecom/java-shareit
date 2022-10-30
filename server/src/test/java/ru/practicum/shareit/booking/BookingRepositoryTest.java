package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.util.FromSizeRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase
class BookingRepositoryTest {
    BookingRepository bookingRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;
    User booker;
    User owner;
    Item item;

    @Autowired
    public BookingRepositoryTest(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            ItemRepository itemRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @BeforeEach
    void setUp() {
        booker = userRepository.save(User.builder()
                .name("Booker")
                .email("booker@email.com")
                .build());
        owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@email.com")
                .build());
        item = itemRepository.save(Item.builder()
                .name("Item")
                .available(true)
                .description("description")
                .comments(new ArrayList<>())
                .owner(owner)
                .build());
    }

    @Test
    @DisplayName("Find all bookings by booker id current")
    void findAllByBookerIdCurrent() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().minusDays(10);
        LocalDateTime end = LocalDateTime.now().plusDays(10);
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .start(start)
                .end(end)
                .status(BookingStatus.APPROVED)
                .item(item)
                .build());
        start = LocalDateTime.now().plusDays(1);
        bookingRepository.save(Booking.builder()
                .booker(booker)
                .start(start)
                .end(end)
                .status(BookingStatus.APPROVED)
                .item(item)
                .build());

        // Act
        List<Booking> testingBooking = bookingRepository.findAllByBookerIdCurrent(
                booker.getId(), LocalDateTime.now(), FromSizeRequest.of(0, 20)
        );

        // Asserts
        assertThat(testingBooking).isNotNull();
        assertThat(testingBooking.size()).isEqualTo(1);
        assertThat(testingBooking.get(0)).isEqualTo(booking);
    }

    @Test
    @DisplayName("Find all bookings by owner id current")
    void findAllByOwnerIdCurrent() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().minusDays(10);
        LocalDateTime end = LocalDateTime.now().plusDays(10);
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .start(start)
                .end(end)
                .status(BookingStatus.APPROVED)
                .item(item)
                .build());
        start = LocalDateTime.now().plusDays(1);
        bookingRepository.save(Booking.builder()
                .booker(booker)
                .start(start)
                .end(end)
                .status(BookingStatus.APPROVED)
                .item(item)
                .build());

        // Act
        List<Booking> testingBooking = bookingRepository.findAllByOwnerIdCurrent(
                owner.getId(), LocalDateTime.now(), FromSizeRequest.of(0, 20)
        );

        // Asserts
        assertThat(testingBooking).isNotNull();
        assertThat(testingBooking.size()).isEqualTo(1);
        assertThat(testingBooking.get(0)).isEqualTo(booking);
    }
}
