package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.OutcomingBookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OutcomingBookingDtoJsonTest {
    private final JacksonTester<OutcomingBookingDto> jacksonTester;
    OutcomingBookingDto bookingDto = new OutcomingBookingDto(999L,
            LocalDateTime.of(2023, 1, 1, 1, 1),
            LocalDateTime.of(2023, 1, 1, 2, 1),
            BookingStatus.WAITING, new ItemDto(0L, 0L, "item_name",
            "description", true, 1111L),
            new UserDto(999L, "booker", "e@ma.il"));

    @Test
    public void whenWrittenToJsonOutcomingBookingDtoTest() throws IOException {
        JsonContent<OutcomingBookingDto> jsonContent = jacksonTester.write(bookingDto);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id")
                .isEqualTo(999);
        assertThat(jsonContent).extractingJsonPathStringValue("$.start")
                .isEqualTo("2023-01-01T01:01:00");
        assertThat(jsonContent).extractingJsonPathStringValue("$.end")
                .isEqualTo("2023-01-01T02:01:00");
        assertThat(jsonContent).extractingJsonPathStringValue("$.status")
                .isEqualTo("WAITING");
        assertThat(jsonContent).extractingJsonPathMapValue("$.item")
                .isEqualTo(Map.of(
                        "id", 0,
                        "ownerId", 0,
                        "name", "item_name",
                        "description", "description",
                        "available", true,
                        "requestId", 1111));
        assertThat(jsonContent).extractingJsonPathMapValue("$.booker")
                .isEqualTo(Map.of(
                        "id", 999,
                        "name", "booker",
                        "email", "e@ma.il"));
    }
}
