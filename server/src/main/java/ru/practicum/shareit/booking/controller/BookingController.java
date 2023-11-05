package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.SimpleBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingState;

import java.util.List;

import static ru.practicum.shareit.util.Logging.logInfoIncomingRequest;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestBody SimpleBookingDto bookingDto,
                             @RequestHeader("X-Sharer-User-Id") long bookerId) {
        logInfoIncomingRequest(log, "POST /bookings", bookingDto, bookerId);
        return bookingService.create(bookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable long bookingId, @RequestParam boolean approved,
                             @RequestHeader("X-Sharer-User-Id") long itemOwnerId) {
        logInfoIncomingRequest(log, "PATCH /bookings/{bookingId}", bookingId, approved, itemOwnerId);

        return bookingService.update(bookingId, approved, itemOwnerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto retrieve(@PathVariable long bookingId,
                               @RequestHeader("X-Sharer-User-Id") long itemOwnerOrBookerId) {
        logInfoIncomingRequest(log, "GET /bookings/{bookingId}", bookingId, itemOwnerOrBookerId);

        return bookingService.retrieve(bookingId, itemOwnerOrBookerId);
    }

    @GetMapping
    public List<BookingDto> findByBookerAndByState(@RequestParam(defaultValue = "all") BookingState state,
                                                   @RequestHeader("X-Sharer-User-Id") long bookerId,
                                                   @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "20") int size) {
        logInfoIncomingRequest(log, "GET /bookings", state, bookerId, from, size);

        return bookingService.findByBookerAndByState(state, bookerId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> findByItemOwnerAndByState(@RequestParam(defaultValue = "all") BookingState state,
                                                      @RequestHeader("X-Sharer-User-Id") long itemOwnerId,
                                                      @RequestParam(defaultValue = "0") int from,
                                                      @RequestParam(defaultValue = "20") int size) {
        logInfoIncomingRequest(log, "GET /bookings/owner", state, itemOwnerId, from, size);
        return bookingService.findByItemOwnerAndByState(state, itemOwnerId, from, size);
    }
}
