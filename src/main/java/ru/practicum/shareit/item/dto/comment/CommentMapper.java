package ru.practicum.shareit.item.dto.comment;

import ru.practicum.shareit.item.model.Comment;

public interface CommentMapper {
    Comment commentFromDto(IncomingCommentDto dto);

    CommentDto commentToDto(Comment comment);
}
