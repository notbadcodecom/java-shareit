package ru.practicum.shareit.booking.validation;

import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EndLessThanStartValidator implements ConstraintValidator<EndLessThanStartValidation, BookingDto> {

    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext context) {
        return bookingDto.getStart().isBefore((bookingDto).getEnd());
    }
}
