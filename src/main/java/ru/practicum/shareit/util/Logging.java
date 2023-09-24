package ru.practicum.shareit.util;

import lombok.Getter;
import org.slf4j.Logger;

public class Logging {

    public static void logInfoIncomingRequest(Logger log, String methodAndEndpoint, Object... params) {
        if (params.length == 0) {
            log.info("Получен запрос с методом и эндпоинтом {}. Начато выполнение.", methodAndEndpoint);
        } else {
            StringBuilder builder = new StringBuilder();
            for (Object param : params) {
                builder.append(param.getClass().getName())
                        .append(" - ")
                        .append(param)
                        .append(", ");
            }
            builder.delete(builder.length() - 2, builder.length());
            log.info("Получен запрос с методом и эндпоинтом {}'. "
                    + "Начато выполнение с параметрами: {}.", methodAndEndpoint, builder);
        }
    }

    public static void logDebugException(Logger log, Exception e) {
        log.debug("Произошла ошибка {} с сообщением '{}'.", e.getClass(), e.getMessage());
    }

    public static void logWarnException(Logger log, Exception e) {
        log.warn("Произошла ошибка {} с сообщением '{}'.", e.getClass(), e.getMessage());
    }

    public enum RepositoryOperation {
        CREATE("Создание объекта с id = {}. Данные, записанные в базу: {}."),
        UPDATE("Изменение объекта с id = {}. Данные записанные в базу: {}."),
        DELETE("Удаление объекта с id = {}."),
        ;

        @Getter
        private final String loggingMessage;

        RepositoryOperation(String loggingMessage) {
            this.loggingMessage = loggingMessage;
        }
    }
}
