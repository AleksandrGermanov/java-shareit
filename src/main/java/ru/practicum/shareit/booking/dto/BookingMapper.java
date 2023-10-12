package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public interface BookingMapper {

    BookingDto bookingToOutcomingDto(Booking booking, ItemDto itemDto, UserDto bookerDto);

    BookingDto bookingToSimpleDto(Booking booking);

    Booking bookingFromDto(SimpleBookingDto dto, Item item, User booker);
}
