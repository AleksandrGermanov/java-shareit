package ru.practicum.shareit.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.service.BookingState;

public class BookingStateConverterTest {
    private final BookingStateConverter converter = new BookingStateConverter();

    @Test
    public void methodConvertConvertsToExistingState() {
        BookingState state = converter.convert("aLl");
        Assertions.assertEquals(BookingState.ALL, state);
    }

    @Test
    public void methodConvertConvertsToNotExistingState() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> converter.convert("unsupported"));
    }
}
