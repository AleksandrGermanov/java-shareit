package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

public interface UserMapper {
    UserDto userToDto(User user);

    User userFromDto(UserDto dto);
}
