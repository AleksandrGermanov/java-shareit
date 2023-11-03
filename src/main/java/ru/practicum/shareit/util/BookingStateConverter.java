package ru.practicum.shareit.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.service.BookingState;

@Component
public class BookingStateConverter implements Converter<String, BookingState> {
    @Override
    public BookingState convert(String source) {
        return BookingState.valueOf(source.toUpperCase());
    }
}
