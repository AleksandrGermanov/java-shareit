package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.alreadyExists.AlreadyExistsException;
import ru.practicum.shareit.exception.alreadyExists.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.mismatch.ItemOwnerOrBookerMismatchException;
import ru.practicum.shareit.exception.mismatch.MismatchException;
import ru.practicum.shareit.exception.mismatch.OwnerMismatchException;
import ru.practicum.shareit.exception.mismatch.TimeMismatchException;
import ru.practicum.shareit.exception.notFound.BookingForCommentNotFoundException;
import ru.practicum.shareit.exception.notFound.NotFoundException;

import java.util.Arrays;

import static ru.practicum.shareit.util.Logging.logDebugException;
import static ru.practicum.shareit.util.Logging.logWarnException;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {
    @ExceptionHandler({NotFoundException.class, OwnerMismatchException.class, ItemOwnerOrBookerMismatchException.class})
    public ResponseEntity<String> handleNotFound(ShareItException e) {
        logDebugException(log, e);
        return new ResponseEntity<>('\"' + e.getMessage() + '\"', HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({AlreadyExistsException.class, TimeMismatchException.class, ItemNotAvailableException.class,
            BookingForCommentNotFoundException.class})
    public ResponseEntity<String> handleBadRequest(ShareItException e) {
        logDebugException(log, e);
        return new ResponseEntity<>('\"' + e.getMessage() + '\"', HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({EmailAlreadyExistsException.class})
    public ResponseEntity<String> handleEmailAlreadyExists(EmailAlreadyExistsException e) {
        logDebugException(log, e);
        return new ResponseEntity<>('\"' + e.getMessage() + '\"', HttpStatus.CONFLICT);
    }

    @ExceptionHandler({MismatchException.class})
    public ResponseEntity<String> handleForbidden(MismatchException e) {
        logDebugException(log, e);
        return new ResponseEntity<>('\"' + e.getMessage() + '\"', HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<String> handleUnexpected(Exception e) {
        logWarnException(log, e);
        log.warn(Arrays.toString(e.getStackTrace()));
        return new ResponseEntity<>("Произошла непредвиденная ошибка " + e.getClass()
                + " с сообщением \"" + e.getMessage() + '\"', HttpStatus.INTERNAL_SERVER_ERROR);
    }
}