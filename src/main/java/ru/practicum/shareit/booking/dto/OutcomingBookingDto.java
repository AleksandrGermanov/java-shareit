package ru.practicum.shareit.booking.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class OutcomingBookingDto extends BookingDto {
    ItemDto item;
    UserDto booker;

    public OutcomingBookingDto(long id, LocalDateTime start, LocalDateTime end, BookingStatus status,
                               ItemDto item, UserDto booker) {
        super(id, start, end, status);
        this.item = item;
        this.booker = booker;
    }
}
