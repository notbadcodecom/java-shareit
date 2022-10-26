package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.enumeration.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_IdOrderByEndDesc(Long id, Pageable pageable);

    List<Booking> findByBooker_IdAndEndLessThanOrderByEndDesc(Long id, LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = :id and :now between b.start and b.end " +
            "order by b.end DESC")
    List<Booking> findAllByBookerIdCurrent(@Param("id") Long id, @Param("now") LocalDateTime now, Pageable pageable);

    List<Booking> findByBooker_IdAndStartGreaterThanOrderByEndDesc(Long id, LocalDateTime now, Pageable pageable);

    List<Booking> findByBooker_IdAndStatusOrderByEndDesc(Long id, BookingStatus status, Pageable pageable);

    List<Booking> findByItem_Owner_IdOrderByEndDesc(Long id, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndEndLessThanOrderByEndDesc(Long id, LocalDateTime end, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner.id  = :id and :now between b.start and b.end " +
            "order by b.end DESC")
    List<Booking> findAllByOwnerIdCurrent(@Param("id") Long id, @Param("now") LocalDateTime now, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStartGreaterThanOrderByEndDesc(Long id, LocalDateTime now, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStatusOrderByEndDesc(Long id, BookingStatus status, Pageable pageable);

    Optional<Booking> findFirstByItem_IdAndEndLessThanEqualOrderByEndDesc(
            Long id, LocalDateTime now
    );

    Optional<Booking> findFirstByItem_IdAndEndGreaterThanEqualOrderByEndDesc(
            Long id, LocalDateTime now
    );

    boolean existsByItem_IdAndBooker_Id(Long itemId, Long bookerId);

    Optional<Booking> findFirstByItem_IdAndBooker_IdAndStatusOrderByStartAsc(
            Long itemId, Long bookerId, BookingStatus status
    );
}
