package ru.practicum.shareit.item.dto.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@Component
@RequiredArgsConstructor
public class CommentMapperImpl implements CommentMapper {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public Comment commentFromDto(IncomingCommentDto dto, Item item, User author) {
        return new Comment(dto.getId(), dto.getText(), item, author,
                dto.getCreated());
    }

    @Override
    public CommentDto commentToDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(),
                comment.getAuthor().getName(), comment.getCreated());
    }
}
