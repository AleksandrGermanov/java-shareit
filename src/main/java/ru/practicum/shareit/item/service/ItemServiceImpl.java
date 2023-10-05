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
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final ShareItValidator shareItValidator;

    @Override
    public ItemDto create(ItemDto itemDto) {
        Item itemFromDto = itemMapper.itemFromDto(itemDto);
        throwIfRepositoryContains(itemFromDto.getId());
        userService.throwIfRepositoryNotContains(itemFromDto.getOwnerId());
        shareItValidator.validate(itemFromDto);
        return itemMapper.itemToDto(itemRepository.create(itemFromDto));
    }

    @Override
    public List<ItemDto> findAllByOwner(@Valid @NotNull long ownerId) {
        userService.throwIfRepositoryNotContains(ownerId);
        return itemRepository.findAllByOwner(ownerId).stream()
                .map(itemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchByText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchByText(text).stream()
                .map(itemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto retrieve(long id) {
        throwIfRepositoryNotContains(id);
        return itemMapper.itemToDto(itemRepository.retrieve(id));
    }

    @Override
    public ItemDto update(ItemDto itemDto) {
        throwIfRepositoryNotContains(itemDto.getId());
        userService.throwIfRepositoryNotContains(itemDto.getOwnerId());
        Item itemToUpdate = itemRepository.retrieve(itemDto.getId());
        throwIfOwnerMismatched(itemToUpdate.getOwnerId(), itemDto.getOwnerId());
        mergeDtoIntoExistingItem(itemDto, itemToUpdate);
        shareItValidator.validate(itemToUpdate);
        return itemMapper.itemToDto(itemRepository.update(itemToUpdate));
    }

    @Override
    public void delete(long id) {
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

    private void throwIfOwnerMismatched(long inRepositoryOwnersId, long inDtoOwnersId) {
        if (inRepositoryOwnersId != inDtoOwnersId) {
            throw new OwnerMismatchException("Редактировать запрошенный предмет может только его владелец");
        }
    }

    private void mergeDtoIntoExistingItem(ItemDto updated, Item beforeUpdate) {
        if (updated.getOwnerId() != null) {
            beforeUpdate.setOwnerId(updated.getOwnerId());
        }
        if (updated.getName() != null) {
            beforeUpdate.setName(updated.getName());
        }
        if (updated.getDescription() != null) {
            beforeUpdate.setDescription(updated.getDescription());
        }
        if (updated.getAvailable() != null) {
            beforeUpdate.setAvailable(updated.getAvailable());
        }
        if (updated.getRequestId() != null) {
            beforeUpdate.setRequestId(updated.getRequestId());
        }
    }
}

