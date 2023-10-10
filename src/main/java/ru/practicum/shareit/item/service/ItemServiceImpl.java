package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.Util.ShareItValidator;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.alreadyExists.ItemAlreadyExistsException;
import ru.practicum.shareit.exception.mismatch.OwnerMismatchException;
import ru.practicum.shareit.exception.notFound.BookingForCommentNotFoundException;
import ru.practicum.shareit.exception.notFound.ItemNotFoundException;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentMapper;
import ru.practicum.shareit.item.dto.comment.IncomingCommentDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.dto.item.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final ShareItValidator shareItValidator;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto create(ItemDto itemDto) {
        Item itemFromDto = itemMapper.itemFromDto(itemDto);
        throwIfRepositoryContains(itemFromDto.getId());
        shareItValidator.validate(itemFromDto);
        userService.throwIfRepositoryNotContains(itemFromDto.getOwnerId());
        return itemMapper.itemToDto(itemRepository.save(itemFromDto));
    }

    @Override
    public CommentDto create(IncomingCommentDto commentDto) {
        Comment comment = commentMapper.commentFromDto(commentDto);
        throwIfBookingForCommentNotFound(comment);
        shareItValidator.validate(comment);
        return commentMapper.commentToDto(commentRepository.save(comment));
    }

    @Override
    public List<ItemDtoWithBookingsAndComments> findAllByOwner(@Valid @NotNull long ownerId) {
        userService.throwIfRepositoryNotContains(ownerId);
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(this::mapItemToDtoWithBookingsAndComments)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchByText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        text = '%' + text + '%';
        return itemRepository.findByText(text).stream()
                .map(this::mapItemToDtoWithBookingsAndComments)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto retrieve(long id, long requesterId) {
        Item item = findByIdOrThrow(id);
        userService.findByIdOrThrow(requesterId);
        if (requesterId == item.getOwnerId()) {
            return mapItemToDtoWithBookingsAndComments(findByIdOrThrow(id));
        } else {
            return itemMapper.itemToDtoWithBookingsAndComments(item, null, null);
        }
    }

    @Override
    public ItemDto update(ItemDto itemDto) {
        throwIfRepositoryNotContains(itemDto.getId());
        Item itemToUpdate = findByIdOrThrow(itemDto.getId());
        throwIfOwnerMismatched(itemToUpdate.getOwnerId(), itemDto.getOwnerId());
        mergeDtoIntoExistingItem(itemDto, itemToUpdate);
        shareItValidator.validate(itemToUpdate);
        return itemMapper.itemToDto(itemRepository.save(itemToUpdate));
    }

    @Override
    public void delete(long id) {
        throwIfRepositoryNotContains(id);
        itemRepository.deleteById(id);
    }

    private void throwIfRepositoryNotContains(long id) throws ItemNotFoundException {
        if (!itemRepository.existsById(id)) {
            throw new ItemNotFoundException("Предмет с id = " + id + " не найден.");
        }
    }

    public Item findByIdOrThrow(long id) throws ItemNotFoundException {
        return itemRepository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("Предмет с id = " + id + " не найден."));
    }

    private void throwIfRepositoryContains(long id) throws ItemAlreadyExistsException {
        if (itemRepository.existsById(id)) {
            throw new ItemAlreadyExistsException("Предмет с id = " + id + " уже существует. "
                    + "Попробуйте изменить передаваемые данные или используйте подходящий метод.");
        }
    }

    private void throwIfOwnerMismatched(long inRepositoryOwnersId, long inDtoOwnersId) {
        if (inRepositoryOwnersId != inDtoOwnersId) {
            throw new OwnerMismatchException("Редактировать запрошенный предмет может только его владелец");
        }
    }

    private void throwIfBookingForCommentNotFound(Comment comment) {
        if (!Integer.valueOf(1).equals(
                bookingRepository.lastCurrentBookingExistsForCommentAuthor(comment.getItem(),
                        comment.getAuthor(), LocalDateTime.now())
        )) {
            throw new BookingForCommentNotFoundException(String.format("Для пользователя с id = %d "
                            + "не найдено текущего или прошедшего бронирования предмета с id = %d",
                    comment.getAuthor().getId(), comment.getItem().getId()));
        }
    }

    private void mergeDtoIntoExistingItem(ItemDto updated, Item beforeUpdate) {
        if (updated.getOwnerId() != null) {
            beforeUpdate.setOwnerId(updated.getOwnerId());
        }
        if (updated.getName() != null) {
            beforeUpdate.setName(updated.getName());
        }
        if (updated.getDescription() != null) {
            beforeUpdate.setDescription(updated.getDescription());
        }
        if (updated.getAvailable() != null) {
            beforeUpdate.setAvailable(updated.getAvailable());
        }
        if (updated.getRequestId() != null) {
            beforeUpdate.setRequestId(updated.getRequestId());
        }
    }

    private ItemDtoWithBookingsAndComments mapItemToDtoWithBookingsAndComments(Item item) {
        Booking lastBooking = bookingRepository.findFirstByItemAndStatusInAndStartBeforeOrderByStartDesc(item,
                new BookingStatus[]{BookingStatus.APPROVED, BookingStatus.WAITING},
                LocalDateTime.now());
        Booking nextBooking = bookingRepository.findFirstByItemAndStatusInAndStartAfterOrderByStartAsc(item,
                new BookingStatus[]{BookingStatus.APPROVED, BookingStatus.WAITING},
                LocalDateTime.now());
        BookingDto lastBookingDto = lastBooking != null
                ? bookingMapper.bookingToNestedInItemDtoDto(lastBooking)
                : null;
        BookingDto nextBookingDto = nextBooking != null
                ? bookingMapper.bookingToNestedInItemDtoDto(nextBooking)
                : null;
        return itemMapper.itemToDtoWithBookingsAndComments(item, lastBookingDto, nextBookingDto);
    }
}

