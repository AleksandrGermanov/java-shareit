package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Util.ShareItValidator;
import ru.practicum.shareit.exception.alreadyExists.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.alreadyExists.UserAlreadyExistsException;
import ru.practicum.shareit.exception.notFound.UserNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ShareItValidator shareItValidator;

    @Override
    public UserDto create(UserDto userDto) {
        User userFromDto = userMapper.userFromDto(userDto);
        throwIfRepositoryContains(userFromDto.getId());
        throwIfEmailAlreadyExists(userFromDto.getEmail());
        shareItValidator.validate(userFromDto);
        return userMapper.userToDto(userRepository.create(userFromDto));
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::userToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto retrieve(long id) {
        throwIfRepositoryNotContains(id);
        return userMapper.userToDto(userRepository.retrieve(id));
    }

    @Override
    public UserDto update(UserDto userDto) {
        User userToUpdate = userRepository.retrieve(userDto.getId());
        if (!haveSameEmail(userToUpdate, userDto)) {
            throwIfEmailAlreadyExists(userDto.getEmail());
        }
        mergeDtoIntoExistingUser(userDto, userToUpdate);
        throwIfRepositoryNotContains(userToUpdate.getId());
        shareItValidator.validate(userToUpdate);
        return userMapper.userToDto(userRepository.update(userToUpdate));
    }

    @Override
    public void delete(long id) {
        throwIfRepositoryNotContains(id);
        userRepository.delete(id);
    }

    public void throwIfRepositoryNotContains(long id) throws UserNotFoundException {
        if (!userRepository.containsUser(id)) {
            throw new UserNotFoundException("Пользователь с id = " + id + " не найден.");
        }
    }

    private void throwIfRepositoryContains(long id) throws UserAlreadyExistsException {
        if (userRepository.containsUser(id)) {
            throw new UserAlreadyExistsException("Пользователь с id = " + id + " уже существует. "
                    + "Попробуйте изменить передаваемые данные или используйте подходящий метод.");
        }
    }

    private void throwIfEmailAlreadyExists(String email) throws EmailAlreadyExistsException {
        if (userRepository.findAll().isEmpty()) {
            return;
        }
        if (userRepository.findAll().stream()
                .map(User::getEmail)
                .anyMatch(string -> string.equals(email))) {
            throw new EmailAlreadyExistsException("Такой email уже зарегистрирован.");
        }
    }

    private boolean haveSameEmail(User beforeUpdate, UserDto updated) {
        return beforeUpdate.getEmail().equals(updated.getEmail());
    }

    private void mergeDtoIntoExistingUser(UserDto updated, User beforeUpdate) {
        if (updated.getId() != null) {
            beforeUpdate.setId(updated.getId());
        }
        if (updated.getName() != null) {
            beforeUpdate.setName(updated.getName());
        }
        if (updated.getEmail() != null) {
            beforeUpdate.setEmail(updated.getEmail());
        }
    }
}

