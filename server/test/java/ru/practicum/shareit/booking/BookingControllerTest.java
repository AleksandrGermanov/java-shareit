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
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.SimpleBookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingState;
import ru.practicum.shareit.exception.ExceptionControllerAdvice;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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
    private final FormattingConversionService conversionService;
    private final SimpleBookingDto bookingDto = new SimpleBookingDto(0L,
            LocalDateTime.of(2025, 1, 1, 0, 0),
            LocalDateTime.of(2025, 1, 1, 1, 1),
            BookingStatus.WAITING, 0L, 999L);
    @MockBean
    private BookingService bookingService;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .setConversionService(conversionService)
                .setControllerAdvice(ExceptionControllerAdvice.class)
                .build();
    }

    @Test
    @SneakyThrows
    public void methodCreateCallsServiceAndReturnsBookingDto() {
        when(bookingService.create(bookingDto, 999L))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(jsonPath("$.itemId").value(0))
                .andExpect(jsonPath("$.bookerId").value(999))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty());
    }

    @Test
    @SneakyThrows
    public void methodUpdateCallsServiceAndReturnsBookingDto() {
        when(bookingService.update(0L, true, 999L))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/0?approved=true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(jsonPath("$.itemId").value(0))
                .andExpect(jsonPath("$.bookerId").value(999))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty());
    }

    @Test
    @SneakyThrows
    public void methodRetrieveCallsServiceAndReturnsBookingDto() {
        when(bookingService.retrieve(0L, 999L))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/0")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(jsonPath("$.itemId").value(0))
                .andExpect(jsonPath("$.bookerId").value(999))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty());
    }

    @Test
    @SneakyThrows
    public void methodFindByBookerAndByStateValueAllCallsServiceAndReturnsBookingDto() {
        when(bookingService.findByBookerAndByState(BookingState.ALL, 999L,
                0, 2)).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings?from=0&size=2")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(0))
                .andExpect(jsonPath("$[0].itemId").value(0))
                .andExpect(jsonPath("$[0].bookerId").value(999))
                .andExpect(jsonPath("$[0].start").isNotEmpty())
                .andExpect(jsonPath("$[0].end").isNotEmpty());
    }

    @Test
    @SneakyThrows
    public void methodFindByBookerAndByStateValueFutureCallsServiceAndReturnsBookingDto() {
        when(bookingService.findByBookerAndByState(BookingState.FUTURE, 999L,
                0, 2)).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings?state=future&from=0&size=2")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(0))
                .andExpect(jsonPath("$[0].itemId").value(0))
                .andExpect(jsonPath("$[0].bookerId").value(999))
                .andExpect(jsonPath("$[0].start").isNotEmpty())
                .andExpect(jsonPath("$[0].end").isNotEmpty());
    }

    @Test
    @SneakyThrows
    public void methodFindByBookerAndByStateValueRejectedCallsServiceAndReturnsEmptyList() {
        when(bookingService.findByBookerAndByState(BookingState.REJECTED, 999L,
                0, 2)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings?state=rejected&from=0&size=2")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @SneakyThrows
    public void methodFindByItemOwnerAndByStateValueAllCallsServiceAndReturnsBookingDto() {
        when(bookingService.findByItemOwnerAndByState(BookingState.ALL, 999L,
                0, 2)).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner?from=0&size=2")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(0))
                .andExpect(jsonPath("$[0].itemId").value(0))
                .andExpect(jsonPath("$[0].bookerId").value(999))
                .andExpect(jsonPath("$[0].start").isNotEmpty())
                .andExpect(jsonPath("$[0].end").isNotEmpty());
    }

    @Test
    @SneakyThrows
    public void methodFindByItemOwnerAndByStateValueFutureCallsServiceAndReturnsBookingDto() {
        when(bookingService.findByItemOwnerAndByState(BookingState.FUTURE, 999L,
                0, 2)).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner?state=future&from=0&size=2")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(0))
                .andExpect(jsonPath("$[0].itemId").value(0))
                .andExpect(jsonPath("$[0].bookerId").value(999))
                .andExpect(jsonPath("$[0].start").isNotEmpty())
                .andExpect(jsonPath("$[0].end").isNotEmpty());
    }

    @Test
    @SneakyThrows
    public void methodFindByItemOwnerAndByStateValueRejectedCallsServiceAndReturnsEmptyList() {
        when(bookingService.findByItemOwnerAndByState(BookingState.REJECTED, 999L,
                0, 2)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings/owner?state=rejected&from=0&size=2")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
