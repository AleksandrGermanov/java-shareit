package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.Scenario;

import javax.validation.Valid;

import static ru.practicum.shareit.util.Logging.logInfoIncomingRequest;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        logInfoIncomingRequest(log, "GET /users");
        return userClient.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> retrieve(@PathVariable long id) {
        logInfoIncomingRequest(log, "GET /users/{id}", id);
        return userClient.retrieve(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated({Scenario.Always.class, Scenario.OnCreate.class})
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        logInfoIncomingRequest(log, "POST /users", userDto);
        return userClient.create(userDto);
    }

    @PatchMapping("/{id}")
    @Validated({Scenario.Always.class})
    public ResponseEntity<Object> update(@PathVariable long id, @Valid @RequestBody UserDto userDto) {
        logInfoIncomingRequest(log, "PATCH /users/{id}", id, userDto);
        return userClient.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable long id) {
        logInfoIncomingRequest(log, "DELETE /users/{id}", id);
        return userClient.delete(id);
    }
}
