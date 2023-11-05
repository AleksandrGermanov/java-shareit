package ru.practicum.shareit.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Arrays;

import static ru.practicum.shareit.util.Logging.logDebugException;
import static ru.practicum.shareit.util.Logging.logWarnException;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler({UnknownStateException.class})
    public ResponseEntity<String> handleBadRequestForUnsupportedStatus(UnknownStateException e) {
        logDebugException(log, e);
        return new ResponseEntity<>("{\"error\" : \"" + e.getMessage() + "\"}", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException e) {
        logDebugException(log, e);
        StringBuilder message = new StringBuilder();
        message.append("Неправильный формат переданных данных: ");
        e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath().toString()
                        + ": " + violation.getMessage() + " ")
                .forEach(message::append);
        String formedMessage = message.toString();
        return new ResponseEntity<>('\"' + formedMessage + '\"', HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleArgumentNotValid(MethodArgumentNotValidException e) {
        logDebugException(log, e);
        StringBuilder message = new StringBuilder();
        message.append("Неправильный формат переданных данных: ");
        e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + " (" + fieldError.getRejectedValue() + ") "
                        + ": " + fieldError.getDefaultMessage() + " ")
                .forEach(message::append);
        String formedMessage = message.toString();
        return new ResponseEntity<>('\"' + formedMessage + '\"', HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler({Exception.class})
    public ResponseEntity<String> handleUnexpected(Exception e) {
        logWarnException(log, e);
        log.warn(Arrays.toString(e.getStackTrace()));
        return new ResponseEntity<>("Произошла непредвиденная ошибка " + e.getClass()
                + " с сообщением \"" + e.getMessage() + '\"', HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

