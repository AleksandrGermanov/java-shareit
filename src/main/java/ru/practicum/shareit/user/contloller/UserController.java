package ru.practicum.shareit.user.contloller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static ru.practicum.shareit.util.Logging.logInfoIncomingRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> findAll() {
        logInfoIncomingRequest(log, "GET /users");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto retrieve(@PathVariable long id) {
        logInfoIncomingRequest(log, "GET /users/{id}", id);
        return userService.retrieve(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody UserDto userDto) {
        logInfoIncomingRequest(log, "POST /users", userDto);
        return userService.create(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable long id, @RequestBody UserDto userDto) {
        logInfoIncomingRequest(log, "PATCH /users/{id}", id, userDto);
        userDto.setId(id);
        return userService.update(userDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        logInfoIncomingRequest(log, "DELETE /users/{id}", id);
        userService.delete(id);
    }
}
