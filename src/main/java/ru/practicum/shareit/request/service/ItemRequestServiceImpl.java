package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.alreadyExists.RequestAlreadyExistsException;
import ru.practicum.shareit.exception.notFound.RequestNotFoundException;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.PaginationInfo;
import ru.practicum.shareit.util.ShareItValidator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ShareItValidator shareItValidator;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> findAllByRequester(long requesterId) {
        userService.throwIfRepositoryNotContains(requesterId);
        return itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(requesterId).stream()
                .map(this::mapItemRequestToAdvancedDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDto retrieve(long requestId, long userId) {
        userService.throwIfRepositoryNotContains(userId);
        return mapItemRequestToAdvancedDto(findByIdOrThrow(requestId));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> findAll(long seekerId, int from, int size) {
        userService.throwIfRepositoryNotContains(seekerId);
        PaginationInfo info = new PaginationInfo(from, size);
        shareItValidator.validate(info);
        info.setSort(Sort.by(Sort.Direction.DESC, "created"));
        return itemRequestRepository.findAllByRequesterIdIsNot(seekerId, info.asPageRequest()).stream()
                .map(this::mapItemRequestToAdvancedDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ItemRequestDto create(ItemRequestDto dto) {
        ItemRequest itemRequest = mapItemRequestFromDto(dto);
        throwIfRepositoryContains(itemRequest.getId());
        itemRequest.setCreated(LocalDateTime.now());
        shareItValidator.validate(itemRequest);
        return mapItemRequestToAdvancedDto(itemRequestRepository.save(itemRequest));
    }

    @Transactional(readOnly = true)
    public ItemRequest findByIdOrThrow(long requestId) {
        return itemRequestRepository.findById(requestId).orElseThrow(
                () -> new RequestNotFoundException(String.format("Запрос с id = %d не найден.", requestId)));
    }

    private ItemRequestDto mapItemRequestToAdvancedDto(ItemRequest request) {
        List<ItemDto> items = request.getItems() == null
                ? Collections.emptyList()
                : request.getItems().stream()
                .map(itemMapper::itemToSimpleDto)
                .collect(Collectors.toList());
        return itemRequestMapper.itemRequestToAdvancedDto(request, items);
    }

    private void throwIfRepositoryContains(long requestId) {
        if (itemRequestRepository.existsById(requestId)) {
            throw new RequestAlreadyExistsException("Запрос с id = " + requestId + " уже существует. " +
                    "Попробуйте изменить передаваемые данные или используйте подходящий метод.");
        }
    }

    private ItemRequest mapItemRequestFromDto(ItemRequestDto dto) {
        User requester = userService.findByIdOrThrow(dto.getRequesterId());
        return itemRequestMapper.itemRequestFromDto(dto, requester);
    }
}
