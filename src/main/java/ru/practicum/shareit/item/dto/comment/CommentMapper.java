package ru.practicum.shareit.item.dto.comment;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public interface CommentMapper {
    Comment commentFromDto(IncomingCommentDto dto, Item item, User author);

    CommentDto commentToDto(Comment comment);
}
