package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapperImpl implements ItemMapper {
    @Override
    public ItemDto itemToDto(Item item) {
        return new ItemDto(item.getId(), item.getOwnerId(), item.getName(),
                item.getDescription(), item.getAvailable(), item.getRequestId());
    }

    @Override
    public Item itemFromDto(ItemDto dto) {
        long id = dto.getId() != null ? dto.getId() : 0L;
        return new Item(id, dto.getOwnerId(), dto.getName(),
                dto.getDescription(), dto.getAvailable(), dto.getRequestId());
    }
}
