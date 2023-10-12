package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Component
@RequiredArgsConstructor
public class BookingMapperImpl implements BookingMapper {

    @Override
    public BookingDto bookingToOutcomingDto(Booking booking, ItemDto itemDto, UserDto bookerDto) {
        return new OutcomingBookingDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getStatus(),
                itemDto, bookerDto);
    }

    @Override
    public BookingDto bookingToSimpleDto(Booking booking) {
        return new SimpleBookingDto(booking.getId(), booking.getStart(), booking.getEnd(),
                booking.getStatus(), booking.getItem().getId(), booking.getBooker().getId());
    }

    @Override
    public Booking bookingFromDto(SimpleBookingDto dto, Item item, User booker) {
        long id = dto.getId() != null ? dto.getId() : 0L;
        BookingStatus status = dto.getStatus() != null ? dto.getStatus() : BookingStatus.WAITING;
        return new Booking(id, item, booker, dto.getStart(), dto.getEnd(), status);
    }
}
