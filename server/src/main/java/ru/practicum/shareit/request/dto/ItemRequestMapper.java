package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRequestMapper {
    ItemRequest itemRequestFromDto(ItemRequestDto itemRequestDto, User requester);

    ItemRequestDto itemRequestToDto(ItemRequest itemRequest);

    AdvancedItemRequestDto itemRequestToAdvancedDto(ItemRequest itemRequest, List<ItemDto> items);
}
