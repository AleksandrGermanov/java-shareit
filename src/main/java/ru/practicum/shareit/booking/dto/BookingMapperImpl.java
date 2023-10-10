package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.notFound.ItemNotFoundException;
import ru.practicum.shareit.item.dto.item.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

@Component
@RequiredArgsConstructor
public class BookingMapperImpl implements BookingMapper {
    private final ItemRepository itemRepository; //тут обращение напрямую к репозиторию
    // во избежание циклической зависимости.
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public BookingDto bookingToOutcomingDto(Booking booking) {
        return new OutcomingBookingDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getStatus(),
                itemMapper.itemToDto(booking.getItem()), userMapper.userToDto(booking.getBooker()));
    }

    @Override
    public BookingDto bookingToNestedInItemDtoDto(Booking booking) {
        return new IncomingAndNestedInItemDtoBookingDto(booking.getId(), booking.getStart(), booking.getEnd(),
                booking.getStatus(), booking.getItem().getId(), booking.getBooker().getId());
    }

    @Override
    public Booking bookingFromDto(IncomingAndNestedInItemDtoBookingDto dto) {
        long id = dto.getId() != null ? dto.getId() : 0L;
        BookingStatus status = dto.getStatus() != null ? dto.getStatus() : BookingStatus.WAITING;
        return new Booking(id, itemRepository.findById(dto.getItemId()).orElseThrow(() -> new ItemNotFoundException(
                "Не найден объект с id = " + dto.getItemId() + ".")),
                userService.findByIdOrThrow(dto.getBookerId()), dto.getStart(), dto.getEnd(), status);
    }
}
