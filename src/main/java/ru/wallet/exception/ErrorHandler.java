package ru.wallet.exception;

import org.hibernate.engine.internal.Collections;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandler {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleNotFound(NotFoundException e) {
        return ErrorDto.builder()
                .status("NOT_FOUND")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleBadRequest(BadRequestException e) {
        return ErrorDto.builder()
                .status("BAD_REQUEST")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleValidationExceptions(MethodArgumentNotValidException e) {
        String message = e.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ErrorDto.builder()
                .status("BAD_REQUEST")
                .message(message)
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }
}
