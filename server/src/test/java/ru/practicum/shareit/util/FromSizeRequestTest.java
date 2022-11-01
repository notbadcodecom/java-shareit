package ru.practicum.shareit.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.handler.exception.BadRequestException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FromSizeRequestTest {

    @Test
    @DisplayName("Create new pageable")
    void createPageable() {
       // Arrange, Act
       Pageable pageable = FromSizeRequest.of(0, 10);
        // Asserts
       assertThat(pageable.getOffset()).isEqualTo(0);
    }

    @Test
    @DisplayName("Create pageable with not positive values")
    void createPageableWithNotPositiveFrom_throw400Error() {
        // Arrange, Act
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> FromSizeRequest.of(-1, 10),
                "not positive value in pagination"
        );

        // Asserts
        assertThat(exception.getMessage()).isEqualTo("not positive value in pagination");
    }

    @Test
    @DisplayName("Create pageable with not positive values")
    void createPageableWithNotPositiveSize_throw400Error() {
        // Arrange, Act
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> FromSizeRequest.of(0, 0),
                "not positive value in pagination"
        );

        // Asserts
        assertThat(exception.getMessage()).isEqualTo("not positive value in pagination");
    }
}