package ru.practicum.shareit.item.dto.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemMapperImpl implements ItemMapper {
    private final CommentMapper commentMapper;

    @Override
    public ItemDto itemToDto(Item item) {
        Long requestId = item.getRequest() == null ? null : item.getRequest().getId();
        return new ItemDto(item.getId(), item.getOwner().getId(), item.getName(),
                item.getDescription(), item.getAvailable(), requestId);
    }

    public ItemDto itemToSimpleDto(Item item) {
        Long requestId = item.getRequest() == null ? null : item.getRequest().getId();
        return new ItemDto(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable(), requestId);
    }

    @Override
    public AdvancedItemDto itemToDtoWithBookingsAndComments(Item item, BookingDto lastBooking,
                                                            BookingDto nextBooking) {
        List<CommentDto> comments = item.getComments().isEmpty()
                ? Collections.emptyList()
                : item.getComments().stream()
                .map(commentMapper::commentToDto).collect(Collectors.toList());
        Long requestId = item.getRequest() == null ? null : item.getRequest().getId();
        return new AdvancedItemDto(item.getId(), item.getOwner().getId(), item.getName(),
                item.getDescription(), item.getAvailable(), requestId,
                lastBooking, nextBooking, comments);
    }

    @Override
    public Item itemFromDto(ItemDto dto, User owner, ItemRequest request) {
        long id = dto.getId() != null ? dto.getId() : 0L;
        return new Item(id, owner, dto.getName(),
                dto.getDescription(), dto.getAvailable(), request, Collections.emptyList());
    }
}
