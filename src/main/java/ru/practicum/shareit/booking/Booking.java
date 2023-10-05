package ru.practicum.shareit.booking;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
public class Booking {
    @EqualsAndHashCode.Include
    private final Long id;
    private final Long itemId;
    private final Long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;

    public enum BookingStatus {
        WAITING("Ожидает одобрения."),
        APPROVED("Бронирование подтверждено владельцем."),
        REJECTED("Бронирование отклонено владельцем."),
        CANCELED("Бронирование отменено создателем.");
        @Getter
        private final String description;

        BookingStatus(String description) {
            this.description = description;
        }
    }
}
