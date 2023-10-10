package ru.practicum.shareit.exception.mismatch;

import ru.practicum.shareit.exception.ShareItException;

public abstract class MismatchException extends ShareItException {
    public MismatchException(String message) {
        super(message);
    }
}
