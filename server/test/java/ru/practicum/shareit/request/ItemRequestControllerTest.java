package ru.practicum.shareit.request;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.ExceptionControllerAdvice;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.AdvancedItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRequestControllerTest {
    private final ObjectMapper mapper;
    @InjectMocks
    private final ItemRequestController requestController;
    private final AdvancedItemRequestDto dto = new AdvancedItemRequestDto(0L, 0L,
            "description of request", LocalDateTime.now(),
            Collections.emptyList());
    @MockBean
    private ItemRequestService itemRequestService;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(requestController)
                .setControllerAdvice(ExceptionControllerAdvice.class)
                .build();
    }

    @Test
    @SneakyThrows
    public void methodFindAllByRequesterCallsServiceReturnsList() {
        when(itemRequestService.findAllByRequester(0L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/requests")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(0))
                .andExpect(jsonPath("$[0].requesterId").value(0))
                .andExpect(jsonPath("$[0].description").value("description of request"))
                .andExpect(jsonPath("$[0].created").isNotEmpty())
                .andExpect(jsonPath("$[0].items").isEmpty());
    }

    @Test
    @SneakyThrows
    public void methodRetrieveCallsServiceReturnsRequestDto() {
        when(itemRequestService.retrieve(0L, 0L)).thenReturn(dto);

        mockMvc.perform(get("/requests/0")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(jsonPath("$.requesterId").value(0))
                .andExpect(jsonPath("$.description").value("description of request"))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    @SneakyThrows
    public void methodFindAllCallsServiceReturnsList() {
        when(itemRequestService.findAll(0L, 0, 2)).thenReturn(List.of(dto));

        mockMvc.perform(get("/requests/all?from=0&size=2")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(0))
                .andExpect(jsonPath("$[0].requesterId").value(0))
                .andExpect(jsonPath("$[0].description").value("description of request"))
                .andExpect(jsonPath("$[0].created").isNotEmpty())
                .andExpect(jsonPath("$[0].items").isEmpty());
    }

    @Test
    @SneakyThrows
    public void methodCreateCallsServiceReturnsDto() {
        when(itemRequestService.create(any())).thenAnswer(invocationOnMock -> {
            ItemRequestDto invocationDto = invocationOnMock.getArgument(0);
            if (invocationDto.getId() == 0
                    && invocationDto.getRequesterId() == 999L
                    && invocationDto.getDescription().equals(dto.getDescription())
                    && invocationDto.getCreated().equals(dto.getCreated())) {
                return dto;
            }
            return null;
        });

        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(jsonPath("$.requesterId").value(0))
                .andExpect(jsonPath("$.description").value("description of request"))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.items").isEmpty());
    }
}
