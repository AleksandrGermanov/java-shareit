package ru.practicum.shareit.item.dto.item;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public interface ItemMapper {
    ItemDto itemToDto(Item item);

    AdvancedItemDto itemToDtoWithBookingsAndComments(Item item, BookingDto lastBooking,
                                                     BookingDto nextBooking);

    Item itemFromDto(ItemDto dto, User owner);
}
