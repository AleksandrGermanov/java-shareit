package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

public interface UserMapper {
    static UserMapper getDefaultImpl() {
        return new UserMapperImpl();
    }

    UserDto userToDto(User user);

    User userFromDto(UserDto dto);
}
