package ru.practicum.shareit.itemRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;

import java.net.URI;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRequestClientTest {
    private final ItemRequestClient itemRequestClient;
    private final ObjectMapper mapper;
    private final String testObject = "testObject";
    private MockRestServiceServer mockServer;
    private ResponseEntity<Object> responseEntity;
    private String serverURI;

    @Autowired
    public void setServerURI(@Value("${shareit-server.url}") String serverURI) {
        this.serverURI = serverURI;
    }

    @BeforeEach
    @SneakyThrows
    public void setup() {
        mockServer = MockRestServiceServer.createServer(itemRequestClient.getRest());
        responseEntity = ResponseEntity.ok(testObject);
    }

    @Test
    @SneakyThrows
    public void methodFindAllByOwnerSendsRequestWithHeaders() {
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(serverURI
                        + "/requests")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "0"))
                .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(testObject)));

        ResponseEntity<Object> result = itemRequestClient.findAllByRequester(0L);
        mockServer.verify();
        Assertions.assertEquals(responseEntity.getStatusCode(), result.getStatusCode());
        Assertions.assertEquals(responseEntity.getBody(), result.getBody());
    }

    @Test
    @SneakyThrows
    public void methodRetrieveSendsRequestWithHeaders() {
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(serverURI
                        + "/requests/999")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "0"))
                .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(testObject)));

        ResponseEntity<Object> result = itemRequestClient.retrieve(999L, 0L);
        mockServer.verify();
        Assertions.assertEquals(responseEntity.getStatusCode(), result.getStatusCode());
        Assertions.assertEquals(responseEntity.getBody(), result.getBody());
    }

    @Test
    @SneakyThrows
    public void methodFindAllSendsRequestWithHeaders() {
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(serverURI
                        + "/requests/all?from=0&size=10")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "0"))
                .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(testObject)));

        ResponseEntity<Object> result = itemRequestClient.findAll(0L, 0, 10);
        mockServer.verify();
        Assertions.assertEquals(responseEntity.getStatusCode(), result.getStatusCode());
        Assertions.assertEquals(responseEntity.getBody(), result.getBody());
    }

    @Test
    @SneakyThrows
    public void methodCreateSendsRequestWithHeaders() {
        ItemRequestDto dto = new ItemRequestDto("description");

        mockServer.expect(ExpectedCount.once(), requestTo(new URI(serverURI + "/requests")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "0"))
                .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
                .andExpect(header("Content-Type", MediaType.APPLICATION_JSON.toString()))
                .andExpect(content().json(mapper.writeValueAsString(dto)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(testObject)));

        ResponseEntity<Object> result = itemRequestClient.create(0L, dto);
        mockServer.verify();
        Assertions.assertEquals(responseEntity.getStatusCode(), result.getStatusCode());
        Assertions.assertEquals(responseEntity.getBody(), result.getBody());
    }
}
