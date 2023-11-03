package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.SimpleBookingDto;
import ru.practicum.shareit.item.dto.comment.CommentMapper;
import ru.practicum.shareit.item.dto.item.AdvancedItemDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class ItemMapperImplTest {
    private final ItemDto itemDto = new ItemDto(0L, 0L, "item",
            "description", true, null);
    private final ItemDto simpleItemDto = new ItemDto(0L, "item",
            "description", true, null);
    private final User user = new User(0L, "n", "e@m.l");
    private final Item item = new Item(itemDto.getId(), user,
            itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), null,
            Collections.emptyList());
    @Mock
    private CommentMapper commentMapper;
    @InjectMocks
    private ItemMapperImpl mapper;

    @Test
    public void methodItemToDtoCreatesItemDto() {
        Assertions.assertEquals(itemDto, mapper.itemToDto(item));
    }

    @Test
    public void methodItemToSimpleDtoCreatesItemDto() {
        Assertions.assertEquals(simpleItemDto, mapper.itemToSimpleDto(item));
    }

    @Test
    public void methodItemToDtoWithBookingAndCommentsCreatesAdvancedItemDto() {
        AdvancedItemDto advancedItemDto = new AdvancedItemDto(itemDto.getId(), itemDto.getOwnerId(),
                itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(),
                itemDto.getRequestId(), new SimpleBookingDto(), new SimpleBookingDto(), Collections.emptyList());

        Assertions.assertEquals(advancedItemDto, mapper.itemToDtoWithBookingsAndComments(item,
                new SimpleBookingDto(), new SimpleBookingDto()));
    }

    @Test
    public void methodItemFromDtoCreatesItem() {
        Assertions.assertEquals(item, mapper.itemFromDto(itemDto, user, null));
    }
}
