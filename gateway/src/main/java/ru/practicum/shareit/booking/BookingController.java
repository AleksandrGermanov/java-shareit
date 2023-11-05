package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.UnknownStateException;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.util.EndIsAfterStart;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Logging.logInfoIncomingRequest;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new UnknownStateException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findByItemOwnerAndByState(@RequestParam(defaultValue = "all") String state,
                                                            @RequestHeader("X-Sharer-User-Id") long itemOwnerId,
                                                            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                            @Positive @RequestParam(defaultValue = "20") int size) {
		BookingState stateValue = BookingState.from(state)
				.orElseThrow(() -> new UnknownStateException("Unknown state: " + state));
        logInfoIncomingRequest(log, "GET /bookings/owner", stateValue, itemOwnerId, from, size);
        return bookingClient.findByItemOwnerAndByState(itemOwnerId, stateValue, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @EndIsAfterStart @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(@PathVariable long bookingId, @RequestParam boolean approved,
                             @RequestHeader("X-Sharer-User-Id") long itemOwnerId) {
        logInfoIncomingRequest(log, "PATCH /bookings/{bookingId}", bookingId, approved, itemOwnerId);
        return bookingClient.update(bookingId, approved, itemOwnerId);
    }
}
