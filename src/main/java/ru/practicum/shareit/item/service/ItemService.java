package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.notFound.ItemNotFoundException;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.IncomingCommentDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto);

    CommentDto create(IncomingCommentDto commentDto);

    List<ItemDtoWithBookingsAndComments> findAllByOwner(@Valid @NotNull long ownerId);

    List<ItemDto> searchByText(String text);

    ItemDto retrieve(long id, long requesterId);

    ItemDto update(ItemDto itemDto);

    void delete(long id);

    Item findByIdOrThrow(long id) throws ItemNotFoundException;
}