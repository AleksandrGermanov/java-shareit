package ru.practicum.shareit.booking.service;

/**
 * Состояние бронирования не является частью сущности, а представляет собой сложный признак,
 * вычисляемый для каждого бронирования на основе статуса и времени начала и окончания бронирования.
 * All - все возможные комбинации;
 * Current - start < 'now', end > 'now';
 * Future - статус "WAITING","APPROVED", start > 'now';
 * Past - статус "APPROVED", end < 'now';
 * Rejected - статус "REJECTED";
 * Waiting - статус "WAITING".
 */
public enum BookingState {
    ALL,
    CURRENT,
    FUTURE,
    PAST,
    REJECTED,
    WAITING
}
