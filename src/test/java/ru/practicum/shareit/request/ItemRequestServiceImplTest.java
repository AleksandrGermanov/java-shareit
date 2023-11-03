package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.alreadyExists.RequestAlreadyExistsException;
import ru.practicum.shareit.exception.notFound.NotFoundException;
import ru.practicum.shareit.exception.notFound.RequestNotFoundException;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.AdvancedItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.PaginationInfo;
import ru.practicum.shareit.util.ShareItValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    private final User requester = new User(0L, "n", "e@m.l");
    private final ItemRequest request = new ItemRequest(0L, requester, "description of requested item",
            LocalDateTime.of(2023, 2, 2, 3, 0), new ArrayList<>());
    private final Item item = new Item(0L, new User(999L, "nm", "fake@e.mail"),
            "name", "description", true, request,
            Collections.emptyList());
    private final ItemDto itemDto = new ItemDto(item.getId(), item.getName(),
            item.getDescription(), item.getAvailable(), item.getRequest().getId());
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRequestMapper itemRequestMapper;
    @Mock
    private ShareItValidator shareItValidator;
    @Mock
    private UserService userService;
    @Mock
    private ItemMapper itemMapper;
    @InjectMocks
    private ItemRequestServiceImpl requestService;
    private AdvancedItemRequestDto requestDto;

    @BeforeEach
    public void setUp() {
        if (!request.getItems().contains(item)) {
            request.getItems().add(item);
        }
        when(itemMapper.itemToSimpleDto(item)).thenReturn(itemDto);
        requestDto = new AdvancedItemRequestDto(item.getId(),
                requester.getId(), request.getDescription(), request.getCreated(),
                request.getItems().stream().map(itemMapper::itemToSimpleDto)
                        .collect(Collectors.toList()));
    }

    @Test
    public void methodFindAllByRequesterReturnsListOfRequest() {
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(0L))
                .thenReturn(List.of(request));
        when(itemRequestMapper.itemRequestToAdvancedDto(request, List.of(itemDto)))
                .thenReturn(requestDto);

        Assertions.assertEquals(List.of(requestDto), requestService.findAllByRequester(0L));
        verify(userService, times(1)).throwIfRepositoryNotContains(0L);
    }

    @Test
    public void methodRetrieveReturnsListOfRequest() {
        when(itemRequestRepository.findById(0L))
                .thenReturn(Optional.of(request));
        when(itemRequestMapper.itemRequestToAdvancedDto(request, List.of(itemDto)))
                .thenReturn(requestDto);

        Assertions.assertEquals(requestDto, requestService.retrieve(0L, 0L));
        verify(userService, times(1)).throwIfRepositoryNotContains(0L);
    }

    @Test
    public void methodFindAllReturnsListOfRequestDto() {
        PaginationInfo info = new PaginationInfo(0, 2, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequest> list = List.of(request);
        when(itemRequestRepository.findAllByRequesterIdIsNot(999L, info.asPageRequest()))
                .thenReturn(list);
        when(itemRequestMapper.itemRequestToAdvancedDto(request, List.of(itemDto)))
                .thenReturn(requestDto);

        Assertions.assertEquals(List.of(requestDto), requestService.findAll(999L, 0, 2));
        verify(userService, times(1)).throwIfRepositoryNotContains(999L);
        verify(shareItValidator, times(1)).validate(info);
    }

    @Test
    public void methodCreateReturnsRequestDto() {
        when(userService.findByIdOrThrow(0L))
                .thenReturn(requester);
        when(itemRequestMapper.itemRequestFromDto(requestDto, requester))
                .thenReturn(request);
        when(itemRequestRepository.existsById(0L)).thenReturn(false);
        when(itemRequestRepository.save(request)).thenReturn(request);
        when(itemRequestMapper.itemRequestToAdvancedDto(request, List.of(itemDto)))
                .thenReturn(requestDto);

        Assertions.assertEquals(requestDto, requestService.create(requestDto));
        verify(shareItValidator, times(1)).validate(request);
    }

    @Test
    public void methodCreateWhenRequestExistsThrowsException() {
        when(userService.findByIdOrThrow(0L))
                .thenReturn(requester);
        when(itemRequestMapper.itemRequestFromDto(requestDto, requester))
                .thenReturn(request);
        when(itemRequestRepository.existsById(0L)).thenReturn(true);

        Assertions.assertThrows(RequestAlreadyExistsException.class, () -> requestService.create(requestDto));
    }

    @Test
    public void methodFindByIdOrThrowReturnsRequest() {
        when(itemRequestRepository.findById(0L)).thenReturn(Optional.of(request));

        Assertions.assertEquals(request, requestService.findByIdOrThrow(0));
        Assertions.assertThrows(NotFoundException.class, () -> requestService.findByIdOrThrow(1L));
        Assertions.assertThrows(RequestNotFoundException.class, () -> requestService.findByIdOrThrow(1L));
    }
}
