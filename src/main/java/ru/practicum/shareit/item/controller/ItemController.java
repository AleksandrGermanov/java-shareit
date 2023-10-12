package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.IncomingCommentDto;
import ru.practicum.shareit.item.dto.item.AdvancedItemDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.Util.Logging.logInfoIncomingRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<AdvancedItemDto> findAllByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        logInfoIncomingRequest(log, "GET /items", ownerId);
        return itemService.findAllByOwner(ownerId);
    }

    @GetMapping("/{id}")
    public ItemDto retrieve(@PathVariable long id, @RequestHeader("X-Sharer-User-Id") long requesterId) {
        logInfoIncomingRequest(log, "GET /items/{id}", id);
        return itemService.retrieve(id, requesterId);
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

    @PostMapping("/{itemId}/comment")
    public CommentDto create(@PathVariable long itemId, @RequestBody IncomingCommentDto dto,
                             @RequestHeader("X-Sharer-User-Id") long authorId) {
        dto.setCreated(LocalDateTime.now());
        dto.setAuthorId(authorId);
        dto.setItemId(itemId);
        return itemService.create(dto);
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
