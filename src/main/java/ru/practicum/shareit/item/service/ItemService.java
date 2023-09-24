package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto);

    List<ItemDto> findAllByOwner(@Valid @NotNull long ownerId);

    List<ItemDto> searchByText(String text);

    ItemDto retrieve(long id);

    ItemDto update(ItemDto itemDto);

    void delete(long id);
}