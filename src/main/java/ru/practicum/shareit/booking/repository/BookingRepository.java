package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long itemOwnerId);

    @Query("SELECT b FROM Booking b "
            + "JOIN FETCH b.booker "
            + "WHERE b.booker.id = :bookerId "
            + "AND b.start < :now "
            + "AND b.end > :now "
            + "ORDER BY b.start DESC")
    List<Booking> findCurrentByBooker(@Param("bookerId") long bookerId,
                                      @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b "
            + "JOIN FETCH b.item "
            + "WHERE b.item.owner.id = :itemOwnerId "
            + "AND b.start < :now "
            + "AND b.end > :now "
            + "ORDER BY b.start ASC")
    List<Booking> findCurrentByItemOwner(@Param("itemOwnerId") long itemOwnerId,
                                         @Param("now") LocalDateTime now);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long bookerId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long itemOwnerId, BookingStatus status);

    List<Booking> findAllByBookerIdAndStatusInAndStartIsAfterOrderByStartDesc(long bookerId, BookingStatus[] status,
                                                                              LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStatusInAndStartIsAfterOrderByStartDesc(long itemOwnerId,
                                                                                 BookingStatus[] status,
                                                                                 LocalDateTime now);

    List<Booking> findAllByBookerIdAndStatusInAndEndIsBeforeOrderByStartDesc(long bookerId, BookingStatus[] status,
                                                                             LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStatusInAndEndIsBeforeOrderByStartDesc(long itemOwnerId,
                                                                                BookingStatus[] status,
                                                                                LocalDateTime now);


    Booking findFirstByItemAndStatusInAndStartBeforeOrderByStartDesc(Item item, BookingStatus[] status,
                                                                     LocalDateTime now);

    Booking findFirstByItemAndStatusInAndStartAfterOrderByStartAsc(Item item, BookingStatus[] status,
                                                                   LocalDateTime now);

    @Query("SELECT 1 FROM Booking WHERE EXISTS("
            + "SELECT b FROM Booking b "
            + "JOIN b.item i "
            + "JOIN b.booker u "
            + "WHERE i = :item "
            + "AND u = :author "
            + "AND b.start < :now "
            + "AND b.status = 'APPROVED'"
            + ")")
    Integer lastCurrentBookingExistsForCommentAuthor(@Param("item") Item item,
                                                     @Param("author") User author,
                                                     @Param("now") LocalDateTime now);
}
