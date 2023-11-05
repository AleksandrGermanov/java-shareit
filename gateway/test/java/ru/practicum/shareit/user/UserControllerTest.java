package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserControllerTest {
    @InjectMocks
    private final UserController userController;
    private final ObjectMapper mapper;
    private final Object testObject = "testObject";
    private final ResponseEntity<Object> responseEntity = ResponseEntity.ok(testObject);
    private final UserDto dto = new UserDto("name", "e@ma.il");
    @MockBean
    private UserClient userClient;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(ExceptionControllerAdvice.class)
                .build();
    }

    @Test
    @SneakyThrows
    public void methodFindAllCallsUserClientMethod() {
        when(userClient.findAll()).thenReturn(responseEntity);

        mockMvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("testObject"));
    }

    @Test
    @SneakyThrows
    public void methodRetrieveCallsUserClientMethod() {
        when(userClient.retrieve(1L)).thenReturn(responseEntity);

        mockMvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("testObject"));
    }

    @Test
    @SneakyThrows
    public void methodCreateCallsUserClientMethod() {
        when(userClient.create(dto)).thenReturn(responseEntity);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("testObject"));
    }

    @Test
    @SneakyThrows
    public void methodCreateWhenNameIsNullReturnsCode400() {
        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(
                                new UserDto(null, "e@ma.il")
                        ))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodCreateWhenNameIsTooBigReturnsCode400() {
        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(
                                new UserDto("name".repeat(32), "email")
                        ))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodCreateWhenEmailIsNullReturnsCode400() {
        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(
                                new UserDto("name", null)
                        ))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodCreateWhenEmailFormattedWrongReturnsCode400() {
        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(
                                new UserDto("name", "email")
                        ))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodUpdateCallsUserClientMethod() {
        when(userClient.update(1L, dto)).thenReturn(responseEntity);

        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("testObject"));
    }

    @Test
    @SneakyThrows
    public void methodUpdateWhenNameIsNullReturnsCode200() {
        when(userClient.update(1L, dto)).thenReturn(responseEntity);

        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(
                                new UserDto(null, "e@ma.il")
                        ))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void methodUpdateWhenNameIsTooBigReturnsCode400() {
        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(
                                new UserDto("name".repeat(32), "email")
                        ))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodUpdateWhenEmailIsNullReturnsCode200() {
        when(userClient.update(1L, dto)).thenReturn(responseEntity);

        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(
                                new UserDto("name", null)
                        ))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void methodUpdateWhenEmailFormattedWrongReturnsCode400() {
        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(
                                new UserDto("name", "email")
                        ))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodDeleteCallsUserClientMethod() {
        when(userClient.delete(1L)).thenReturn(responseEntity);

        mockMvc.perform(delete("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("testObject"));
    }
}