package ru.practicum.shareit.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
public class ItemRequest {
    private final Long requesterId;
    private final String description;
    private final LocalDateTime created;
    @EqualsAndHashCode.Include
    private Long id;
}
