package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static ru.practicum.shareit.Util.Logging.logInfoExecutedMethod;

@Slf4j
@Repository
public class ItemRepositoryFakeImpl implements ItemRepository {
    private final Map<Long, Item> items = new TreeMap<>();
    private Long idCounter = 0L;

    @Override
    public Item create(Item item) {
        logInfoExecutedMethod(log, item);
        long id = ++idCounter;
        item.setId(id);
        items.put(id, item);
        return retrieve(id);
    }

    @Override
    public List<Item> searchByText(String text) {
        logInfoExecutedMethod(log, text);
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findAllByOwner(long ownerId) {
        logInfoExecutedMethod(log, ownerId);
        return items.values().stream()
                .filter(item -> ownerId == item.getOwnerId())
                .collect(Collectors.toList());
    }

    @Override
    public Item retrieve(long id) {
        logInfoExecutedMethod(log, id);
        return items.get(id);
    }

    @Override
    public Item update(Item item) {
        logInfoExecutedMethod(log, item);
        long id = item.getId();
        items.put(id, item);
        return items.get(id);
    }

    @Override
    public void delete(long id) {
        logInfoExecutedMethod(log, id);
        items.remove(id);
    }

    @Override
    public Boolean containsItem(long id) {
        logInfoExecutedMethod(log, id);
        return items.containsKey(id);
    }
}
