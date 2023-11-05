package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.alreadyExists.ItemAlreadyExistsException;
import ru.practicum.shareit.exception.mismatch.MismatchException;
import ru.practicum.shareit.exception.mismatch.OwnerMismatchException;
import ru.practicum.shareit.exception.notFound.BookingForCommentNotFoundException;
import ru.practicum.shareit.exception.notFound.ItemNotFoundException;
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
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.PaginationInfo;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    private ItemDto itemDto;
    private User user;
    private Item item;
    private Comment comment;
    private IncomingCommentDto inCommentDto;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestService itemRequestService;
    @InjectMocks
    private ItemServiceImpl itemService;

    @BeforeEach
    public void createObjects() {
        itemDto = new ItemDto(0L, 0L, "item",
                "description", true, null);
        user = new User(0L, "n", "e@m.l");
        item = new Item(itemDto.getId(), user,
                itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), null,
                Collections.emptyList());
        comment = new Comment(0L, "TextTextText", item, user, LocalDateTime.now());
        LocalDateTime created = LocalDateTime.of(2023, 1, 1, 0, 0);
        inCommentDto = new IncomingCommentDto(0L, "TextTextText", created,
                0L, 0L);
    }

    @Test
    public void methodItemDtoCreateCallsItemMapperUserServiceItemRepository() {
        when(itemMapper.itemFromDto(any(), any(), any())).thenReturn(item);
        when(userService.findByIdOrThrow(0L)).thenReturn(user);
        when(itemRepository.save(any())).thenReturn(item);
        when(itemMapper.itemToDto(item)).thenReturn(itemDto);
        ItemDto dto = itemService.create(itemDto);

        verify(itemMapper, times(1)).itemFromDto(itemDto, user, null);
        verify(itemMapper, times(1)).itemToDto(item);
        verify(itemRepository, times(1)).existsById(0L);

        Assertions.assertEquals(itemDto, dto);
    }

    @Test
    public void methodItemDtoCreateWhenRepositoryContainsItemTrowsException() {
        when(itemMapper.itemFromDto(any(), any(), any())).thenReturn(item);
        when(itemRepository.existsById(item.getId())).thenReturn(true);

        Assertions.assertThrows(ItemAlreadyExistsException.class, () -> itemService.create(itemDto));
    }

    @Test
    public void methodCommentDtoCreateCallsCommentMapperBookingRepositoryShareItValidatorCommentRepository() {
        when(commentMapper.commentFromDto(any(), any(), any())).thenReturn(comment);
        when(bookingRepository.lastCurrentBookingExistsForCommentAuthor(any(), any(), any())).thenReturn(1);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentMapper.commentToDto(comment)).thenReturn(inCommentDto);

        CommentDto dto = itemService.create(inCommentDto);
        verify(commentMapper, times(1)).commentFromDto(inCommentDto, item, null);
        verify(userService, times(1)).findByIdOrThrow(0L);
        Assertions.assertEquals(inCommentDto, dto);
    }

    @Test
    public void methodCommentDtoCreateWhenBookingForCommentNotFoundThrowsException() {
        when(commentMapper.commentFromDto(any(), any(), any())).thenReturn(comment);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.lastCurrentBookingExistsForCommentAuthor(any(), any(), any())).thenReturn(null);

        Assertions.assertThrows(BookingForCommentNotFoundException.class, () -> itemService.create(inCommentDto));
    }

    @Test
    public void methodFindAllByOwnerCallsUserServiceValidatorAndItemRepository() {
        PaginationInfo info = new PaginationInfo(1, 2);
        when(itemRepository.findByOwnerIdOrderByIdAsc(0L, info.asPageRequest())).thenReturn(Collections.emptyList());

        Assertions.assertEquals(Collections.emptyList(), itemService.findAllByOwner(0L, 1, 2));
        verify(userService, times(1)).throwIfRepositoryNotContains(0L);
    }

    @Test
    public void methodSearchByTextCallsValidatorAndItemRepository() {
        PaginationInfo info = new PaginationInfo(1, 2);
        when(itemRepository.findByText("%text%", info.asPageRequest())).thenReturn(Collections.emptyList());

        Assertions.assertEquals(Collections.emptyList(), itemService.searchByText("text", 1, 2));
    }

    @Test
    public void methodRetrieveCallsUserServiceItemRepositoryItemMapper() {
        AdvancedItemDto advancedItemDto = new AdvancedItemDto(0L, 0L, "n", "d",
                true, null, null, null, null);
        when(itemRepository.findById(0L)).thenReturn(Optional.of(item));
        when(userService.findByIdOrThrow(0L)).thenReturn(user);
        when(itemMapper.itemToDtoWithBookingsAndComments(item, null, null))
                .thenReturn(advancedItemDto);

        Assertions.assertEquals(advancedItemDto, itemService.retrieve(0L, 0L));
    }

    @Test
    public void methodRetrieveWhenItemNotFoundThrowsException() {
        when(itemRepository.findById(0L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ItemNotFoundException.class, () -> itemService.retrieve(0L, 0L));
    }

    @Test
    public void methodUpdateCallsItemRepositoryShareItValidatorItemMapper() {
        item.setOwner(new User(888L, "", ""));
        when(itemRepository.findById(0L)).thenReturn(Optional.of(item));

        Assertions.assertThrows(MismatchException.class, () -> itemService.update(itemDto));
        Assertions.assertThrows(OwnerMismatchException.class, () -> itemService.update(itemDto));
    }

    @Test
    public void methodDeleteCallsItemRepository() {
        when(itemRepository.existsById(0L)).thenReturn(true);

        itemService.delete(0L);
        verify(itemRepository, times(1)).deleteById(0L);
    }

    @Test
    public void methodDeleteWhenItemNotExistsThrowsException() {
        when(itemRepository.existsById(0L)).thenReturn(false);

        Assertions.assertThrows(ItemNotFoundException.class, () -> itemService.delete(0L));
    }

    @Test
    public void methodFindByIdOrThrowCallsItemRepository() {
        when(itemRepository.findById(0L)).thenReturn(Optional.of(item));

        Assertions.assertEquals(item, itemService.findByIdOrThrow(0L));
    }

    @Test
    public void methodFindByIdWhenItemNotFoundThrowsException() {
        when(itemRepository.findById(0L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ItemNotFoundException.class, () -> itemService.findByIdOrThrow(0L));
    }
}
