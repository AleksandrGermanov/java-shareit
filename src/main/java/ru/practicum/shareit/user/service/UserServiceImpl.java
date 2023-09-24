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
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.Util.Logging.logInfoExecutedMethod;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ShareItValidator shareItValidator;

    @Override
    public UserDto create(UserDto userDto) {
        logInfoExecutedMethod(log, userDto);
        User userFromDto = User.fromDto(userDto);
        throwIfRepositoryContains(userFromDto.getId());
        throwIfEmailAlreadyExists(userFromDto.getEmail());
        shareItValidator.validate(userFromDto);
        return userRepository.create(userFromDto)
                .toDto();
    }

    @Override
    public List<UserDto> findAll() {
        logInfoExecutedMethod(log);
        return userRepository.findAll().stream()
                .map(User::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto retrieve(long id) {
        logInfoExecutedMethod(log, id);
        throwIfRepositoryNotContains(id);
        return userRepository.retrieve(id)
                .toDto();
    }

    @Override
    public UserDto update(UserDto userDto) {
        logInfoExecutedMethod(log, userDto);
        UserDto beforeUpdate = userRepository.retrieve(userDto.getId()).toDto();
        User result = mergeDtos(userDto, beforeUpdate);
        if (!beforeUpdate.getEmail().equals(userDto.getEmail())) {
            throwIfEmailAlreadyExists(userDto.getEmail());
        }
        throwIfRepositoryNotContains(result.getId());
        shareItValidator.validate(result);
        return userRepository.update(result)
                .toDto();
    }

    @Override
    public void delete(long id) {
        logInfoExecutedMethod(log, id);
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

    private User mergeDtos(UserDto updated, UserDto beforeUpdate) {
        String name = updated.getName() != null ? updated.getName() : beforeUpdate.getName();
        String email = updated.getEmail() != null ? updated.getEmail() : beforeUpdate.getEmail();
        return new User(updated.getId(), name, email);
    }
}

