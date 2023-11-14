package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.ExceptionControllerAdvice;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.when;
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
    private final Object testObject = "testObject";
    private final ResponseEntity<Object> responseEntity = ResponseEntity.ok(testObject);
    @MockBean
    private ItemClient itemClient;
    private ItemDto dto;
    private CommentDto commentDto;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .setControllerAdvice(ExceptionControllerAdvice.class)
                .build();

        dto = new ItemDto("name", "description", true, null);
        commentDto = new CommentDto("text");
    }

    @Test
    @SneakyThrows
    public void methodFindAllByOwnerCallsUserClientMethod() {
        when(itemClient.findAllByOwner(0L, 0, 1)).thenReturn(responseEntity);

        mockMvc.perform(get("/items?from=0&size=1")
                        .header("X-Sharer-User-Id", 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("testObject"));
    }

    @Test
    @SneakyThrows
    public void methodFindAllByOwnerWhenFromIsNegativeReturnsCode400() {
        mockMvc.perform(get("/items?from=-1&size=1")
                        .header("X-Sharer-User-Id", 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodFindAllByOwnerWhenSizeIsZeroReturnsCode400() {
        mockMvc.perform(get("/items?from=0&size=0")
                        .header("X-Sharer-User-Id", 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    @SneakyThrows
    public void methodRetrieveCallsUserClientMethod() {
        when(itemClient.retrieve(999L, 0L)).thenReturn(responseEntity);

        mockMvc.perform(get("/items/999")
                        .header("X-Sharer-User-Id", 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("testObject"));
    }

    @Test
    @SneakyThrows
    public void methodSearchByTextCallsUserClientMethod() {
        when(itemClient.searchByText("text", 0, 1)).thenReturn(responseEntity);

        mockMvc.perform(get("/items/search?text=text&from=0&size=1")
                        .header("X-Sharer-User-Id", 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("testObject"));
    }

    @Test
    @SneakyThrows
    public void methodSearchByTextWhenFromIsNegativeReturnsCode400() {
        mockMvc.perform(get("/items/search?text=text&from=-1&size=1")
                        .header("X-Sharer-User-Id", 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodSearchByTextWhenSizeIsZeroReturnsCode400() {
        mockMvc.perform(get("/items/search?text=text&from=0&size=0")
                        .header("X-Sharer-User-Id", 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodCreateItemDtoCallsClientMethod() {
        when(itemClient.create(0L, dto)).thenReturn(responseEntity);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 0)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("testObject"));
    }

    @Test
    @SneakyThrows
    public void methodCreateItemDtoWhenNameIsNullReturnsCode400() {
        dto.setName(null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 0)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodCreateItemDtoWhenDescriptionIsNullReturnsCode400() {
        dto.setDescription(null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 0)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodCreateItemDtoWhenAvailableIsNullReturnsCode400() {
        dto.setAvailable(null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 0)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodCreateItemDtoWhenNameIsTooBigReturnsCode400() {
        dto.setName("big".repeat(42));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 0)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodCreateItemDtoWhenDescriptionIsTooBigReturnsCode400() {
        dto.setDescription("toobig".repeat(42));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 0)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodCreateCommentDtoCallsClientMethod() {
        when(itemClient.create(999L, 0L, commentDto)).thenReturn(responseEntity);

        mockMvc.perform(post("/items/999/comment")
                        .header("X-Sharer-User-Id", 0)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("testObject"));
    }

    @Test
    @SneakyThrows
    public void methodCreateCommentDtoWhenTextIsNullReturnsCode400() {
        commentDto.setText(null);

        mockMvc.perform(post("/items/999/comment")
                        .header("X-Sharer-User-Id", 0)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodCreateCommentDtoWhenTextIsTooBigReturnsCode400() {
        commentDto.setText("text".repeat(126));

        mockMvc.perform(post("/items/999/comment")
                        .header("X-Sharer-User-Id", 0)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodUpdateItemDtoCallsClientMethod() {
        when(itemClient.update(999L, 0L, dto)).thenReturn(responseEntity);

        mockMvc.perform(patch("/items/999")
                        .header("X-Sharer-User-Id", 0)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("testObject"));
    }

    @Test
    @SneakyThrows
    public void methodUpdateItemDtoWhenNameIsNullCallsClientMethod() {
        dto.setName(null);
        when(itemClient.update(999L, 0L, dto)).thenReturn(responseEntity);

        mockMvc.perform(patch("/items/999")
                        .header("X-Sharer-User-Id", 0)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("testObject"));
    }

    @Test
    @SneakyThrows
    public void methodUpdateItemDtoWhenDescriptionIsNullCallsClientMethod() {
        dto.setDescription(null);
        when(itemClient.update(999L, 0L, dto)).thenReturn(responseEntity);

        mockMvc.perform(patch("/items/999")
                        .header("X-Sharer-User-Id", 0)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("testObject"));
    }

    @Test
    @SneakyThrows
    public void methodUpdateItemDtoWhenAvailableIsNullCallsClientMethod() {
        dto.setAvailable(null);
        when(itemClient.update(999L, 0L, dto)).thenReturn(responseEntity);

        mockMvc.perform(patch("/items/999")
                        .header("X-Sharer-User-Id", 0)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("testObject"));
    }

    @Test
    @SneakyThrows
    public void methodUpdateWhenNameIsTooBigReturnsCode400() {
        dto.setName("null".repeat(32));

        mockMvc.perform(patch("/items/999")
                        .header("X-Sharer-User-Id", 0)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodUpdateWhenDescriptionIsTooBigReturnsCode400() {
        dto.setDescription("null".repeat(63));

        mockMvc.perform(patch("/items/999")
                        .header("X-Sharer-User-Id", 0)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodDeleteCallsClientMethod() {
        dto.setAvailable(null);
        when(itemClient.delete(999L)).thenReturn(responseEntity);

        mockMvc.perform(delete("/items/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("testObject"));
    }
}

