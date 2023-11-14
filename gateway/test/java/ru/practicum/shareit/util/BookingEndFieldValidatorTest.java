package ru.practicum.shareit.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class BookingEndFieldValidatorTest {
    private final BookingEndFieldValidator validator = new BookingEndFieldValidator();
    @Mock
    ConstraintValidatorContext context;

    @Test
    public void methodValidateWhenEndIsAfterStartReturnsTrue() {
        BookItemRequestDto dto = new BookItemRequestDto(0L, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        Assertions.assertTrue(validator.isValid(dto, context));
    }

    @Test
    public void methodValidateWhenEndIsNotAfterStartReturnsFalse() {
        BookItemRequestDto dto = new BookItemRequestDto(0L,
                LocalDateTime.of(2023, 1, 1, 1, 1),
                LocalDateTime.of(2023, 1, 1, 1, 1));

        Assertions.assertFalse(validator.isValid(dto, context));
    }
}
