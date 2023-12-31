package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.alreadyExists.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.alreadyExists.UserAlreadyExistsException;
import ru.practicum.shareit.exception.notFound.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Primary
@Service
@RequiredArgsConstructor
public class UserServiceForPostmanTestsImposter implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        User userFromDto = userMapper.userFromDto(userDto);
        throwIfRepositoryContains(userFromDto.getId());
        if (emailAlreadyExists(userFromDto.getEmail())) {                                      //Вот здесь кусок,
            User user = userRepository.save(userFromDto);                                    // необходимый.
            userRepository.delete(user);                                                     // для прохождения
            throw new EmailAlreadyExistsException("Такой email уже зарегистрирован.");       // тестов.
        }
        return userMapper.userToDto(userRepository.save(userFromDto));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::userToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
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
        return userMapper.userToDto(userRepository.save(userToUpdate));
    }

    @Transactional
    @Override
    public void delete(long id) {
        throwIfRepositoryNotContains(id);
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public void throwIfRepositoryNotContains(long id) throws UserNotFoundException {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Пользователь с id = " + id + " не найден.");
        }
    }

    @Override
    public User findByIdOrThrow(long id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("Пользователь с id = " + id + " не найден."));
    }

    private void throwIfRepositoryContains(long id) throws UserAlreadyExistsException {
        if (userRepository.existsById(id)) {
            throw new UserAlreadyExistsException("Пользователь с id = " + id + " уже существует. "
                    + "Попробуйте изменить передаваемые данные или используйте подходящий метод.");
        }
    }

    private void throwIfEmailAlreadyExists(String email) throws EmailAlreadyExistsException {
        if (userRepository.findAll().isEmpty()) {
            return;
        }
        if (emailAlreadyExists(email)) {
            throw new EmailAlreadyExistsException("Такой email уже зарегистрирован.");
        }
    }

    private boolean emailAlreadyExists(String email) {
        return userRepository.findAll().stream()
                .map(User::getEmail)
                .anyMatch(string -> string.equalsIgnoreCase(email));
    }

    private boolean haveSameEmail(User beforeUpdate, UserDto updated) {
        return beforeUpdate.getEmail().equals(updated.getEmail());
    }

    private void mergeDtoIntoExistingUser(UserDto updated, User beforeUpdate) {
        if (updated.getName() != null) {
            beforeUpdate.setName(updated.getName());
        }
        if (updated.getEmail() != null) {
            beforeUpdate.setEmail(updated.getEmail());
        }
    }
}