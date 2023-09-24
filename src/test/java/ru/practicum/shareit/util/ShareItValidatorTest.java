package ru.practicum.shareit.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.Validator;
import javax.validation.metadata.ConstraintDescriptor;
import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShareItValidatorTest {
    private final User user = new User(null, "name", "description");
    @Mock
    private Validator validator;
    @InjectMocks
    private ShareItValidator shareItValidator;

    @Test
    public void methodValidateCallsValidator() {
        when(validator.validate(any())).thenReturn(Collections.emptySet());

        Assertions.assertDoesNotThrow(() -> shareItValidator.validate(user));
        verify(validator, times(1)).validate(user);
    }

    @Test
    public void methodValidateTrowsException() {
        when(validator.validate(any())).thenReturn(Set.of(new ConstraintViolation<>() {
            @Override
            public String getMessage() {
                return null;
            }

            @Override
            public String getMessageTemplate() {
                return null;
            }

            @Override
            public User getRootBean() {
                return null;
            }

            @Override
            public Class<Object> getRootBeanClass() {
                return null;
            }

            @Override
            public Object getLeafBean() {
                return null;
            }

            @Override
            public Object[] getExecutableParameters() {
                return new Object[0];
            }

            @Override
            public Object getExecutableReturnValue() {
                return null;
            }

            @Override
            public Path getPropertyPath() {
                return null;
            }

            @Override
            public Object getInvalidValue() {
                return null;
            }

            @Override
            public ConstraintDescriptor<?> getConstraintDescriptor() {
                return null;
            }

            @Override
            public <U> U unwrap(Class<U> aClass) {
                return null;
            }
        }));

        Assertions.assertThrows(ConstraintViolationException.class, () -> shareItValidator.validate(user));
        verify(validator, times(1)).validate(user);
    }
}
