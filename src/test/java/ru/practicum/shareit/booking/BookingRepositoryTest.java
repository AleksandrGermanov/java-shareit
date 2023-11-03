package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingRepositoryTest {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private User booker = new User(null, "n", "e@m.l");
    private User owner = new User(null, "name", "e@ma.il");
    private Item item = new Item(null, owner,
            "name", "description", true, null,
            Collections.emptyList());
    private Booking past = new Booking(null, item, booker, LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusHours(12), BookingStatus.APPROVED);
    private Booking current = new Booking(null, item, booker, LocalDateTime.now().minusHours(2),
            LocalDateTime.now().plusHours(12), BookingStatus.APPROVED);
    private Booking future = new Booking(null, item, booker, LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2), BookingStatus.APPROVED);

    @BeforeEach
    public void putUsersAndItemToDb() {
        booker = userRepository.save(booker);
        owner = userRepository.save(owner);
        item = itemRepository.save(item);
        past = bookingRepository.save(past);
        current = bookingRepository.save(current);
        future = bookingRepository.save(future);
    }

    @Test
    public void methodFindCurrentByBookerReturnsListOfCurrentBooking() {
        Assertions.assertEquals(List.of(current), bookingRepository.findCurrentByBooker(booker.getId(),
                LocalDateTime.now()));
    }

    @Test
    public void methodFindCurrentByItemOwnerReturnsListOfCurrentBooking() {
        Assertions.assertEquals(List.of(current), bookingRepository.findCurrentByItemOwner(owner.getId(),
                LocalDateTime.now()));
    }

    @Test
    public void methodLastCurrentBookingExistsForCommentAuthorReturns1OrNull() {
        Assertions.assertEquals(1, bookingRepository.lastCurrentBookingExistsForCommentAuthor(item, booker,
                LocalDateTime.now()));
        Assertions.assertNull(bookingRepository.lastCurrentBookingExistsForCommentAuthor(item, owner,
                LocalDateTime.now()));
    }
}
