package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item create(Item item);

    List<Item> findAllByOwner(long ownerId);

    List<Item> searchByText(String text);

    Item retrieve(long id);

    Item update(Item item);

    void delete(long id);

    Boolean containsItem(long id);
}
