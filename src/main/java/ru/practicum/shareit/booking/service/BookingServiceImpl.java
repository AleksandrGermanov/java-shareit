package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.SimpleBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.alreadyExists.ApprovedAlreadyExistsException;
import ru.practicum.shareit.exception.alreadyExists.BookingAlreadyExistsException;
import ru.practicum.shareit.exception.mismatch.ItemOwnerOrBookerMismatchException;
import ru.practicum.shareit.exception.mismatch.OwnerMismatchException;
import ru.practicum.shareit.exception.mismatch.TimeMismatchException;
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
import ru.practicum.shareit.util.ShareItValidator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ShareItValidator shareItValidator;
    private final UserService userService;
    private final UserMapper userMapper;
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @Transactional
    @Override
    public BookingDto create(SimpleBookingDto bookingDto, long bookerId) {
        bookingDto.setBookerId(bookerId);
        Booking booking = mapBookingFromSimpleDto(bookingDto);
        throwIfRepositoryContains(booking.getId());
        shareItValidator.validate(booking);
        throwIfStartIsInPast(booking);
        throwIfEndIsBeforeStart(booking);
        throwIfItemNotAvailable(booking);
        throwIfItemOwnerIsBooker(booking);
        return mapBookingToOutcomingDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingDto update(long bookingId, boolean approved, long itemOwnerId) {
        Booking booking = findByIdOrThrow(bookingId);
        throwIfItemOwnerMismatched(booking, itemOwnerId);
        throwIfAlreadyApproved(booking);
        defineBookingStatusWithApprovedValue(booking, approved);
        return mapBookingToOutcomingDto(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto retrieve(long bookingId, long itemOwnerOrBookerId) {
        Booking booking = findByIdOrThrow(bookingId);
        throwIfItemOwnerAndBookerMismatched(booking, itemOwnerOrBookerId);
        return mapBookingToOutcomingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> findByBookerAndByState(BookingState state, long bookerId, int from, int size) {
        userService.throwIfRepositoryNotContains(bookerId);
        PaginationInfo info = new PaginationInfo(from, size);
        shareItValidator.validate(info);
        switch (state) {
            case ALL: {
                return bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId, info.asPageRequest())
                        .stream()
                        .map(this::mapBookingToOutcomingDto)
                        .collect(Collectors.toList());
            }
            case CURRENT: {
                return bookingRepository.findCurrentByBooker(
                                bookerId, LocalDateTime.now()).stream()
                        .map(this::mapBookingToOutcomingDto)
                        .collect(Collectors.toList());
            }
            case FUTURE: {
                return bookingRepository.findAllByBookerIdAndStatusInAndStartIsAfterOrderByStartDesc(
                                bookerId, new BookingStatus[]{BookingStatus.APPROVED, BookingStatus.WAITING},
                                LocalDateTime.now()).stream()
                        .map(this::mapBookingToOutcomingDto)
                        .collect(Collectors.toList());
            }
            case PAST: {
                return bookingRepository.findAllByBookerIdAndStatusInAndEndIsBeforeOrderByStartDesc(
                                bookerId, new BookingStatus[]{BookingStatus.APPROVED, BookingStatus.WAITING},
                                LocalDateTime.now()).stream()
                        .map(this::mapBookingToOutcomingDto)
                        .collect(Collectors.toList());
            }
            case WAITING: {
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING)
                        .stream()
                        .map(this::mapBookingToOutcomingDto)
                        .collect(Collectors.toList());
            }
            case REJECTED: {
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED)
                        .stream()
                        .map(this::mapBookingToOutcomingDto)
                        .collect(Collectors.toList());
            }
            default: {
                return Collections.emptyList();
            }
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> findByItemOwnerAndByState(BookingState state, long itemOwnerId, int from, int size) {
        userService.throwIfRepositoryNotContains(itemOwnerId);
        PaginationInfo info = new PaginationInfo(from, size);
        shareItValidator.validate(info);
        switch (state) {
            case ALL: {
                return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(itemOwnerId, info.asPageRequest())
                        .stream()
                        .map(this::mapBookingToOutcomingDto)
                        .collect(Collectors.toList());
            }
            case CURRENT: {
                return bookingRepository.findCurrentByItemOwner(
                                itemOwnerId, LocalDateTime.now()).stream()
                        .map(this::mapBookingToOutcomingDto)
                        .collect(Collectors.toList());
            }
            case FUTURE: {
                return bookingRepository.findAllByItemOwnerIdAndStatusInAndStartIsAfterOrderByStartDesc(
                                itemOwnerId, new BookingStatus[]{BookingStatus.APPROVED, BookingStatus.WAITING},
                                LocalDateTime.now()).stream()
                        .map(this::mapBookingToOutcomingDto)
                        .collect(Collectors.toList());
            }
            case PAST: {
                return bookingRepository.findAllByItemOwnerIdAndStatusInAndEndIsBeforeOrderByStartDesc(
                                itemOwnerId, new BookingStatus[]{BookingStatus.APPROVED},
                                LocalDateTime.now()).stream()
                        .map(this::mapBookingToOutcomingDto)
                        .collect(Collectors.toList());
            }
            case WAITING: {
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(itemOwnerId,
                                BookingStatus.WAITING)
                        .stream()
                        .map(this::mapBookingToOutcomingDto)
                        .collect(Collectors.toList());
            }
            case REJECTED: {
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(itemOwnerId,
                                BookingStatus.REJECTED)
                        .stream()
                        .map(this::mapBookingToOutcomingDto)
                        .collect(Collectors.toList());
            }
            default: {
                return Collections.emptyList();
            }
        }
    }

    private void throwIfRepositoryContains(long id) throws BookingAlreadyExistsException {
        if (bookingRepository.existsById(id)) {
            throw new BookingAlreadyExistsException("Бронирование с id = " + id + " уже существует. "
                    + "Попробуйте изменить передаваемые данные или используйте подходящий метод.");
        }
    }

    private Booking findByIdOrThrow(long id) throws BookingNotFoundException {
        return bookingRepository.findById(id).orElseThrow(
                () -> new BookingNotFoundException("Бронирование с id = " + id + " не найдено."));
    }

    private void defineBookingStatusWithApprovedValue(Booking booking, boolean approved) {
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
    }

    private void throwIfItemOwnerMismatched(Booking booking, long itemOwnerId) throws OwnerMismatchException {
        if (booking.getItem().getOwner().getId() != itemOwnerId) {
            throw new OwnerMismatchException("Редактировать статус бронирования может только владелец предмета.");
        }
    }

    private void throwIfItemOwnerIsBooker(Booking booking) throws OwnerMismatchException {
        if (booking.getItem().getOwner().getId().equals(booking.getBooker().getId())) {
            throw new OwnerMismatchException("Нельзя создавать бронирование для своих предметов.");
        }
    }

    private void throwIfItemOwnerAndBookerMismatched(Booking booking, long itemOwnerOrBookerId)
            throws ItemOwnerOrBookerMismatchException {
        if (booking.getItem().getOwner().getId() != itemOwnerOrBookerId
                && booking.getBooker().getId() != itemOwnerOrBookerId) {
            throw new ItemOwnerOrBookerMismatchException(
                    "Запрашивать данные о бронировании могут только владелец предмета или создатель бронирования.");
        }
    }

    private void throwIfItemNotAvailable(Booking booking) {
        if (!booking.getItem().getAvailable()) {
            throw new ItemNotAvailableException(String.format("Вещь с id = %d недоступна для аренды.",
                    booking.getItem().getId()));
        }
    }

    private void throwIfStartIsInPast(Booking booking) throws TimeMismatchException {
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new TimeMismatchException("Время старта нового бронирования не может быть в прошлом.");
        }
    }

    private void throwIfEndIsBeforeStart(Booking booking) throws TimeMismatchException {
        if (booking.getStart().equals(booking.getEnd()) || booking.getStart().isAfter(booking.getEnd())) {
            throw new TimeMismatchException("Время окончания бронирования должно быть после времени его начала.");
        }
    }

    private void throwIfAlreadyApproved(Booking booking) throws ApprovedAlreadyExistsException {
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ApprovedAlreadyExistsException("После подтверждения бронирования менять статус запрещено.");
        }
    }

    private Booking mapBookingFromSimpleDto(SimpleBookingDto dto) {
        User booker = userService.findByIdOrThrow(dto.getBookerId());
        Item item = itemService.findByIdOrThrow(dto.getItemId());
        return bookingMapper.bookingFromDto(dto, item, booker);
    }

    private BookingDto mapBookingToOutcomingDto(Booking booking) {
        UserDto bookerDto = userMapper.userToDto(booking.getBooker());
        ItemDto itemDto = itemMapper.itemToDto(booking.getItem());
        return bookingMapper.bookingToOutcomingDto(booking, itemDto, bookerDto);
    }
}
