package ru.practicum.shareit.exception.notFound;

public class RequestNotFoundException extends NotFoundException {
    public RequestNotFoundException(String message) {
        super(message);
    }
}
