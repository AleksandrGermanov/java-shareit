package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapperImpl implements UserMapper {
    @Override
    public UserDto userToDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    @Override
    public User userFromDto(UserDto dto) {
        long id = dto.getId() != null ? dto.getId() : 0L;
        return new User(id, dto.getName(), dto.getEmail());
    }
}
