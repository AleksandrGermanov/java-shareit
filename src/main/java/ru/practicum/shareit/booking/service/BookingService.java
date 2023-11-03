package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.SimpleBookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(SimpleBookingDto bookingDto, long bookerId);

    BookingDto update(long bookingId, boolean approved, long itemOwnerId);

    BookingDto retrieve(long bookingId, long itemOwnerOrBookerId);

    List<BookingDto> findByBookerAndByState(BookingState state, long bookerId, int from, int size);

    List<BookingDto> findByItemOwnerAndByState(BookingState state, long itemOwnerId, int from, int size);
}
