package ru.practicum.shareit.booking;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "bookings", schema = "public")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime start;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime end;

    @CreationTimestamp
    @Column(name = "created", updatable = false)
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "booker_id", referencedColumnName = "id")
    private User booker;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}
