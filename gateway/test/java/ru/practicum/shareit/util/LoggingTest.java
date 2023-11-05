package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LoggingTest {
    Object object = new Object() {
        @Override
        public String toString() {
            return "toString";
        }
    };
    @Mock
    private Logger log;

    @Test
    public void methodLogInfoIncomingRequestCallsLoggerWithoutParams() {
        Logging.logInfoIncomingRequest(log, "GET /");
        verify(log, times(1))
                .info("Получен запрос с методом и эндпоинтом {}. Начато выполнение.", "GET /");
    }

    @Test
    public void methodLogInfoIncomingRequestCallsLoggerWithParams() {
        Logging.logInfoIncomingRequest(log, "Post /", object);

        verify(log, times(1))
                .info(any(String.class), any(String.class), any(StringBuilder.class));
    }

    @Test
    public void methodLogDebugExceptionCallsLogger() {
        Logging.logDebugException(log, new Exception());

        verify(log, times(1))
                .debug(any(String.class), any(), any());
    }

    @Test
    public void methodLogWarnExceptionCallsLogger() {
        Logging.logDebugException(log, new Exception());

        verify(log, times(1))
                .debug(any(String.class), any(), any());
    }
}
