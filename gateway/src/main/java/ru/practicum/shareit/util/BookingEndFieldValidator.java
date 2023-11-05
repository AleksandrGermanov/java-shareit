package ru.practicum.shareit.util;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class BookingEndFieldValidator implements ConstraintValidator<EndIsAfterStart, BookItemRequestDto> {
    @Override
    public boolean isValid(BookItemRequestDto bookItemRequestDto,
                           ConstraintValidatorContext constraintValidatorContext) {
        return bookItemRequestDto.getEnd().isAfter(bookItemRequestDto.getStart());
    }
}
