package ru.practicum.shareit.booking.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
public class SimpleBookingDto extends BookingDto {
    private Long itemId;
    private Long bookerId;

    public SimpleBookingDto(long id, LocalDateTime start, LocalDateTime end, BookingStatus status,
                            long itemId, long bookerId) {
        super(id, start, end, status);
        this.bookerId = bookerId;
        this.itemId = itemId;
    }
}
