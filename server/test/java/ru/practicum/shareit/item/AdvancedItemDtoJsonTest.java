package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.SimpleBookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.item.AdvancedItemDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AdvancedItemDtoJsonTest {
    private final JacksonTester<AdvancedItemDto> jacksonTester;

    @Test
    public void whenWrittenToJsonAdvancedItemDtoTest() throws IOException {
        JsonContent<AdvancedItemDto> json = jacksonTester.write(new AdvancedItemDto(
                0L, 0L, "name", "description", true, null,
                new SimpleBookingDto(),
                new SimpleBookingDto(0L, LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                        BookingStatus.WAITING, 0L, 0L),
                List.of(
                        new CommentDto(0L, "text", "authorName",
                                LocalDateTime.of(2023, 1, 1, 1, 1)),
                        new CommentDto(1L, "text1", "authorName1",
                                LocalDateTime.of(2023, 1, 1, 1, 11))
                )));

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(0);
        assertThat(json).extractingJsonPathNumberValue("$.ownerId").isEqualTo(0);
        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(json).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(json).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(json).extractingJsonPathNumberValue("$.requestId").isNull();
        assertThat(json).extractingJsonPathMapValue("$.lastBooking").isNotEmpty();
        assertThat(json).extractingJsonPathMapValue("$.nextBooking").isNotEmpty();
        assertThat(json).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(0);
        assertThat(json).extractingJsonPathStringValue("$.nextBooking.start")
                .containsIgnoringCase(Year.now().toString());
        assertThat(json).extractingJsonPathStringValue("$.nextBooking.end")
                .containsIgnoringCase(Year.now().toString());
        assertThat(json).extractingJsonPathStringValue("$.nextBooking.status").isEqualTo("WAITING");
        assertThat(json).extractingJsonPathNumberValue("$.nextBooking.itemId").isEqualTo(0);
        assertThat(json).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(0);
        assertThat(json).extractingJsonPathArrayValue("$.comments").size().isEqualTo(2);
        assertThat(json).extractingJsonPathMapValue("$.comments[0]").isEqualTo(
                Map.of("id", 0,
                        "text", "text",
                        "authorName", "authorName",
                        "created", "2023-01-01T01:01:00"));
        assertThat(json).extractingJsonPathMapValue("$.comments[1]").isEqualTo(
                Map.of("id", 1,
                        "text", "text1",
                        "authorName", "authorName1",
                        "created", "2023-01-01T01:11:00"));
    }
}
