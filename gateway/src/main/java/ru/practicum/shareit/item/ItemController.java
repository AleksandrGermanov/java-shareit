package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.Scenario;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Map;

import static ru.practicum.shareit.util.Logging.logInfoIncomingRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> findAllByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                 @Positive @RequestParam(defaultValue = "20") int size) {
        logInfoIncomingRequest(log, "GET /items", ownerId);
        return itemClient.findAllByOwner(ownerId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> retrieve(@PathVariable long id, @RequestHeader("X-Sharer-User-Id") long requesterId) {
        logInfoIncomingRequest(log, "GET /items/{id}", id, requesterId);
        return itemClient.retrieve(id, requesterId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByText(@RequestParam String text,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                               @Positive @RequestParam(defaultValue = "20") int size) {
        logInfoIncomingRequest(log, "GET /search", text);
        return itemClient.searchByText(text, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated({Scenario.OnCreate.class, Scenario.Always.class})
    public ResponseEntity<Object> create(@Valid @RequestBody ItemDto itemDto,
                                         @RequestHeader("X-Sharer-User-Id") long ownerId) {
        logInfoIncomingRequest(log, "POST /items", ownerId, itemDto);
        return itemClient.create(ownerId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> create(@PathVariable long itemId, @Valid @RequestBody CommentDto dto,
                                         @RequestHeader("X-Sharer-User-Id") long authorId) {
        logInfoIncomingRequest(log, "POST/items/{itemId}/comment", itemId, dto, authorId);
        return itemClient.create(itemId, authorId, dto);
    }

    @PatchMapping("/{id}")
    @Validated(Scenario.Always.class)
    public ResponseEntity<Object> update(@PathVariable long id, @Valid @RequestBody ItemDto itemDto,
                                         @RequestHeader("X-Sharer-User-Id") long ownerId) {
        logInfoIncomingRequest(log, "PATCH /items/{id}", id, itemDto, ownerId);
        return itemClient.update(id, ownerId, itemDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable long id) {
        logInfoIncomingRequest(log, "DELETE /items/{id}", id);
        return itemClient.delete(id);
    }
}
