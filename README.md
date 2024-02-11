# java-shareit

### Сервис сдачи вещей в аренду.

Разработка web-приложения с собственным шлюзом с использованием SpringBoot, SpringData
(JPA), J-unit, Mockito, Postgresql, SLF4J (Logback).<br>

---

> [!NOTE]<br>
> приложение на Java SE 11<br>
> для запуска приложения необходимо:<br>
>  - склонировать репозиторий
>  - проверить и при необходимости освободить 8080, 9090, 5432 порты
>  - убедиться, что `Docker daemon` запущен
>  - в терминале с поддержкой Maven, docker-compose и JDK 11 или выше выполнить
     (из папки с клонированным репозиторием)<br>
     `mvn install`<br>
     `docker-compose up -d`<br>
>
> После запуска приложения можно воспользоваться [postman-коллекцией](https://github.com/yandex-praktikum/java-shareit/blob/add-docker/postman/sprint.json)
для проверки функциональности приложения. Для правильной работы тестов время в контейнере должно совпадать 
с временем системы, на которой запущен постман. Если время вашей системы отличается от `Europe/Moscow`, измените параметр
`environment : TZ` в файле `docker-compose.yml` для контейнеров сервера и шлюза и пересоберите контейнеры.
>
> Для пользователей Windows можно запустить файл `deploy.cmd` - скрипт
создаст и запустит jar c проектом (при условии установленного JDK, Maven и запущенного Docker).

### Функциональность

- Приложение умеет создавать, изменять, возвращать сущности пользователей (user), предметов (item).
- Позволяет отсавлять комментарии к вещи (comment).
- Позволяет создавать и проматривать запросы на бронирование вещей (booking).
- Позволяет размещать запросы на добавление вещей в приложение для аренды (request).
- Умеет принимать Http-запросы (Tomcat).
- Умеет сохранять данные в БД (JPA-Postgres).
- Реализована валидация данных (Hibernate).
- Реализовано логирование (Logback).

