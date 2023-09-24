package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.alreadyExists.AlreadyExistsException;
import ru.practicum.shareit.exception.alreadyExists.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.notFound.NotFoundException;

import javax.validation.ConstraintViolationException;

import static ru.practicum.shareit.Util.Logging.logWarnException;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {
    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<String> handleNotFound(NotFoundException e) {
        logWarnException(log, e);
        return new ResponseEntity<>('\"' + e.getMessage() + '\"', HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({AlreadyExistsException.class})
    public ResponseEntity<String> handleBadRequest(AlreadyExistsException e) {
        logWarnException(log, e);
        return new ResponseEntity<>('\"' + e.getMessage() + '\"', HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({EmailAlreadyExistsException.class})
    public ResponseEntity<String> handleBadRequest(EmailAlreadyExistsException e) {
        logWarnException(log, e);
        return new ResponseEntity<>('\"' + e.getMessage() + '\"', HttpStatus.CONFLICT);
    }

    @ExceptionHandler({OwnerMismatchException.class})
    public ResponseEntity<String> handleBadRequest(OwnerMismatchException e) {
        logWarnException(log, e);
        return new ResponseEntity<>('\"' + e.getMessage() + '\"', HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException e) {
        logWarnException(log, e);
        StringBuilder message = new StringBuilder();
        message.append("Неправильный формат переданных данных: ");
        e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath().toString()
                        + ": " + violation.getMessage() + " ")
                .forEach(message::append);
        String formedMessage = message.toString();
        return new ResponseEntity<>('\"' + formedMessage + '\"', HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<String> handleBadRequest(Exception e) {
        logWarnException(log, e);
        return new ResponseEntity<>('\"' + e.getMessage() + '\"', HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
