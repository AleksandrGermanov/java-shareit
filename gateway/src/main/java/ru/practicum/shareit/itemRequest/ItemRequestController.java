package ru.practicum.shareit.itemRequest;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;
import ru.practicum.shareit.util.Logging;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@Validated
@Data
@Slf4j
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> findAllByRequester(@RequestHeader("X-Sharer-User-Id") long requesterId) {
        Logging.logInfoIncomingRequest(log, "GET /requests", requesterId);
        return itemRequestClient.findAllByRequester(requesterId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> retrieve(@PathVariable long requestId, @RequestHeader("X-Sharer-User-Id") long userId) {
        Logging.logInfoIncomingRequest(log, "GET /requests/{requestId}", requestId, userId);
        return itemRequestClient.retrieve(requestId, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(@RequestHeader("X-Sharer-User-Id") long seekerId,
                                          @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                          @Positive @RequestParam(defaultValue = "20") int size) {
        Logging.logInfoIncomingRequest(log, "GET /requests/all", seekerId, from, size);
        return itemRequestClient.findAll(seekerId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemRequestDto dto,
                                         @RequestHeader("X-Sharer-User-Id") long requesterId) {
        Logging.logInfoIncomingRequest(log, "POST /requests", dto, requesterId);
        return itemRequestClient.create(requesterId, dto);
    }
}
