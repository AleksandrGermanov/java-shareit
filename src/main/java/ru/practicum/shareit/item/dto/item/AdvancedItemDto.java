package ru.practicum.shareit.item.dto.item;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class AdvancedItemDto extends ItemDto {
    private List<CommentDto> comments;
    private BookingDto lastBooking;
    private BookingDto nextBooking;

    public AdvancedItemDto(Long id, Long ownerId, String name, String description, Boolean available,
                           Long requestId, BookingDto lastBooking,
                           BookingDto nextBooking, List<CommentDto> comments) {
        super(id, ownerId, name, description, available, requestId);
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
        this.comments = comments;
    }
}
