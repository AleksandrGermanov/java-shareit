package ru.practicum.shareit.item.dto.item;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;

public interface ItemMapper {
    ItemDto itemToDto(Item item);

    ItemDtoWithBookingsAndComments itemToDtoWithBookingsAndComments(Item item, BookingDto lastBooking,
                                                                    BookingDto nextBooking);

    Item itemFromDto(ItemDto dto);
}