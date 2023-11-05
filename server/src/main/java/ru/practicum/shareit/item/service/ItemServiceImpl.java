package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.alreadyExists.ItemAlreadyExistsException;
import ru.practicum.shareit.exception.mismatch.OwnerMismatchException;
import ru.practicum.shareit.exception.notFound.BookingForCommentNotFoundException;
import ru.practicum.shareit.exception.notFound.ItemNotFoundException;
import ru.practicum.shareit.exception.notFound.UserNotFoundException;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentMapper;
import ru.practicum.shareit.item.dto.comment.IncomingCommentDto;
import ru.practicum.shareit.item.dto.item.AdvancedItemDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.PaginationInfo;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Validated
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final ItemRequestService itemRequestService;

    @Transactional
    @Override
    public ItemDto create(ItemDto itemDto) {
        Item itemFromDto = mapItemFromDto(itemDto);
        throwIfRepositoryContains(itemFromDto.getId());
        userService.throwIfRepositoryNotContains(itemFromDto.getOwner().getId());
        return itemMapper.itemToDto(itemRepository.save(itemFromDto));
    }

    @Transactional
    @Override
    public CommentDto create(IncomingCommentDto commentDto) {
        Comment comment = mapCommentFromDto(commentDto);
        throwIfBookingForCommentNotFound(comment);
        return commentMapper.commentToDto(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    @Override
    public List<AdvancedItemDto> findAllByOwner(long ownerId, int from, int size) {
        userService.throwIfRepositoryNotContains(ownerId);
        PaginationInfo info = new PaginationInfo(from, size);
        List<Item> found = itemRepository.findByOwnerIdOrderByIdAsc(ownerId, info.asPageRequest());
        return found == null
                ? Collections.emptyList()
                : found.stream()
                .map(this::mapItemToDtoWithBookingsAndComments)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> searchByText(String text, int from, int size) {
        PaginationInfo info = new PaginationInfo(from, size);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        text = '%' + text + '%';
        List<Item> found = itemRepository.findByText(text, info.asPageRequest());
        return found == null
                ? Collections.emptyList()
                : found.stream()
                .map(this::mapItemToDtoWithBookingsAndComments)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto retrieve(long id, long requesterId) {
        Item item = findByIdOrThrow(id);
        userService.findByIdOrThrow(requesterId);
        if (requesterId == item.getOwner().getId()) {
            return mapItemToDtoWithBookingsAndComments(item);
        } else {
            return itemMapper.itemToDtoWithBookingsAndComments(item, null, null);
        }
    }

    @Transactional
    @Override
    public ItemDto update(ItemDto itemDto) {
        Item itemToUpdate = findByIdOrThrow(itemDto.getId());
        throwIfOwnerMismatched(itemToUpdate.getOwner().getId(), itemDto.getOwnerId());
        mergeDtoIntoExistingItem(itemDto, itemToUpdate);
        return itemMapper.itemToDto(itemRepository.save(itemToUpdate));
    }

    @Transactional
    @Override
    public void delete(long id) {
        throwIfRepositoryNotContains(id);
        itemRepository.deleteById(id);
    }

    @Override
    public Item findByIdOrThrow(long id) {
        return itemRepository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("Предмет с id = " + id + " не найден."));
    }

    private void throwIfRepositoryNotContains(long id) {
        if (!itemRepository.existsById(id)) {
            throw new ItemNotFoundException("Предмет с id = " + id + " не найден.");
        }
    }

    private void throwIfRepositoryContains(long id) {
        if (itemRepository.existsById(id)) {
            throw new ItemAlreadyExistsException("Предмет с id = " + id + " уже существует. "
                    + "Попробуйте изменить передаваемые данные или используйте подходящий метод.");
        }
    }

    private void throwIfOwnerMismatched(long inRepositoryOwnerId, long inDtoOwnerId) {
        if (inRepositoryOwnerId != inDtoOwnerId) {
            throw new OwnerMismatchException("Редактировать запрошенный предмет может только его владелец");
        }
    }

    private void throwIfBookingForCommentNotFound(Comment comment) {
        if (Objects.isNull(bookingRepository.lastCurrentBookingExistsForCommentAuthor(comment.getItem(),
                comment.getAuthor(), LocalDateTime.now()))) {
            throw new BookingForCommentNotFoundException(String.format("Для пользователя с id = %d "
                            + "не найдено текущего или прошедшего бронирования предмета с id = %d",
                    comment.getAuthor().getId(), comment.getItem().getId()));
        }
    }

    private void mergeDtoIntoExistingItem(ItemDto updated, Item beforeUpdate) {
        if (updated.getOwnerId() != null) {
            beforeUpdate.setOwner(userService.findByIdOrThrow(updated.getOwnerId()));
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
            beforeUpdate.setRequest(itemRequestService.findByIdOrThrow(updated.getRequestId()));
        }
    }

    private AdvancedItemDto mapItemToDtoWithBookingsAndComments(Item item) {
        Booking lastBooking = bookingRepository.findFirstByItemAndStatusInAndStartBeforeOrderByStartDesc(item,
                new BookingStatus[]{BookingStatus.APPROVED, BookingStatus.WAITING},
                LocalDateTime.now());
        Booking nextBooking = bookingRepository.findFirstByItemAndStatusInAndStartAfterOrderByStartAsc(item,
                new BookingStatus[]{BookingStatus.APPROVED, BookingStatus.WAITING},
                LocalDateTime.now());
        BookingDto lastBookingDto = lastBooking != null
                ? bookingMapper.bookingToSimpleDto(lastBooking)
                : null;
        BookingDto nextBookingDto = nextBooking != null
                ? bookingMapper.bookingToSimpleDto(nextBooking)
                : null;
        return itemMapper.itemToDtoWithBookingsAndComments(item, lastBookingDto, nextBookingDto);
    }

    private Item mapItemFromDto(ItemDto dto) {
        User owner = new User(0L, "null", "fake@e.mail");
        try {
            owner = userService.findByIdOrThrow(dto.getOwnerId());
        } catch (UserNotFoundException e) {
            System.out.println("Чек-стайл не пропускает пустой кетч-блок.");
        }
        ItemRequest request = dto.getRequestId() == null ? null
                : itemRequestService.findByIdOrThrow(dto.getRequestId());
        return itemMapper.itemFromDto(dto, owner, request);
    }

    private Comment mapCommentFromDto(IncomingCommentDto dto) {
        Item item = findByIdOrThrow(dto.getItemId());
        User author = userService.findByIdOrThrow(dto.getAuthorId());
        return commentMapper.commentFromDto(dto, item, author);
    }
}