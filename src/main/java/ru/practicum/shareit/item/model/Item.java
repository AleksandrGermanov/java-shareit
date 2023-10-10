package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @NotNull(message = "Поле 'ownerId' не может быть незаполненным.")
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;
    @NotBlank(message = "Поле 'name' не может быть пустым.")
    @Size(max = 125, message = "Имя не должно превышать 125 символов (для UTF-8).")
    @Column(nullable = false)
    private String name;
    @NotNull
    @Size(max = 250, message = "Описание не должно превышать 250 символов (для UTF-8).")
    @Column(nullable = false)
    private String description;
    @NotNull(message = "Поле 'isAvailable' не может быть незаполненным.")
    @Column(nullable = false)
    private Boolean available;
    @Column(name = "request_id", nullable = false)
    private Long requestId;
    @OneToMany(mappedBy = "item", fetch = FetchType.EAGER)
    private List<Comment> comments;
}
