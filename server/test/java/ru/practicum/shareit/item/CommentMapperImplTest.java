package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentMapperImpl;
import ru.practicum.shareit.item.dto.comment.IncomingCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

public class CommentMapperImplTest {
    private final CommentMapperImpl mapper = new CommentMapperImpl();
    private final User user = new User(0L, "n", "e@m.l");
    private final Item item = new Item(0L, user, "n", "d",
            true, null, Collections.emptyList());
    private final Comment comment = new Comment(0L, "text", item, user, LocalDateTime.now());

    @Test
    public void methodCommentFromDtoReturnsCommentDto() {
        CommentDto dto = new CommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(),
                comment.getCreated());

        Assertions.assertInstanceOf(CommentDto.class, mapper.commentToDto(comment));
        Assertions.assertEquals(dto, mapper.commentToDto(comment));
    }

    @Test
    public void methodCommentToDtoReturnsCommentDto() {
        IncomingCommentDto dto = new IncomingCommentDto(comment.getId(), comment.getText(),
                comment.getCreated(), comment.getAuthor().getId(), comment.getItem().getId());
        Assertions.assertEquals(comment, mapper.commentFromDto(dto, item, user));
    }
}
