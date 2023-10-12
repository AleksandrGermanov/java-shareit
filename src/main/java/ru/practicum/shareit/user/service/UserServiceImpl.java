package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Util.ShareItValidator;
import ru.practicum.shareit.exception.alreadyExists.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.alreadyExists.UserAlreadyExistsException;
import ru.practicum.shareit.exception.notFound.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ShareItValidator shareItValidator;

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        User userFromDto = userMapper.userFromDto(userDto);
        throwIfRepositoryContains(userFromDto.getId());
        throwIfEmailAlreadyExists(userFromDto.getEmail());
        shareItValidator.validate(userFromDto);
        return userMapper.userToDto(userRepository.save(userFromDto));
    }

    @Transactional
    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::userToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDto retrieve(long id) {
        return userMapper.userToDto(findByIdOrThrow(id));
    }

    @Transactional
    @Override
    public UserDto update(UserDto userDto) {
        User userToUpdate = findByIdOrThrow(userDto.getId());
        if (!haveSameEmail(userToUpdate, userDto)) {
            throwIfEmailAlreadyExists(userDto.getEmail());
        }
        mergeDtoIntoExistingUser(userDto, userToUpdate);
        throwIfRepositoryNotContains(userToUpdate.getId());
        shareItValidator.validate(userToUpdate);
        return userMapper.userToDto(userRepository.save(userToUpdate));
    }

    @Transactional
    @Override
    public void delete(long id) {
        throwIfRepositoryNotContains(id);
        userRepository.deleteById(id);
    }

    @Override
    public void throwIfRepositoryNotContains(long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Пользователь с id = " + id + " не найден.");
        }
    }

    @Override
    public User findByIdOrThrow(long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("Пользователь с id = " + id + " не найден."));
    }

    private void throwIfRepositoryContains(long id) {
        if (userRepository.existsById(id)) {
            throw new UserAlreadyExistsException("Пользователь с id = " + id + " уже существует. "
                    + "Попробуйте изменить передаваемые данные или используйте подходящий метод.");
        }
    }

    private void throwIfEmailAlreadyExists(String email) {
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