package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

@Component
public class ItemRequestMapperImpl implements ItemRequestMapper {
    @Override
    public ItemRequest itemRequestFromDto(ItemRequestDto itemRequestDto, User requester) {
        long id = itemRequestDto.getId() == null ? 0L : itemRequestDto.getId();
        String description = itemRequestDto.getDescription() == null ? "" :
                itemRequestDto.getDescription();
        return new ItemRequest(id, requester,
                description, itemRequestDto.getCreated(), Collections.emptyList());
    }

    @Override
    public ItemRequestDto itemRequestToDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getRequester().getId(),
                itemRequest.getDescription(), itemRequest.getCreated());
    }

    @Override
    public AdvancedItemRequestDto itemRequestToAdvancedDto(ItemRequest itemRequest, List<ItemDto> items) {
        return new AdvancedItemRequestDto(itemRequest.getId(), itemRequest.getRequester().getId(),
                itemRequest.getDescription(), itemRequest.getCreated(), items);
    }
}
