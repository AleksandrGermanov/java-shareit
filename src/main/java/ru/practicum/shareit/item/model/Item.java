package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Validated
@AllArgsConstructor
public class Item {
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
}
