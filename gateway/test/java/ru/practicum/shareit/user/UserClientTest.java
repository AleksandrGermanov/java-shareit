package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;

import java.net.URI;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserClientTest {
    private final UserClient userClient;
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
        mockServer = MockRestServiceServer.createServer(userClient.getRest());
        responseEntity = ResponseEntity.ok(testObject);
    }

    @Test
    @SneakyThrows
    public void methodFindAllSendsRequestWithHeaders() {
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(serverURI
                        + "/users")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(testObject)));

        ResponseEntity<Object> result = userClient.findAll();
        mockServer.verify();
        Assertions.assertEquals(responseEntity.getStatusCode(), result.getStatusCode());
        Assertions.assertEquals(responseEntity.getBody(), result.getBody());
    }

    @Test
    @SneakyThrows
    public void methodRetrieveSendsRequestWithHeaders() {
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(serverURI
                        + "/users/999")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(testObject)));

        ResponseEntity<Object> result = userClient.retrieve(999);
        mockServer.verify();
        Assertions.assertEquals(responseEntity.getStatusCode(), result.getStatusCode());
        Assertions.assertEquals(responseEntity.getBody(), result.getBody());
    }

    @Test
    @SneakyThrows
    public void methodCreateSendsRequestWithHeaders() {
        UserDto dto = new UserDto("name", "e@ma.il");

        mockServer.expect(ExpectedCount.once(), requestTo(new URI(serverURI + "/users")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
                .andExpect(header("Content-Type", MediaType.APPLICATION_JSON.toString()))
                .andExpect(content().json(mapper.writeValueAsString(dto)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(testObject)));

        ResponseEntity<Object> result = userClient.create(dto);
        mockServer.verify();
        Assertions.assertEquals(responseEntity.getStatusCode(), result.getStatusCode());
        Assertions.assertEquals(responseEntity.getBody(), result.getBody());
    }

    @Test
    @SneakyThrows
    public void methodUpdateSendsRequestWithHeaders() {
        UserDto dto = new UserDto("name", "e@ma.il");

        mockServer.expect(ExpectedCount.once(), requestTo(new URI(serverURI + "/users/999")))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
                .andExpect(header("Content-Type", MediaType.APPLICATION_JSON.toString()))
                .andExpect(content().json(mapper.writeValueAsString(dto)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(testObject)));

        ResponseEntity<Object> result = userClient.update(999L, dto);
        mockServer.verify();
        Assertions.assertEquals(responseEntity.getStatusCode(), result.getStatusCode());
        Assertions.assertEquals(responseEntity.getBody(), result.getBody());
    }

    @Test
    @SneakyThrows
    public void methodDeleteSendsRequestWithHeaders() {
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(serverURI
                        + "/users/999")))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(testObject)));

        ResponseEntity<Object> result = userClient.delete(999);
        mockServer.verify();
        Assertions.assertEquals(responseEntity.getStatusCode(), result.getStatusCode());
        Assertions.assertEquals(responseEntity.getBody(), result.getBody());
    }
}
