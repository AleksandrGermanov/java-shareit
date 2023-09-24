package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.ExceptionControllerAdvice;
import ru.practicum.shareit.exception.notFound.UserNotFoundException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.IncomingCommentDto;
import ru.practicum.shareit.item.dto.item.AdvancedItemDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemControllerTest {
    @InjectMocks
    private final ItemController itemController;
    private final ObjectMapper mapper;
    private final ItemDto itemDto = new ItemDto(0L, 0L, "item",
            "description", true, null);
    private final AdvancedItemDto advancedItemDto = new AdvancedItemDto(999L, 0L, "Aitem",
            "Adescription", true, null,
            null, null, Collections.emptyList());
    @MockBean
    private ItemService itemService;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .setControllerAdvice(ExceptionControllerAdvice.class)
                .build();
    }

    @Test
    public void itemsGetReturnsCode200AndEmptyList() throws Exception {
        when(itemService.findAllByOwner(0L, 0, 2))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items?from=0&size=2")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void itemsGetReturnsCode200AndListOfAdvancedDto() throws Exception {
        when(itemService.findAllByOwner(0L, 0, 2))
                .thenReturn(List.of(advancedItemDto));

        mockMvc.perform(get("/items?from=0&size=2")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(advancedItemDto.getId()))
                .andExpect(jsonPath("$[0].ownerId").value(advancedItemDto.getOwnerId()))
                .andExpect(jsonPath("$[0].name").value(advancedItemDto.getName()))
                .andExpect(jsonPath("$[0].description").value(advancedItemDto.getDescription()));
    }

    @Test
    public void itemsGetWithIdReturnsCode404AndMessageWhenNoUserFound() throws Exception {
        when(itemService.retrieve(1L, 0L)).thenThrow(UserNotFoundException.class);

        mockMvc.perform(get("/items/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "0"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").isString());
    }

    @Test
    public void itemsGetWithIdReturnsItemDto() throws Exception {
        when(itemService.retrieve(advancedItemDto.getId(), 0L)).thenReturn(advancedItemDto);

        mockMvc.perform(get("/items/999")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.id").value(advancedItemDto.getId()))
                .andExpect(jsonPath("$.ownerId").value(advancedItemDto.getOwnerId()))
                .andExpect(jsonPath("$.name").value(advancedItemDto.getName()))
                .andExpect(jsonPath("$.description").value(advancedItemDto.getDescription()));
    }

    @Test
    public void getItemsSearchReturnsItemDto() throws Exception {
        when(itemService.searchByText("desc", 0, 2)).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search?text=desc&from=0&size=2")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].ownerId").value(itemDto.getOwnerId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()));
    }

    @Test
    public void postItemsItemIdCommentReturnsCommentDto() throws Exception {
        final IncomingCommentDto commentDto = new IncomingCommentDto(0L, "text",
                null, null, null);
        when(itemService.create(any(IncomingCommentDto.class))).thenAnswer(invocationOnMock -> {
            IncomingCommentDto dto = invocationOnMock.getArgument(0);
            if (dto.getId() == 0L
                    && dto.getAuthorId() == 0L
                    && dto.getText().equals("text")
                    && dto.getItemId() == 999L
                    && dto.getCreated() != null) {
                return new CommentDto(0L, "text", "authorName", LocalDateTime.now());
            }
            return null;
        });

        mockMvc.perform(post("/items/999/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value("authorName"))
                .andExpect(jsonPath("$.created").isNotEmpty());
    }

    @Test
    public void postItemsReturnsItemDto() throws Exception {
        when(itemService.create(any(ItemDto.class))).thenReturn(
                new ItemDto(0L, 0L, "name", "d", true, null));

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "0"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(jsonPath("$.ownerId").value(0))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("d"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    public void patchItemsItemIdReturnsItemDto() throws Exception {
        when(itemService.update(any(ItemDto.class))).thenAnswer(invocationOnMock -> {
            ItemDto dto = invocationOnMock.getArgument(0);
            if (dto.getId() == 999L && dto.getOwnerId() == 0L) {
                return new ItemDto(0L, 0L, "name", "d", true, null);
            }
            return null;
        });

        mockMvc.perform(patch("/items/999")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(jsonPath("$.ownerId").value(0))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("d"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    public void deleteItemsItemIdCallsItemServiceWithPathVariableArg() throws Exception {
        mockMvc.perform(delete("/items/0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(itemService, times(1)).delete(0L);
    }
}

