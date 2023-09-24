package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.TestClient.TestClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.TestClient.TestClient.TestHttpMethod.*;


@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserControllerTest {
    private final TestClient client;
    private final UserMapper userMapper;
    private final String users = "/users";
    private final List<Long> idListForCleanUp = new ArrayList<>();

    @BeforeAll
    public static void start() {
        SpringApplication.run(ShareItApp.class);
    }

    @AfterEach
    public void cleanUp() {
        idListForCleanUp.forEach(id -> client.sendRequest(DELETE, users, "/", id.toString()));
        idListForCleanUp.clear();
    }

    //------------------------Секция GET--------------------------------
    @Test
    public void usersGetReturnsCode200AndEmptyList() {
        HttpResponse<String> resp = client.sendRequest(GET, users);
        List<UserDto> fetched = client.deserializeBody(resp, new TypeReference<>() {
        });
        assertEquals(200, resp.statusCode());
        assertInstanceOf(ArrayList.class, fetched);
        assertEquals(Collections.emptyList(), fetched);
    }

    @Test
    public void usersGetReturnsCode200AndListContainingPostedDtos() {
        User user = new User(-1L, "Nm", "e@mail.fake");
        UserDto userDto = client.deserializeBody(client.sendRequest(POST, userMapper.userToDto(user), users),
                UserDto.class);
        User user2 = new User(-1L, "name", "em@a.il");
        UserDto user2Dto = client.deserializeBody(client.sendRequest(POST, userMapper.userToDto(user2), users),
                UserDto.class);
        HttpResponse<String> resp = client.sendRequest(GET, users);
        List<UserDto> fetched = client.deserializeBody(resp, new TypeReference<>() {
        });
        idListForCleanUp.add(userDto.getId());
        idListForCleanUp.add(user2Dto.getId());
        assertEquals(200, resp.statusCode());
        assertInstanceOf(ArrayList.class, fetched);
        assertTrue(fetched.contains(userDto));
        assertTrue(fetched.contains(user2Dto));
    }

    @Test
    public void usersGetWithIdReturnsCode404AndMessageWhenNoUserFound() {
        HttpResponse<String> response = client.sendRequest(GET, users, "/1");
        assertEquals(404, response.statusCode());
        assertEquals("Пользователь с id = 1 не найден.", client.deserializeBody(response, String.class));
    }

    @Test
    public void usersGetWithIdReturnsPostedUserDto() {
        User user = new User(-1L, "Nm", "e@mail.fake");
        UserDto dto1 = client.deserializeBody(client.sendRequest(POST, user, users), new TypeReference<>() {
        });
        idListForCleanUp.add(dto1.getId());
        HttpResponse<String> response = client.sendRequest(GET, users, "/", dto1.getId().toString());
        UserDto dto2 = client.deserializeBody(response, UserDto.class);
        assertEquals(dto1.getName(), dto2.getName());
        assertEquals(dto1.getEmail(), dto2.getEmail());
    }

    //------------------------Секция POST--------------------------------
    @Test
    public void usersPostReturnsUserDtoWithDefinedFields() {
        User user = new User(-1L, "Nm", "e@mail.fake");
        UserDto fetchedDto = client.deserializeBody(client.sendRequest(POST, user, users), new TypeReference<>() {
        });
        idListForCleanUp.add(fetchedDto.getId());
        User fetched = userMapper.userFromDto(fetchedDto);
        assertEquals(user.getName(), fetched.getName());
        assertEquals(user.getEmail(), fetched.getEmail());
    }

    @Test
    public void usersPostReturnsCode500WhenEmptyBody() {
        HttpResponse<String> response = client.sendRequest(POST, (Object) "", users);
        assertEquals(500, response.statusCode());
    }

    @Test
    public void usersPostReturnsCode201WhenCreated() {
        User user = new User(-1L, "Nm", "e@mail.fake");
        HttpResponse<String> resp = client.sendRequest(POST, userMapper.userToDto(user), users);
        idListForCleanUp.add(client.deserializeBody(resp, UserDto.class).getId());
        assertEquals(201, resp.statusCode());
    }

    @Test
    public void usersPostReturnsCode409AndMessageWhenSameEmail() {
        User user = new User(-1L, "Nm", "e@mail.fake");
        idListForCleanUp.add(client.deserializeBody(
                client.sendRequest(POST, userMapper.userToDto(user), users), UserDto.class
        ).getId());
        User user2 = new User(-1L, "name", "e@mail.fake");
        HttpResponse<String> resp = client.sendRequest(POST, userMapper.userToDto(user2), users);
        assertEquals(409, resp.statusCode());
        assertEquals("Такой email уже зарегистрирован.",
                client.deserializeBody(resp, String.class));
    }

    @Test
    public void usersPostReturnsCode400AndMessageWhenExistingId() {
        User user = new User(-1L, "Nm", "e@mail.fake");
        Long userId = client.deserializeBody(
                client.sendRequest(POST, userMapper.userToDto(user), users), UserDto.class
        ).getId();
        idListForCleanUp.add(userId);
        User user2 = new User(userId, "name", "e@mail.fake");
        HttpResponse<String> resp = client.sendRequest(POST, userMapper.userToDto(user2), users);
        assertEquals(400, resp.statusCode());
        assertEquals("Пользователь с id = " + userId + " уже существует. "
                        + "Попробуйте изменить передаваемые данные или используйте подходящий метод.",
                client.deserializeBody(resp, String.class));
    }

    @Test
    public void usersPostReturnsCode400AndMessageWhenBlankName() {
        User user = new User(-1L, " ", "e@mail.fake");
        HttpResponse<String> resp = client.sendRequest(POST, userMapper.userToDto(user), users);
        assertEquals(400, resp.statusCode());
        assertTrue(client.deserializeBody(resp, String.class).contains("Нельзя оставлять поле 'name' пустым."));
    }

    @Test
    public void usersPostReturnsCode400AndMessageWhenBigName() {
        User user = new User(-1L, "1".repeat(256), "e@mail.fake");
        HttpResponse<String> resp = client.sendRequest(POST, userMapper.userToDto(user), users);
        assertEquals(400, resp.statusCode());
        assertTrue(client.deserializeBody(resp, String.class)
                .contains("Имя не должно превышать 125 символов (для UTF-8)."));
    }

    @Test
    public void usersPostReturnsCode400AndMessageWhenBadEmail() {
        User user = new User(-1L, "1", "WhatIsEmail?");
        HttpResponse<String> resp = client.sendRequest(POST, userMapper.userToDto(user), users);
        assertEquals(400, resp.statusCode());
        assertTrue(client.deserializeBody(resp, String.class)
                .contains("Поле 'email' должно иметь правильный формат."));
    }

    //------------------------Секция PATCH--------------------------------
    @Test
    public void usersPatchReturnsUserDtoWithNewParams() {
        User user = new User(-1L, "Nm", "e@mail.fake");
        UserDto fetchedDto = client.deserializeBody(client.sendRequest(POST, userMapper.userToDto(user), users),
                new TypeReference<>() {
                });
        long fetchedId = fetchedDto.getId();
        idListForCleanUp.add(fetchedId);
        UserDto updated = new UserDto(null, "Updated", "up@date.d");
        UserDto fetchedDto2 = client.deserializeBody(client.sendRequest(PATCH, updated, users,
                        "/" + fetchedId),
                new TypeReference<>() {
                });
        assertNotEquals(fetchedDto.getName(), fetchedDto2.getName());
        assertNotEquals(fetchedDto.getEmail(), fetchedDto2.getEmail());
        assertEquals(updated.getName(), fetchedDto2.getName());
        assertEquals(updated.getEmail(), fetchedDto2.getEmail());
    }

    @Test
    public void usersPatchReturnsCode500WhenNoId() {
        HttpResponse<String> response = client.sendRequest(PATCH, (Object) "", users);
        assertEquals(500, response.statusCode());
    }

    @Test
    public void usersPatchReturnsCode500WhenEmptyBody() {
        User user = new User(-1L, "Nm", "e@mail.fake");
        long id = client.deserializeBody(client.sendRequest(POST, userMapper.userToDto(user), users), UserDto.class)
                .getId();
        idListForCleanUp.add(id);
        HttpResponse<String> response = client.sendRequest(PATCH, (Object) "",
                users, "/" + id);
        assertEquals(500, response.statusCode());
    }

    @Test
    public void usersPatchReturnsCode200WhenUpdates() {
        User user = new User(-1L, "Nm", "e@mail.fake");
        long id = client.deserializeBody(client.sendRequest(POST, userMapper.userToDto(user), users), UserDto.class)
                .getId();
        idListForCleanUp.add(id);
        HttpResponse<String> response = client.sendRequest(PATCH, new UserDto(null, "n", "e@m.l"),
                users, "/" + id);
        assertEquals(200, response.statusCode());
        assertEquals("n", client.deserializeBody(response, UserDto.class).getName());
        assertEquals("e@m.l", client.deserializeBody(response, UserDto.class).getEmail());
    }

    @Test
    public void usersPatchReturnsCode200AndUpdatesNameWhenSameEmail() {
        User user = new User(-1L, "Nm", "e@mail.fake");
        long id = client.deserializeBody(client.sendRequest(POST, userMapper.userToDto(user), users), UserDto.class)
                .getId();
        idListForCleanUp.add(id);
        User user2 = new User(-1L, "name", "e@mail.fake");
        HttpResponse<String> resp = client.sendRequest(PATCH, userMapper.userToDto(user2), users, "/" + id);
        assertEquals(200, resp.statusCode());
        assertEquals(user2.getName(), client.deserializeBody(resp, UserDto.class).getName());
    }

    @Test
    public void usersPatchReturnsCode400AndMessageWhenBlankName() {
        User user = new User(-1L, "Nm", "e@mail.fake");
        long id = client.deserializeBody(client.sendRequest(POST, userMapper.userToDto(user), users), UserDto.class)
                .getId();
        idListForCleanUp.add(id);
        UserDto dto = new UserDto(null, " ", "em@il.nowhere");
        HttpResponse<String> resp = client.sendRequest(PATCH, dto, users, "/" + id);
        assertEquals(400, resp.statusCode());
        assertTrue(client.deserializeBody(resp, String.class).contains("Нельзя оставлять поле 'name' пустым."));
    }

    @Test
    public void usersPatchReturnsCode400AndMessageWhenBigName() {
        User user = new User(-1L, "Nm", "e@mail.fake");
        long id = client.deserializeBody(client.sendRequest(POST, userMapper.userToDto(user), users), UserDto.class)
                .getId();
        idListForCleanUp.add(id);
        UserDto dto = new UserDto(null, "1".repeat(256), "em@il.nowhere");
        HttpResponse<String> resp = client.sendRequest(PATCH, dto, users, "/" + id);
        assertEquals(400, resp.statusCode());
        assertTrue(client.deserializeBody(resp, String.class)
                .contains("Имя не должно превышать 125 символов (для UTF-8)."));
    }

    @Test
    public void usersPatchReturnsCode400AndMessageWhenBadEmail() {
        User user = new User(-1L, "Nm", "e@mail.fake");
        long id = client.deserializeBody(client.sendRequest(POST, userMapper.userToDto(user), users), UserDto.class)
                .getId();
        idListForCleanUp.add(id);
        UserDto dto = new UserDto(null, "1lll1", "talkToMyPigeon");
        HttpResponse<String> resp = client.sendRequest(PATCH, dto, users, "/" + id);
        assertEquals(400, resp.statusCode());
        assertTrue(client.deserializeBody(resp, String.class)
                .contains("Поле 'email' должно иметь правильный формат."));
    }

    @Test
    public void usersPatchReturnsCode200AndUpdatesNameWhenDtoHasNoEmail() {
        User user = new User(-1L, "Nm", "e@mail.fake");
        long id = client.deserializeBody(client.sendRequest(POST, userMapper.userToDto(user), users), UserDto.class)
                .getId();
        idListForCleanUp.add(id);
        UserDto dto = new UserDto(null, "1lll1", null);
        HttpResponse<String> resp = client.sendRequest(PATCH, dto, users, "/" + id);
        assertEquals(200, resp.statusCode());
        assertEquals(client.deserializeBody(resp, UserDto.class)
                .getName(), dto.getName());
    }

    @Test
    public void usersPatchReturnsCode200AndUpdatesEmailWhenDtoHasNoName() {
        User user = new User(-1L, "Nm", "e@mail.fake");
        long id = client.deserializeBody(client.sendRequest(POST, userMapper.userToDto(user), users), UserDto.class)
                .getId();
        idListForCleanUp.add(id);
        UserDto dto = new UserDto(null, null, "e@ma.e");
        HttpResponse<String> resp = client.sendRequest(PATCH, dto, users, "/" + id);
        assertEquals(200, resp.statusCode());
        assertEquals(dto.getEmail(), client.deserializeBody(resp, UserDto.class)
                .getEmail());
    }

    //------------------------Секция DELETE--------------------------------
    @Test
    public void usersDeleteReturnsCode500WhenNoId() {
        HttpResponse<String> response = client.sendRequest(DELETE, users);
        assertEquals(500, response.statusCode());
    }

    @Test
    public void usersDeleteReturnsCode404WhenNoUserFound() {
        HttpResponse<String> response = client.sendRequest(DELETE, users, "/999");
        assertEquals(404, response.statusCode());
    }

    @Test
    public void usersDeleteReturnsCode200AndDeletesUser() {
        User user = new User(-1L, "name", "e@mail.fake");
        client.sendRequest(POST, userMapper.userToDto(user), users);
        HttpResponse<String> resp = client.sendRequest(GET, users, "/1");
        assertEquals(200, resp.statusCode());
        HttpResponse<String> resp2 = client.sendRequest(DELETE, users, "/1");
        assertEquals(200, resp2.statusCode());
        HttpResponse<String> resp3 = client.sendRequest(GET, users, "/1");
        assertEquals(404, resp3.statusCode());
    }
}