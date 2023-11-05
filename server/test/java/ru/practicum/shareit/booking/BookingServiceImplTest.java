package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.SimpleBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.service.BookingState;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.ShareItException;
import ru.practicum.shareit.exception.alreadyExists.ApprovedAlreadyExistsException;
import ru.practicum.shareit.exception.alreadyExists.BookingAlreadyExistsException;
import ru.practicum.shareit.exception.mismatch.ItemOwnerOrBookerMismatchException;
import ru.practicum.shareit.exception.mismatch.OwnerMismatchException;
import ru.practicum.shareit.exception.notFound.BookingNotFoundException;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.PaginationInfo;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    private SimpleBookingDto bookingDto;
    private User user;
    private Item item;
    private Booking booking;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private UserService userService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private ItemService itemService;
    @Mock
    private ItemMapper itemMapper;
    @InjectMocks
    private BookingServiceImpl bookingService;

    @BeforeEach
    public void createObjects() {
        bookingDto = new SimpleBookingDto(0L,
                LocalDateTime.now().plusMinutes(15), LocalDateTime.now().plusHours(1),
                BookingStatus.WAITING, 0L, 999L);
        user = new User(999L, "n", "e@m.l");
        item = new Item(0L, new User(0L, "name", "e@ma.il"),
                "name", "description", true, null,
                Collections.emptyList());
        booking = new Booking(0L, item, user, bookingDto.getStart(), bookingDto.getEnd(),
                bookingDto.getStatus());
    }

    @Test
    public void methodCreateCallsMapperRepositoriesValidator() {
        when(itemService.findByIdOrThrow(0L)).thenReturn(item);
        when(userService.findByIdOrThrow(999L)).thenReturn(user);
        when(bookingMapper.bookingFromDto(bookingDto, item, user)).thenReturn(booking);
        when(bookingRepository.existsById(0L)).thenReturn(false);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(userMapper.userToDto(user)).thenReturn(new UserDto());
        when(itemMapper.itemToDto(item)).thenReturn(new ItemDto());
        when(bookingMapper.bookingToOutcomingDto(booking, new ItemDto(), new UserDto())).thenReturn(bookingDto);

        Assertions.assertEquals(bookingDto, bookingService.create(bookingDto, 999L));
    }

    @Test
    public void methodCreateWhenBookingAlreadyExistsThrowsException() {
        booking.setStart(LocalDateTime.now().minusHours(1));
        when(itemService.findByIdOrThrow(0L)).thenReturn(item);
        when(userService.findByIdOrThrow(999L)).thenReturn(user);
        when(bookingMapper.bookingFromDto(bookingDto, item, user)).thenReturn(booking);
        when(bookingRepository.existsById(0L)).thenReturn(true);

        Assertions.assertThrows(BookingAlreadyExistsException.class,
                () -> bookingService.create(bookingDto, 999L));
    }

    @Test
    public void methodCreateWhenItemNotAvailableThrowsException() {
        item.setAvailable(false);
        when(itemService.findByIdOrThrow(0L)).thenReturn(item);
        when(userService.findByIdOrThrow(999L)).thenReturn(user);
        when(bookingMapper.bookingFromDto(bookingDto, item, user)).thenReturn(booking);
        when(bookingRepository.existsById(0L)).thenReturn(false);

        Assertions.assertThrows(ItemNotAvailableException.class, () -> bookingService.create(bookingDto, 999L));
    }

    @Test
    public void methodCreateWhenOwnerIsBookerThrowsException() {
        item.setOwner(user);
        when(itemService.findByIdOrThrow(0L)).thenReturn(item);
        when(userService.findByIdOrThrow(999L)).thenReturn(user);
        when(bookingMapper.bookingFromDto(bookingDto, item, user)).thenReturn(booking);
        when(bookingRepository.existsById(0L)).thenReturn(false);

        Assertions.assertThrows(OwnerMismatchException.class, () -> bookingService.create(bookingDto, 999L));
    }

    @Test
    public void methodUpdateCallsBookingRepositoryValidator() {
        when(bookingRepository.findById(0L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(userMapper.userToDto(user)).thenReturn(new UserDto());
        when(itemMapper.itemToDto(item)).thenReturn(new ItemDto());
        when(bookingMapper.bookingToOutcomingDto(booking, new ItemDto(), new UserDto())).thenReturn(bookingDto);

        Assertions.assertEquals(bookingDto, bookingService.update(0L, true, 0L));
        Assertions.assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }

    @Test
    public void methodUpdateWhenOwnerMismatchedThrowsException() {
        when(bookingRepository.findById(0L)).thenReturn(Optional.of(booking));

        Assertions.assertThrows(OwnerMismatchException.class, () -> bookingService
                .update(0L, true, 999L));
    }

    @Test
    public void methodUpdateWhenBookingNotFoundThrowsException() {
        when(bookingRepository.findById(0L)).thenReturn(Optional.empty());

        Assertions.assertThrows(BookingNotFoundException.class, () -> bookingService
                .update(0L, true, 0L));
    }

    @Test
    public void methodUpdateWhenAlreadyApprovedThrowsException() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(0L)).thenReturn(Optional.of(booking));

        Assertions.assertThrows(ApprovedAlreadyExistsException.class, () -> bookingService
                .update(0L, true, 0L));
    }

    @Test
    public void methodRetrieveCallsBookingRepository() {
        when(bookingRepository.findById(0L)).thenReturn(Optional.of(booking));
        when(userMapper.userToDto(user)).thenReturn(new UserDto());
        when(itemMapper.itemToDto(item)).thenReturn(new ItemDto());
        when(bookingMapper.bookingToOutcomingDto(booking, new ItemDto(), new UserDto())).thenReturn(bookingDto);

        Assertions.assertEquals(bookingDto, bookingService.retrieve(0L, 0L));
    }

    @Test
    public void methodRetrieveWhenItemOwnerAndBookerMismatchedThrowsException() {
        when(bookingRepository.findById(0L)).thenReturn(Optional.of(booking));

        Assertions.assertThrows(ShareItException.class,
                () -> bookingService.retrieve(0L, 888L));
        Assertions.assertThrows(ItemOwnerOrBookerMismatchException.class,
                () -> bookingService.retrieve(0L, 888L));
    }


    @Test
    public void methodFindByBookerAndByStateValueAllCallsRepositoryMethod() {
        PaginationInfo info = new PaginationInfo(0, 10);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(0L, info.asPageRequest()))
                .thenReturn(Page.empty());

        Assertions.assertEquals(Collections.emptyList(),
                bookingService.findByBookerAndByState(BookingState.ALL, 0L, 0, 10));
        verify(userService, times(1)).throwIfRepositoryNotContains(0L);
    }

    @Test
    public void methodFindByBookerAndByStateValueCurrentCallsRepositoryMethod() {
        PaginationInfo info = new PaginationInfo(0, 10);
        when(bookingRepository.findCurrentByBooker(anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(userMapper.userToDto(any())).thenReturn(new UserDto());
        when(itemMapper.itemToDto(any())).thenReturn(new ItemDto());
        when(bookingMapper.bookingToOutcomingDto(booking, new ItemDto(), new UserDto()))
                .thenReturn(bookingDto);

        Assertions.assertEquals(List.of(bookingDto),
                bookingService.findByBookerAndByState(BookingState.CURRENT, 0L, 0, 10));
        verify(userService, times(1)).throwIfRepositoryNotContains(0L);
    }

    @Test
    public void methodFindByBookerAndByStateValueFutureCallsRepositoryMethod() {
        PaginationInfo info = new PaginationInfo(0, 10);
        when(bookingRepository.findAllByBookerIdAndStatusInAndStartIsAfterOrderByStartDesc(anyLong(),
                any(BookingStatus[].class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(userMapper.userToDto(any())).thenReturn(new UserDto());
        when(itemMapper.itemToDto(any())).thenReturn(new ItemDto());
        when(bookingMapper.bookingToOutcomingDto(booking, new ItemDto(), new UserDto()))
                .thenReturn(bookingDto);

        Assertions.assertEquals(List.of(bookingDto),
                bookingService.findByBookerAndByState(BookingState.FUTURE, 0L, 0, 10));
        verify(userService, times(1)).throwIfRepositoryNotContains(0L);
    }

    @Test
    public void methodFindByBookerAndByStateValuePastCallsRepositoryMethod() {
        PaginationInfo info = new PaginationInfo(0, 10);
        when(bookingRepository.findAllByBookerIdAndStatusInAndEndIsBeforeOrderByStartDesc(anyLong(),
                any(BookingStatus[].class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(userMapper.userToDto(any())).thenReturn(new UserDto());
        when(itemMapper.itemToDto(any())).thenReturn(new ItemDto());
        when(bookingMapper.bookingToOutcomingDto(booking, new ItemDto(), new UserDto()))
                .thenReturn(bookingDto);

        Assertions.assertEquals(List.of(bookingDto),
                bookingService.findByBookerAndByState(BookingState.PAST, 0L, 0, 10));
        verify(userService, times(1)).throwIfRepositoryNotContains(0L);
    }

    @Test
    public void methodFindByBookerAndByStateValueWaitingCallsRepositoryMethod() {
        PaginationInfo info = new PaginationInfo(0, 10);
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(0L,
                BookingStatus.WAITING)).thenReturn(List.of(booking));
        when(userMapper.userToDto(any())).thenReturn(new UserDto());
        when(itemMapper.itemToDto(any())).thenReturn(new ItemDto());
        when(bookingMapper.bookingToOutcomingDto(booking, new ItemDto(), new UserDto()))
                .thenReturn(bookingDto);

        Assertions.assertEquals(List.of(bookingDto),
                bookingService.findByBookerAndByState(BookingState.WAITING, 0L, 0, 10));
        verify(userService, times(1)).throwIfRepositoryNotContains(0L);
    }

    @Test
    public void methodFindByBookerAndByStateValueRejectedCallsRepositoryMethod() {
        PaginationInfo info = new PaginationInfo(0, 10);
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(0L,
                BookingStatus.REJECTED)).thenReturn(List.of(booking));
        when(userMapper.userToDto(any())).thenReturn(new UserDto());
        when(itemMapper.itemToDto(any())).thenReturn(new ItemDto());
        when(bookingMapper.bookingToOutcomingDto(booking, new ItemDto(), new UserDto()))
                .thenReturn(bookingDto);

        Assertions.assertEquals(List.of(bookingDto),
                bookingService.findByBookerAndByState(BookingState.REJECTED, 0L, 0, 10));
        verify(userService, times(1)).throwIfRepositoryNotContains(0L);
    }

    @Test
    public void methodFindByOwnerAndByStateValueAllCallsRepositoryMethod() {
        PaginationInfo info = new PaginationInfo(0, 10);
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(0L, info.asPageRequest()))
                .thenReturn(Page.empty());

        Assertions.assertEquals(Collections.emptyList(),
                bookingService.findByItemOwnerAndByState(BookingState.ALL, 0L, 0, 10));
        verify(userService, times(1)).throwIfRepositoryNotContains(0L);
    }

    @Test
    public void methodFindByOwnerAndByStateValueCurrentCallsRepositoryMethod() {
        PaginationInfo info = new PaginationInfo(0, 10);
        when(bookingRepository.findCurrentByItemOwner(anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(userMapper.userToDto(any())).thenReturn(new UserDto());
        when(itemMapper.itemToDto(any())).thenReturn(new ItemDto());
        when(bookingMapper.bookingToOutcomingDto(booking, new ItemDto(), new UserDto()))
                .thenReturn(bookingDto);

        Assertions.assertEquals(List.of(bookingDto),
                bookingService.findByItemOwnerAndByState(BookingState.CURRENT, 0L, 0, 10));
        verify(userService, times(1)).throwIfRepositoryNotContains(0L);
    }

    @Test
    public void methodFindByOwnerAndByStateValueFutureCallsRepositoryMethod() {
        PaginationInfo info = new PaginationInfo(0, 10);
        when(bookingRepository.findAllByItemOwnerIdAndStatusInAndStartIsAfterOrderByStartDesc(anyLong(),
                any(BookingStatus[].class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(userMapper.userToDto(any())).thenReturn(new UserDto());
        when(itemMapper.itemToDto(any())).thenReturn(new ItemDto());
        when(bookingMapper.bookingToOutcomingDto(booking, new ItemDto(), new UserDto()))
                .thenReturn(bookingDto);

        Assertions.assertEquals(List.of(bookingDto),
                bookingService.findByItemOwnerAndByState(BookingState.FUTURE, 0L, 0, 10));
        verify(userService, times(1)).throwIfRepositoryNotContains(0L);
    }

    @Test
    public void methodFindByOwnerAndByStateValuePastCallsRepositoryMethod() {
        PaginationInfo info = new PaginationInfo(0, 10);
        when(bookingRepository.findAllByItemOwnerIdAndStatusInAndEndIsBeforeOrderByStartDesc(anyLong(),
                any(BookingStatus[].class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(userMapper.userToDto(any())).thenReturn(new UserDto());
        when(itemMapper.itemToDto(any())).thenReturn(new ItemDto());
        when(bookingMapper.bookingToOutcomingDto(booking, new ItemDto(), new UserDto()))
                .thenReturn(bookingDto);

        Assertions.assertEquals(List.of(bookingDto),
                bookingService.findByItemOwnerAndByState(BookingState.PAST, 0L, 0, 10));
        verify(userService, times(1)).throwIfRepositoryNotContains(0L);
    }

    @Test
    public void methodFindByOwnerAndByStateValueWaitingCallsRepositoryMethod() {
        PaginationInfo info = new PaginationInfo(0, 10);
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(0L,
                BookingStatus.WAITING)).thenReturn(List.of(booking));
        when(userMapper.userToDto(any())).thenReturn(new UserDto());
        when(itemMapper.itemToDto(any())).thenReturn(new ItemDto());
        when(bookingMapper.bookingToOutcomingDto(booking, new ItemDto(), new UserDto()))
                .thenReturn(bookingDto);

        Assertions.assertEquals(List.of(bookingDto),
                bookingService.findByItemOwnerAndByState(BookingState.WAITING, 0L, 0, 10));
        verify(userService, times(1)).throwIfRepositoryNotContains(0L);
    }

    @Test
    public void methodFindByOwnerAndByStateValueRejectedCallsRepositoryMethod() {
        PaginationInfo info = new PaginationInfo(0, 10);
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(0L,
                BookingStatus.REJECTED)).thenReturn(List.of(booking));
        when(userMapper.userToDto(any())).thenReturn(new UserDto());
        when(itemMapper.itemToDto(any())).thenReturn(new ItemDto());
        when(bookingMapper.bookingToOutcomingDto(booking, new ItemDto(), new UserDto()))
                .thenReturn(bookingDto);

        Assertions.assertEquals(List.of(bookingDto),
                bookingService.findByItemOwnerAndByState(BookingState.REJECTED, 0L, 0, 10));
        verify(userService, times(1)).throwIfRepositoryNotContains(0L);
    }
}