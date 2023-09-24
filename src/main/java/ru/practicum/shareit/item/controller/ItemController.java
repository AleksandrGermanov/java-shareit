package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.Util.Logging.logInfoIncomingRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> findAllByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        logInfoIncomingRequest(log, "GET /items", ownerId);
        return itemService.findAllByOwner(ownerId);
    }

    @GetMapping("/{id}")
    public ItemDto retrieve(@PathVariable long id) {
        logInfoIncomingRequest(log, "GET /items/{id}", id);
        return itemService.retrieve(id);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByText(@RequestParam String text) {
        logInfoIncomingRequest(log, "GET /search", text);
        return itemService.searchByText(text);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long ownerId) {
        logInfoIncomingRequest(log, "POST /items", ownerId, itemDto);
        itemDto.setOwnerId(ownerId);
        return itemService.create(itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@PathVariable long id, @RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") long ownerId) {
        logInfoIncomingRequest(log, "PATCH /items/{id}", id, itemDto, ownerId);
        itemDto.setId(id);
        itemDto.setOwnerId(ownerId);
        return itemService.update(itemDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        logInfoIncomingRequest(log, "DELETE /items/{id}", id);
        itemService.delete(id);
    }
}
