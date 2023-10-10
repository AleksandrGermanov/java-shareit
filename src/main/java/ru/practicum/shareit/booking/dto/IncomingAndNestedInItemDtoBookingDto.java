package ru.practicum.shareit.booking.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class IncomingAndNestedInItemDtoBookingDto extends BookingDto {

    private Long itemId;
    private Long bookerId;
    public IncomingAndNestedInItemDtoBookingDto(long id, LocalDateTime start, LocalDateTime end, BookingStatus status,
                                                long itemId, long bookerId) {
        super(id, start, end, status);
        this.bookerId = bookerId;
        this.itemId = itemId;
    }
}
