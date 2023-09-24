package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Validated
@AllArgsConstructor
public class Item {
    private static final ItemMapper itemMapper = ItemMapper.getDefaultImpl();
    @EqualsAndHashCode.Include
    private Long id;
    @NotNull(message = "Поле 'ownerId' не может быть незаполненным.")
    private Long ownerId;
    @NotBlank(message = "Поле 'name' не может быть пустым.")
    @Size(max = 125, message = "Имя не должно превышать 125 символов (для UTF-8).")
    private String name;
    @NotNull
    @Size(max = 250, message = "Описание не должно превышать 250 символов (для UTF-8).")
    private String description;
    @NotNull(message = "Поле 'isAvailable' не может быть незаполненным.")
    private Boolean available;
    private Long requestId;

    public static Item fromDto(ItemDto dto) {
        return itemMapper.itemFromDto(dto);
    }

    public ItemDto toDto() {
        return itemMapper.itemToDto(this);
    }
}
