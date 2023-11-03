package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.item.dto.item.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AdvancedItemRequestDto extends ItemRequestDto {
    private List<ItemDto> items;

    public AdvancedItemRequestDto(Long id, Long requesterId, String description, LocalDateTime created,
                                  List<ItemDto> items) {
        super(id, requesterId, description, created);
        this.items = items;
    }
}
