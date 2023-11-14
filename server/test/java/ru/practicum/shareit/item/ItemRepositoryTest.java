package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRepositoryTest {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private User user = new User(null, "user", "e@ma.il");
    private Item item1 = new Item(null, user, "item1", "descr1",
            true, null, Collections.emptyList());
    private Item item2 = new Item(null, user, "i2", "d2",
            true, null, Collections.emptyList());

    @BeforeEach
    public void setUp() {
        user = userRepository.save(user);
        item1 = itemRepository.save(item1);
        item2 = itemRepository.save(item2);
    }

    @Test
    public void methodFindByTextReturnsSearchedItems() {
        List<Item> items = itemRepository.findByText("%1%", PageRequest.of(0, 2)).stream()
                .collect(Collectors.toList());
        Assertions.assertEquals(List.of(item1), items);

        List<Item> items2 = itemRepository.findByText("%2%", PageRequest.of(0, 2)).stream()
                .collect(Collectors.toList());
        Assertions.assertEquals(List.of(item2), items2);

        List<Item> items3 = itemRepository.findByText("%i%", PageRequest.of(0, 2)).stream()
                .collect(Collectors.toList());
        Assertions.assertEquals(2, items3.size());
        Assertions.assertTrue(items3.contains(item1));
        Assertions.assertTrue(items3.contains(item2));

        List<Item> items4 = itemRepository.findByText("%q%", PageRequest.of(0, 2)).stream()
                .collect(Collectors.toList());
        Assertions.assertEquals(Collections.emptyList(), items4);
    }
}
