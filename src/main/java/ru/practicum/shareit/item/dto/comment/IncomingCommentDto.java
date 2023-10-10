package ru.practicum.shareit.item.dto.comment;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class IncomingCommentDto extends CommentDto {
    private Long authorId;
    private Long itemId;

    public IncomingCommentDto(Long id, String text, LocalDateTime created, Long authorId, Long itemId) {
        super(id, text, null, created);
        this.authorId = authorId;
        this.itemId = itemId;
    }
}
