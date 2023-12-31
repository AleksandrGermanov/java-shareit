package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    List<UserDto> findAll();

    UserDto retrieve(long id);

    UserDto update(UserDto userDto);

    void delete(long id);

    void throwIfRepositoryNotContains(long id);

    User findByIdOrThrow(long id);
}
