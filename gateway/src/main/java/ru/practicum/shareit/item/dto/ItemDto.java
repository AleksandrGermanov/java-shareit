package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.util.Scenario;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class ItemDto {
    @NotBlank(groups = Scenario.OnCreate.class, message = "Поле 'name' не может быть пустым.")
    @Size(groups = Scenario.Always.class, max = 125, message = "Имя не должно превышать 125 символов (для UTF-8).")
    private String name;
    @NotNull(groups = Scenario.OnCreate.class)
    @Size(groups = Scenario.Always.class, max = 250, message = "Описание не должно превышать 250 символов (для UTF-8).")
    private String description;
    @NotNull(groups = Scenario.OnCreate.class,
            message = "Поле 'available' не может быть незаполненным.")
    private Boolean available;
    private Long requestId;
}
