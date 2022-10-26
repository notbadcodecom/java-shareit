package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.booking.enumeration.BookingStatus;
import ru.practicum.shareit.booking.validation.EndLessThanStartValidation;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Builder
@EndLessThanStartValidation
public class BookingDto {
    private Long id;

    private BookingStatus status;

    @NotNull(message = "start booking is required")
    @FutureOrPresent(message = "start cannot be in the past")
    private LocalDateTime start;

    @NotNull(message = "end booking is required")
    @FutureOrPresent(message = "end cannot be in the past")
    private LocalDateTime end;

    @NotNull(message = "item id is required")
    private Long itemId;

    private Long bookerId;
}
