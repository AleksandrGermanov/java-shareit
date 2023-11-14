package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapperImpl;
import ru.practicum.shareit.user.model.User;

public class UserMapperImplTest {
    private final UserMapperImpl userMapper = new UserMapperImpl();
    private final User user = new User(0L, "n", "e@m.l");
    private final UserDto dto = new UserDto(0L, "n", "e@m.l");

    @Test
    public void methodUserToDtoReturnsUserDto() {
        UserDto dtoFromUser = userMapper.userToDto(user);
        Assertions.assertEquals(dto, dtoFromUser);
    }

    @Test
    public void methodUserFromDtoReturnsUser() {
        User userFromDto = userMapper.userFromDto(dto);
        Assertions.assertEquals(user, userFromDto);
    }

    @Test
    public void methodUserFromDtoIfDtoIdNullSetsId() {
        User userFromDto = userMapper.userFromDto(new UserDto());
        Assertions.assertEquals(0L, userFromDto.getId());
    }
}
