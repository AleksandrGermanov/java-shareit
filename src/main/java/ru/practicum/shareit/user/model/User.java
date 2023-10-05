package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Validated
@AllArgsConstructor
public class User {
    @EqualsAndHashCode.Include
    private Long id;
    @Size(max = 125, message = "Имя не должно превышать 125 символов (для UTF-8).")
    @NotBlank(message = "Нельзя оставлять поле 'name' пустым.")
    private String name;
    @NotNull
    @Email(message = "Поле 'email' должно иметь правильный формат.")
    private String email;
}
