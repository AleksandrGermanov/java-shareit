package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.util.ExceptionControllerAdvice;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Year;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingControllerTest {
    @InjectMocks
    private final BookingController bookingController;
    private final ObjectMapper mapper;
    private final ResponseEntity<Object> responseEntity = ResponseEntity.ok("testObject");
    @MockBean
    private BookingClient bookingClient;
    private MockMvc mockMvc;
    private BookItemRequestDto dto;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .setControllerAdvice(ExceptionControllerAdvice.class)
                .build();

        dto = new BookItemRequestDto(0L,
                LocalDateTime.of(Year.now().plusYears(1).getValue(), 1, 1, 1, 1),
                LocalDateTime.of(Year.now().plusYears(1).getValue(), 2, 2, 2, 2));
    }

    @Test
    @SneakyThrows
    public void methodGetBookingsCallsClientMethod() {
        when(bookingClient.getBookings(0L, BookingState.ALL, 0, 10)).thenReturn(responseEntity);

        mockMvc.perform(get("/bookings")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 0))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("testObject"));
    }

    @Test
    @SneakyThrows
    public void methodGetBookingsWhenStateUnsupportedReturnsCode500() {
        mockMvc.perform(get("/bookings?state=state")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 0))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @SneakyThrows
    public void methodGetBookingsWhenFromIsNegativeReturnsCode400() {
        mockMvc.perform(get("/bookings?from=-1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 0))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodGetBookingsWhenSizeIsZeroReturnsCode400() {
        mockMvc.perform(get("/bookings?size=0")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 0))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodFindByItemOwnerAndByStateCallsClientMethod() {
        when(bookingClient.findByItemOwnerAndByState(0L, BookingState.ALL, 0, 20)).thenReturn(responseEntity);

        mockMvc.perform(get("/bookings/owner")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 0))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("testObject"));
    }

    @Test
    @SneakyThrows
    public void methodFindByItemOwnerAndByStateWhenStateUnsupportedReturnsCode500() {
        mockMvc.perform(get("/bookings/owner?state=state")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 0))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @SneakyThrows
    public void methodFindByItemOwnerAndByStateWhenFromIsNegativeReturnsCode400() {
        mockMvc.perform(get("/bookings/owner?from=-1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 0))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodFindByItemOwnerAndByStateWhenSizeIsZeroReturnsCode400() {
        mockMvc.perform(get("/bookings/owner?size=0")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 0))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodCreateCallsClientMethod() {
        when(bookingClient.bookItem(0L, dto)).thenReturn(responseEntity);

        mockMvc.perform(post("/bookings")
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
    public void methodCreateWhenItemIdIsNullReturnsCode400() {
        dto.setItemId(null);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 0)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodCreateWhenStartIsNullReturnsCode400() {
        dto.setStart(null);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 0)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodCreateWhenStartIsInPastReturnsCode400() {
        dto.setStart(LocalDateTime.now().minusHours(1));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 0)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodCreateWhenEndIsNullReturnsCode400() {
        dto.setEnd(null);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 0)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodCreateWhenEndIsBeforeStartReturnsCode400() {
        dto.setEnd(dto.getStart().minusHours(1));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 0)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void methodGetBookingCallsClientMethod() {
        when(bookingClient.getBooking(0L, 999L)).thenReturn(responseEntity);

        mockMvc.perform(get("/bookings/999")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 0))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("testObject"));
    }

    @Test
    @SneakyThrows
    public void methodUpdateCallsClientMethod() {
        when(bookingClient.update(999L, true, 0L)).thenReturn(responseEntity);

        mockMvc.perform(patch("/bookings/999?approved=true")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 0))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("testObject"));
    }
}
