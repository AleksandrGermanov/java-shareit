package ru.practicum.shareit.itemRequest;

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
import ru.practicum.shareit.ExceptionControllerAdvice;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRequestControllerTest {
    @InjectMocks
    private final ItemRequestController itemRequestController;
    private final ObjectMapper mapper;
    private final Object testObject = "testObject";
    private final ResponseEntity<Object> responseEntity = ResponseEntity.ok(testObject);
    private final ItemRequestDto dto = new ItemRequestDto("Тестовое описание.");
    @MockBean
    private ItemRequestClient itemRequestClient;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .setControllerAdvice(ExceptionControllerAdvice.class)
                .build();
    }

    @Test
    @SneakyThrows
    public void methodFindAllByRequesterCallsClientMethod() {
        when(itemRequestClient.findAllByRequester(0L)).thenReturn(responseEntity);

        mockMvc.perform(get("/requests")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 0))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("testObject"));
    }

    @Test
    @SneakyThrows
    public void methodRetrieveCallsClientMethod() {
        when(itemRequestClient.retrieve(999L, 0L)).thenReturn(responseEntity);

        mockMvc.perform(get("/requests/999")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 0))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("testObject"));
    }

    @Test
    @SneakyThrows
    public void methodFindAllCallsClientMethod() {
        when(itemRequestClient.findAll(0L, 0, 1)).thenReturn(responseEntity);

        mockMvc.perform(get("/requests/all?from=0&size=1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 0))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("testObject"));
    }

    @Test
    @SneakyThrows
    public void methodFindAllWhenFromParamIsNegativeReturnsCode400() {
        mockMvc.perform(get("/requests/all?from=-1&size=1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 0))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodFindAllWhenSizeParamIsZeroReturnsCode400() {
        mockMvc.perform(get("/requests/all?from=0&size=0")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 0))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodCreateCallsClientMethod() {
        when(itemRequestClient.create(0L, dto)).thenReturn(responseEntity);

        mockMvc.perform(post("/requests")
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
    public void methodCreateWhenDescriptionIsNullReturnsCode400() {
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 0)
                        .content(mapper.writeValueAsString(new ItemRequestDto(null)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodCreateWhenDescriptionIsTooSmallReturnsCode400() {
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 0)
                        .content(mapper.writeValueAsString(new ItemRequestDto("small")))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodCreateWhenDescriptionIsTooBigReturnsCode400() {
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 0)
                        .content(mapper.writeValueAsString(new ItemRequestDto("small".repeat(126))))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}