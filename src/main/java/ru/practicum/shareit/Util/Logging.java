package ru.practicum.shareit.Util;

import org.slf4j.Logger;

public class Logging {
    public static void logInfoExecutedMethod(Logger log, Object... params) {
        if (params.length == 0) {
            log.info("Начато выполнение метода '{}'.", Thread.currentThread().getStackTrace()[2].getMethodName());
        } else {
            StringBuilder builder = new StringBuilder();
            for (Object param : params) {
                builder.append(param.getClass().getName())
                        .append(" - ")
                        .append(param)
                        .append(", ");
            }
            builder.delete(builder.length() - 2, builder.length());
            log.info("Начато выполнение метода '{}' c параметрами: {}.",
                    Thread.currentThread().getStackTrace()[2].getMethodName(), builder);
        }
    }

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

    public static void logWarnException(Logger log, Exception e) {
        log.warn("Произошла ошибка {} с сообщением '{}'.", e.getClass(), e.getMessage());
    }
}
