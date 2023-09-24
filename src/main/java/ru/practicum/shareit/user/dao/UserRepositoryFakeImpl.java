package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static ru.practicum.shareit.Util.Logging.logInfoExecutedMethod;

@Slf4j
@Repository
public class UserRepositoryFakeImpl implements UserRepository {
    private final Map<Long, User> users = new TreeMap<>();
    private Long idCounter = 0L;

    @Override
    public User create(User user) {
        logInfoExecutedMethod(log, user);
        long id = ++idCounter;
        user.setId(id);
        users.put(id, user);
        return retrieve(id);
    }

    @Override
    public List<User> findAll() {
        logInfoExecutedMethod(log);
        return List.copyOf(users.values());
    }

    @Override
    public User retrieve(long id) {
        logInfoExecutedMethod(log, id);
        return users.get(id);
    }

    @Override
    public User update(User user) {
        logInfoExecutedMethod(log, user);
        long id = user.getId();
        users.put(id, user);
        return users.get(id);
    }

    @Override
    public void delete(long id) {
        logInfoExecutedMethod(log, id);
        users.remove(id);
    }

    @Override
    public Boolean containsUser(long id) {
        logInfoExecutedMethod(log, id);
        return users.containsKey(id);
    }
}
