package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;


public interface ItemRequestService {
    List<ItemRequestDto> findAllByRequester(long requesterId);

    ItemRequestDto retrieve(long requestId, long userId);

    List<ItemRequestDto> findAll(long seekerId, int from, int size);

    ItemRequestDto create(ItemRequestDto dto);

    ItemRequest findByIdOrThrow(long requestId);
}
