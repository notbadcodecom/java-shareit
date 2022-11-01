package ru.practicum.shareit.booking.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = EndLessThanStartValidator.class)
public @interface EndLessThanStartValidation {
    String message() default "end cannot be after the start";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
