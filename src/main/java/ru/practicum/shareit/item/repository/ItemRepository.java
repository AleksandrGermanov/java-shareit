package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(long ownerId, PageRequest pageRequest);

    @Query("SELECT i "
            + "FROM Item i "
            + "WHERE i.available = true "
            + "AND (UPPER(i.name) LIKE UPPER(?1) "
            + "OR UPPER(i.description) LIKE UPPER(?1))")
    List<Item> findByText(String text, PageRequest pageRequest);
}
