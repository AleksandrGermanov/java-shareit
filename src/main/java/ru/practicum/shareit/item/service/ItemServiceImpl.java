package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.Util.ShareItValidator;
import ru.practicum.shareit.exception.OwnerMismatchException;
import ru.practicum.shareit.exception.alreadyExists.ItemAlreadyExistsException;
import ru.practicum.shareit.exception.notFound.ItemNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.Util.Logging.logInfoExecutedMethod;

@Service
@Validated
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ShareItValidator shareItValidator;

    @Override
    public ItemDto create(ItemDto itemDto) {
        logInfoExecutedMethod(log, itemDto);
        Item itemFromDto = Item.fromDto(itemDto);
        throwIfRepositoryContains(itemFromDto.getId());
        userService.throwIfRepositoryNotContains(itemFromDto.getOwnerId());
        shareItValidator.validate(itemFromDto);
        return itemRepository.create(itemFromDto)
                .toDto();
    }

    @Override
    public List<ItemDto> findAllByOwner(@Valid @NotNull long ownerId) {
        logInfoExecutedMethod(log, ownerId);
        userService.throwIfRepositoryNotContains(ownerId);
        return itemRepository.findAllByOwner(ownerId).stream()
                .map(Item::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchByText(String text) {
        logInfoExecutedMethod(log, text);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchByText(text).stream()
                .map(Item::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto retrieve(long id) {
        logInfoExecutedMethod(log, id);
        throwIfRepositoryNotContains(id);
        return itemRepository.retrieve(id)
                .toDto();
    }

    @Override
    public ItemDto update(ItemDto itemDto) {
        logInfoExecutedMethod(log, itemDto);
        throwIfRepositoryNotContains(itemDto.getId());
        userService.throwIfRepositoryNotContains(itemDto.getOwnerId());
        ItemDto beforeUpdate = itemRepository.retrieve(itemDto.getId()).toDto();
        if (!beforeUpdate.getOwnerId().equals(itemDto.getOwnerId())) {
            throw new OwnerMismatchException("Редактировать запрошенный предмет может только его владелец");
        }
        Item result = mergeDtos(itemDto, beforeUpdate);
        shareItValidator.validate(result);
        return itemRepository.update(result)
                .toDto();
    }

    @Override
    public void delete(long id) {
        logInfoExecutedMethod(log, id);
        throwIfRepositoryNotContains(id);
        itemRepository.delete(id);
    }

    private void throwIfRepositoryNotContains(long id) throws ItemNotFoundException {
        if (!itemRepository.containsItem(id)) {
            throw new ItemNotFoundException("Предмет с id = " + id + " не найден.");
        }
    }

    private void throwIfRepositoryContains(long id) throws ItemAlreadyExistsException {
        if (itemRepository.containsItem(id)) {
            throw new ItemAlreadyExistsException("Предмет с id = " + id + " уже существует. "
                    + "Попробуйте изменить передаваемые данные или используйте подходящий метод.");
        }
    }

    private Item mergeDtos(ItemDto updated, ItemDto beforeUpdate) {
        Long ownerId = updated.getOwnerId() != null ? updated.getOwnerId() : beforeUpdate.getOwnerId();
        String name = updated.getName() != null ? updated.getName() : beforeUpdate.getName();
        String description = updated.getDescription() != null
                ? updated.getDescription() : beforeUpdate.getDescription();
        Boolean isAvailable = updated.getAvailable() != null ?
                updated.getAvailable() : beforeUpdate.getAvailable();
        Long requestId = updated.getRequestId() != null ? updated.getRequestId() : beforeUpdate.getRequestId();
        return new Item(updated.getId(), ownerId, name, description, isAvailable, requestId);
    }
}

