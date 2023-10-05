package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static ru.practicum.shareit.Util.Logging.RepositoryOperation.*;
import static ru.practicum.shareit.Util.Logging.logInfoRepositoryStateChange;

@Slf4j
@Repository
public class ItemRepositoryFakeImpl implements ItemRepository {
    private final Map<Long, Item> items = new TreeMap<>();
    private Long idCounter = 0L;

    @Override
    public Item create(Item item) {
        long id = ++idCounter;
        item.setId(id);
        items.put(id, item);
        logInfoRepositoryStateChange(log, CREATE, id, item);
        return retrieve(id);
    }

    @Override
    public List<Item> searchByText(String text) {
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findAllByOwner(long ownerId) {
        return items.values().stream()
                .filter(item -> ownerId == item.getOwnerId())
                .collect(Collectors.toList());
    }

    @Override
    public Item retrieve(long id) {
        return items.get(id);
    }

    @Override
    public Item update(Item item) {
        long id = item.getId();
        items.put(id, item);
        logInfoRepositoryStateChange(log, UPDATE, id, item);
        return items.get(id);
    }

    @Override
    public void delete(long id) {
        logInfoRepositoryStateChange(log, DELETE, id);
        items.remove(id);
    }

    @Override
    public Boolean containsItem(long id) {
        return items.containsKey(id);
    }
}
