package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.SimpleBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingState;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestBody SimpleBookingDto bookingDto, @RequestHeader("X-Sharer-User-Id") long bookerId) {
        return bookingService.create(bookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable long bookingId, @RequestParam boolean approved,
                             @RequestHeader("X-Sharer-User-Id") long itemOwnerId) {
        return bookingService.update(bookingId, approved, itemOwnerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto retrieve(@PathVariable long bookingId,
                               @RequestHeader("X-Sharer-User-Id") long itemOwnerOrBookerId) {
        return bookingService.retrieve(bookingId, itemOwnerOrBookerId);
    }

    @GetMapping
    public List<BookingDto> findByBookerAndByState(@RequestParam(defaultValue = "all") BookingState state,
                                                   @RequestHeader("X-Sharer-User-Id") long bookerId) {
        return bookingService.findByBookerAndByState(state, bookerId);
    }

    @GetMapping("/owner")
    public List<BookingDto> findByItemOwnerAndByState(@RequestParam(defaultValue = "all") BookingState state,
                                                      @RequestHeader("X-Sharer-User-Id") long itemOwnerId) {
        return bookingService.findByItemOwnerAndByState(state, itemOwnerId);
    }
}
