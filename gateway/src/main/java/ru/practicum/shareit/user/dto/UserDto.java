package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.util.Scenario;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class UserDto {
    @Size(groups = Scenario.Always.class,
            max = 125, message = "Имя не должно превышать 125 символов (для UTF-8).")
    @NotBlank(groups = Scenario.OnCreate.class,
            message = "Нельзя оставлять поле 'name' пустым.")
    private String name;
    @NotNull(groups = Scenario.OnCreate.class)
    @Email(groups = Scenario.Always.class,
            message = "Поле 'email' должно иметь правильный формат.")
    private String email;
}
