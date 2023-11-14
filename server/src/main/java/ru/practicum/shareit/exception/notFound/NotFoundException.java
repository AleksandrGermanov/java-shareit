package ru.practicum.shareit.exception.notFound;

import ru.practicum.shareit.exception.ShareItException;

public abstract class NotFoundException extends ShareItException {

    public NotFoundException(String message) {
        super(message);
    }
}
