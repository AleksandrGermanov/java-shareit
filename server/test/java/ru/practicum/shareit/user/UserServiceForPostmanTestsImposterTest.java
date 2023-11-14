package ru.practicum.shareit.user;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.alreadyExists.AlreadyExistsException;
import ru.practicum.shareit.exception.alreadyExists.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.alreadyExists.UserAlreadyExistsException;
import ru.practicum.shareit.exception.notFound.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceForPostmanTestsImposter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceForPostmanTestsImposterTest {
    private final UserDto userDto = new UserDto(0L, "name", "e@ma.il");
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceForPostmanTestsImposter userService;

    @BeforeEach
    public void setMockBehaviour() {
        lenient().when(userRepository.findAll())
                .thenReturn(Collections.emptyList());
        lenient().when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        lenient().when(userRepository.save(any(User.class)))
                .thenAnswer(invocationOnMock -> {
                    User user = invocationOnMock.getArgument(0, User.class);
                    user.setId(1001L);
                    return user;
                });
        lenient().when(userMapper.userFromDto(any(UserDto.class)))
                .thenAnswer(invocationOnMock -> {
                    UserDto u = invocationOnMock.getArgument(0, UserDto.class);
                    return new User(u.getId(), u.getName(), u.getEmail());
                });
        lenient().when(userMapper.userToDto(any(User.class)))
                .thenAnswer(invocationOnMock -> {
                    User u = invocationOnMock.getArgument(0, User.class);
                    return new UserDto(u.getId(), u.getName(), u.getEmail());
                });
    }

    @Test
    public void methodCreateCallsValidatorMapperAndRepository() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        Assertions.assertInstanceOf(UserDto.class, userService.create(userDto));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(userRepository, times(1)).findAll();
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).userToDto(any(User.class));
        verify(userMapper, times(1)).userFromDto(any(UserDto.class));
    }

    @Test
    public void methodCreateReturnsUserDtoWithMockDrivenParams() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        UserDto saved = userService.create(userDto);
        Assertions.assertInstanceOf(UserDto.class, saved);
        Assertions.assertEquals(userDto.getName(), saved.getName());
        Assertions.assertEquals(userDto.getEmail(), saved.getEmail());
        Assertions.assertNotEquals(userDto.getId(), saved.getId());
    }

    @Test
    public void methodCreateThrowsExceptionAndDeletesSavedUserIfEmailAlreadyExists() {
        when(userRepository.findAll())
                .thenReturn(List.of(new User(1L, "nm", userDto.getEmail())));
        when(userRepository.existsById(anyLong())).thenReturn(false);

        Assertions.assertThrows(EmailAlreadyExistsException.class, () -> userService.create(userDto));
        verify(userRepository, times(1)).delete(any(User.class));
    }

    @Test
    public void methodCreateThrowsExceptionIfUserAlreadyExists() {
        Assertions.assertThrows(AlreadyExistsException.class, () -> userService.create(userDto));
        Assertions.assertThrows(UserAlreadyExistsException.class, () -> userService.create(userDto));
    }

    @Test
    public void methodFindAllCallsRepositoryAndReturnsListOfUserDto() {
        when(userRepository.findAll())
                .thenReturn(List.of(new User(1L, "nm", "ema@i.l")));

        Assertions.assertInstanceOf(UserDto.class, userService.findAll().get(0));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void methodRetrieveCallsRepositoryAndReturnsUserDto() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(new User(1L, "nm", "ema@i.l")));

        Assertions.assertInstanceOf(UserDto.class, userService.retrieve(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void methodRetrieveThrowsExceptionIfRepositoryReturnsEmptyOptional() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.retrieve(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void methodUpdateCallsRepositoryMapperAndValidator() {
        when(userRepository.findById(0L))
                .thenReturn(Optional.of(new User(0L, "nm", "ema@i.l")));

        userService.update(userDto);
        verify(userRepository, times(1)).findById(0L);
        verify(userRepository, times(1)).findAll();
        verify(userRepository, times(1)).save(any());
        verify(userMapper, times(1)).userToDto(any());
    }

    @Test
    public void methodUpdateDoesNotCallsRepositoryFindAllIfEmailNotUpdated() {
        when(userRepository.findById(0L))
                .thenReturn(Optional.of(new User(0L, "nm", "e@ma.il")));

        userService.update(userDto);
        verify(userRepository, times(1)).findById(0L);
        verify(userRepository, times(1)).save(any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void methodUpdateMergesDtoIntoExistingUser() {
        when(userRepository.findById(0L))
                .thenReturn(Optional.of(new User(0L, "nm", "ema@i.l")));
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocationOnMock -> {
                    User user = invocationOnMock.getArgument(0, User.class);
                    return user;
                });

        UserDto fetched = userService.update(userDto);
        Assertions.assertEquals(fetched.getId(), userDto.getId());
        Assertions.assertEquals(fetched.getName(), userDto.getName());
        Assertions.assertEquals(fetched.getEmail(), userDto.getEmail());
    }

    @Test
    public void methodUpdateThrowsExceptionWhenNoUserFoundForUpdate() {
        when(userRepository.findById(0L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.update(userDto));
    }

    @Test
    public void methodUpdateThrowsExceptionWhenUpdatedEmailAlreadyInBase() {
        when(userRepository.findById(0L))
                .thenReturn(Optional.of(new User(0L, "nm", "ema@i.l")));
        when(userRepository.findAll()).thenReturn(List.of(new User(1000L, "user", "e@ma.il")));

        Assertions.assertThrows(EmailAlreadyExistsException.class, () -> userService.update(userDto));
    }

    @Test
    public void methodUpdateReturnsUserDto() {
        when(userRepository.findById(0L))
                .thenReturn(Optional.of(new User(0L, "nm", "ema@i.l")));

        Assertions.assertInstanceOf(UserDto.class, userService.update(userDto));
    }

    @Test
    public void methodDeleteCallsRepository() {
        userService.delete(0L);

        verify(userRepository, times(1)).existsById(0L);
        verify(userRepository, times(1)).deleteById(0L);
    }

    @Test
    public void methodDeleteThrowsExceptionIfUserNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.delete(0L));
    }

    @Test
    public void methodThrowIfRepositoryNotContainsCallsRepository() {
        userService.throwIfRepositoryNotContains(0L);
        verify(userRepository, times(1)).existsById(0L);
    }

    @Test
    public void methodThrowIfRepositoryNotContainsThrowsExceptionWhenNoIdExistsInRepository() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.throwIfRepositoryNotContains(0L));
    }

    @Test
    public void methodFindByIdOrThrowCallsRepository() {
        when(userRepository.findById(0L))
                .thenReturn(Optional.of(new User(0L, "nm", "ema@i.l")));

        userService.findByIdOrThrow(0L);
        verify(userRepository, times(1)).findById(0L);
    }

    @Test
    public void methodFindByIdOrThrowReturnsUserIfFound() {
        when(userRepository.findById(0L))
                .thenReturn(Optional.of(new User(0L, "nm", "ema@i.l")));

        Assertions.assertInstanceOf(User.class, userService.findByIdOrThrow(0L));
    }

    @Test
    public void methodFindByIdOrThrowThrowsExceptionIfUserNotFound() {
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.findByIdOrThrow(0L));
    }
}
