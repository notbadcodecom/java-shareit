package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.handler.exception.ForbiddenException;
import ru.practicum.shareit.handler.exception.BadRequestException;
import ru.practicum.shareit.handler.exception.NotFoundException;
import ru.practicum.shareit.handler.exception.UnsupportedStatusException;

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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ErrorResponse handleBadRequestException(BadRequestException ex) {
        log.info("Bad request: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UnsupportedStatusException.class)
    public ErrorResponse handleUnsupportedStatusException(UnsupportedStatusException ex) {
        log.info("Unsupported status: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException.class)
    public ErrorResponse handleForbiddenException(ForbiddenException ex) {
        log.info("Forbidden: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNoSuchElementFoundException(NotFoundException ex) {
        log.info("Not found: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ErrorResponse handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.info("Method not allowed: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage().toLowerCase());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.info("Data integrity violation: {}", ex.getMessage());
        return new ErrorResponse("data integrity violation");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse handleServerErrorException(RuntimeException ex) {
        log.info("Server error: {}, {}", ex.getClass(), ex.getMessage());
        return new ErrorResponse("internal server error");
    }
}
