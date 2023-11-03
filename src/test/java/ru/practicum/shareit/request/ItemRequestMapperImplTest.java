package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.AdvancedItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapperImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

public class ItemRequestMapperImplTest {
    private final ItemRequestMapperImpl mapper = new ItemRequestMapperImpl();
    private final User requester = new User(0L, "n", "e@m.l");
    private final ItemRequest request = new ItemRequest(0L, requester,
            "description of requested item",
            LocalDateTime.of(2023, 2, 2, 3, 0), new ArrayList<>());
    private final ItemRequestDto dto = new ItemRequestDto(request.getId(), requester.getId(),
            request.getDescription(), request.getCreated());

    @Test
    public void methodItemRequestFromDtoCreatesItemRequest() {
        Assertions.assertEquals(request, mapper.itemRequestFromDto(dto, requester));
    }

    @Test
    public void methodItemRequestFromDtoWhenNullSetsFieldsIdDescription() {
        ItemRequest ir = mapper.itemRequestFromDto(new ItemRequestDto(), new User());
        Assertions.assertEquals(0L, ir.getId());
        Assertions.assertEquals("", ir.getDescription());
    }

    @Test
    public void methodItemRequestToDtoCreatesItemRequestDto() {
        Assertions.assertEquals(dto, mapper.itemRequestToDto(request));
    }

    @Test
    public void methodItemRequestToAdvancedDtoCreatesAdvancedItemRequestDto() {
        AdvancedItemRequestDto advancedItemRequestDto = new AdvancedItemRequestDto(request.getId(), requester.getId(),
                request.getDescription(), request.getCreated(), Collections.emptyList());
        Assertions.assertEquals(advancedItemRequestDto, mapper
                .itemRequestToAdvancedDto(request, Collections.emptyList()));
    }
}
