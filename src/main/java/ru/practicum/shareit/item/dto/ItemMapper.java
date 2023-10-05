package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public interface ItemMapper {
    ItemDto itemToDto(Item item);

    Item itemFromDto(ItemDto dto);
}
