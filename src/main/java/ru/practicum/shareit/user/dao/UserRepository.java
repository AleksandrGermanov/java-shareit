package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User create(User user);

    List<User> findAll();

    User retrieve(long id);

    User update(User user);

    void delete(long id);

    Boolean containsUser(long id);
}
