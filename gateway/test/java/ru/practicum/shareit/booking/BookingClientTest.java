package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.Year;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingClientTest {
    private final BookingClient bookingClient;
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
        mockServer = MockRestServiceServer.createServer(bookingClient.getRest());
        responseEntity = ResponseEntity.ok(testObject);
    }

    @Test
    @SneakyThrows
    public void methodGetBookingsSendsRequestWithHeaders() {
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(serverURI + "/bookings?state=ALL&from=0&size=10")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "0"))
                .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(testObject)));

        ResponseEntity<Object> result = bookingClient.getBookings(0L, BookingState.ALL, 0, 10);
        mockServer.verify();
        Assertions.assertEquals(responseEntity.getStatusCode(), result.getStatusCode());
        Assertions.assertEquals(responseEntity.getBody(), result.getBody());
    }

    @Test
    @SneakyThrows
    public void methodFindByItemOwnerAndByStateSendsRequestWithHeaders() {
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(serverURI + "/bookings/owner?state=ALL&from=0&size=10")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "0"))
                .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(testObject)));

        ResponseEntity<Object> result = bookingClient.findByItemOwnerAndByState(0L, BookingState.ALL, 0, 10);
        mockServer.verify();
        Assertions.assertEquals(responseEntity.getStatusCode(), result.getStatusCode());
        Assertions.assertEquals(responseEntity.getBody(), result.getBody());
    }


    @Test
    @SneakyThrows
    public void methodBookItemSendsRequestWithHeaders() {
        BookItemRequestDto dto = new BookItemRequestDto(0L, LocalDateTime.of(
                Year.now().plusYears(1).getValue(), 1, 1, 1, 1),
                LocalDateTime.of(Year.now().plusYears(1).getValue(), 2, 2, 2, 2));

        mockServer.expect(ExpectedCount.once(), requestTo(new URI(serverURI + "/bookings")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "0"))
                .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
                .andExpect(header("Content-Type", MediaType.APPLICATION_JSON.toString()))
                .andExpect(content().json(mapper.writeValueAsString(dto)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(testObject)));

        ResponseEntity<Object> result = bookingClient.bookItem(0L, dto);
        mockServer.verify();
        Assertions.assertEquals(responseEntity.getStatusCode(), result.getStatusCode());
        Assertions.assertEquals(responseEntity.getBody(), result.getBody());
    }

    @Test
    @SneakyThrows
    public void methodGetBookingSendsRequestWithHeaders() {
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(serverURI + "/bookings/999")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "0"))
                .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(testObject)));

        ResponseEntity<Object> result = bookingClient.getBooking(0L, 999L);
        mockServer.verify();
        Assertions.assertEquals(responseEntity.getStatusCode(), result.getStatusCode());
        Assertions.assertEquals(responseEntity.getBody(), result.getBody());
    }

    @Test
    @SneakyThrows
    public void methodUpdateSendsRequestWithHeaders() {
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(serverURI + "/bookings/999?approved=true")))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", "0"))
                .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(testObject)));

        ResponseEntity<Object> result = bookingClient.update(999L, true, 0L);
        mockServer.verify();
        Assertions.assertEquals(responseEntity.getStatusCode(), result.getStatusCode());
        Assertions.assertEquals(responseEntity.getBody(), result.getBody());
    }
}
