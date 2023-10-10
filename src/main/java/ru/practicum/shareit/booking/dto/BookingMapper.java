package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

public interface BookingMapper {

    BookingDto bookingToOutcomingDto(Booking booking);

    BookingDto bookingToNestedInItemDtoDto(Booking booking);

    Booking bookingFromDto(IncomingAndNestedInItemDtoBookingDto dto);
}
