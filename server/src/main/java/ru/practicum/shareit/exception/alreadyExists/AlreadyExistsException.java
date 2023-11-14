package ru.practicum.shareit.exception.alreadyExists;

import ru.practicum.shareit.exception.ShareItException;

public abstract class AlreadyExistsException extends ShareItException {
    public AlreadyExistsException(String message) {
        super(message);
    }
}

