package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        if (ex.getBindingResult().getFieldErrors().size() > 0) {
            return ex.getBindingResult().getFieldErrors().stream()
                    .peek(e -> log.info("Validation: {}", e.getDefaultMessage()))
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            e -> (e.getDefaultMessage() == null) ? "Validation error" : e.getDefaultMessage()
                    ));
        }
        Map<String, String> err = new HashMap<>();
        err.put("Validation error", Objects.requireNonNull(ex.getGlobalError()).getDefaultMessage());
        return err;
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ErrorResponse handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.info("Method not allowed: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage().toLowerCase());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse handleServerErrorException(RuntimeException ex) {
        log.info("Server error: {}, {}", ex.getClass(), ex.getMessage());
        return new ErrorResponse("internal server error");
    }
}
