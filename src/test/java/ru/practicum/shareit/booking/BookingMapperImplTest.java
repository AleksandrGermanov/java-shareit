package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapperImpl;
import ru.practicum.shareit.booking.dto.OutcomingBookingDto;
import ru.practicum.shareit.booking.dto.SimpleBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

public class BookingMapperImplTest {
    private final BookingMapperImpl mapper = new BookingMapperImpl();
    private final User user = new User(0L, "n", "e@m.l");
    private final Item item = new Item(0L, user, "n", "d",
            true, null, Collections.emptyList());
    private final Booking booking = new Booking(0L, item, user, LocalDateTime.now(),
            LocalDateTime.now().plusHours(2), BookingStatus.WAITING);
    private final SimpleBookingDto simpleBookingDto = new SimpleBookingDto(booking.getId(),
            booking.getStart(), booking.getEnd(),
            booking.getStatus(), booking.getItem().getId(), booking.getBooker().getId());

    @Test
    public void methodBookingToOutcomingDtoReturnsOutcomingBookingDto() {
        UserDto bookerDto = new UserDto();
        ItemDto itemDto = new ItemDto();
        OutcomingBookingDto dto = new OutcomingBookingDto(booking.getId(), booking.getStart(), booking.getEnd(),
                booking.getStatus(), itemDto, bookerDto);

        Assertions.assertInstanceOf(BookingDto.class, mapper.bookingToOutcomingDto(booking, itemDto, bookerDto));
        Assertions.assertEquals(dto, mapper.bookingToOutcomingDto(booking, itemDto, bookerDto));
    }

    @Test
    public void methodBookingToSimpleDtoReturnsSimpleBookingDto() {
        Assertions.assertEquals(simpleBookingDto, mapper.bookingToSimpleDto(booking));
    }

    @Test
    public void methodBookingFromSimpleDtoReturnsBooking() {
        Assertions.assertEquals(booking, mapper.bookingFromDto(simpleBookingDto, item, user));
    }
}
