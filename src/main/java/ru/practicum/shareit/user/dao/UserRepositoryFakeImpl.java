package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static ru.practicum.shareit.Util.Logging.RepositoryOperation.*;
import static ru.practicum.shareit.Util.Logging.logInfoRepositoryStateChange;

@Slf4j
@Repository
public class UserRepositoryFakeImpl implements UserRepository {
    private final Map<Long, User> users = new TreeMap<>();
    private Long idCounter = 0L;

    @Override
    public User create(User user) {
        long id = ++idCounter;
        user.setId(id);
        users.put(id, user);
        logInfoRepositoryStateChange(log, CREATE, id, user);
        return retrieve(id);
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(users.values());
    }

    @Override
    public User retrieve(long id) {
        return users.get(id);
    }

    @Override
    public User update(User user) {
        long id = user.getId();
        users.put(id, user);
        logInfoRepositoryStateChange(log, UPDATE, id, user);
        return users.get(id);
    }

    @Override
    public void delete(long id) {
        logInfoRepositoryStateChange(log, DELETE, id);
        users.remove(id);
    }

    @Override
    public Boolean containsUser(long id) {
        return users.containsKey(id);
    }
}
