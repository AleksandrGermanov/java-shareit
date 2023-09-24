package ru.practicum.shareit.exception;

public class ShareItException extends RuntimeException {

    public ShareItException(String message) {
        super(message);
    }

    public ShareItException(Throwable cause) {
        super(cause);
    }

    public ShareItException(String message, Throwable cause) {
        super(message, cause);
    }
}
