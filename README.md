# Система бронирования отелей

Многомодульный проект распределённого приложения на Spring Boot/Cloud, где каждый модуль имитирует отдельный микросервис:
- API Gateway (Spring Cloud Gateway для проксирования трафика);
- Booking Service (создание/управление бронированиями, согласованность данными со сторонним сервисом, регистрация/авторизация пользователей, администрирование);
- Hotel Management Service (управление отелями и номерами (CRUD), агрегация по загруженности, администрирование);
- Eureka Server (Service Registry, динамическое обнаружение сервисов).

Все сервисы используют встроенную БД H2. Взаимодействие между сервисами выполняется при помощи протокола HTTP как последовательность локальных транзакций (без учета глобальных распределённых транзакций).


## Возможности системы бронирования отелей
- Регистрация и вход пользователей через Booking Service;
- Создание бронирований с двухшаговой согласованностью между сервисами Booking Service и Hotel Management Service (PENDING → CONFIRMED/CANCELLED с компенсацией);
- Потокобезопасное создание резерва номера без дублей на установленные даты при формировании бронирований в пиковые периоды нагрузки, а также идемпотентная операция обработки самих бронирований с использованием идентификатора запроса, задаваемого в шлюзе.
- Повторы с экспоненциальной паузой и таймауты при удалённых вызовах стороннего сервиса;
- Механизм автоматического подбора номера на установленные даты при наименьшей нагруженности;
- Администрирование пользователей и отелей/номеров;
- Предоставление агрегированной информацией о номерах и отелях с использованием пагинации;
- Сквозная корреляция в рамках единого идентификатора бронирования ***bookingId***.


## Архитектура и порты
- `eureka-server`: порт 8761;
- `api-gateway`: порт 8080, регистрируется в Eureka под именем `API-GATEWAY`;
- `hotel-management-service`: порт 8082, регистрируется в Eureka под именем `HOTEL-MANAGEMENT-SERVICE`;
- `booking-service`: порт 8081, регистрируется в Eureka под именем `BOOKING-SERVICE`.

API Gateway маршрутизирует запросы, дополняя их пользовательским заголовком с вычисленным идентификатором для каждого из них, к сервисам по их serviceId через Eureka и проксирует заголовок `Authorization` (JWT).


## Требования
- JDK 17+;
- Gradle;
- IntelliJ IDEA для локального запуска.

## Инструкция по запуску проекта
1) Склонировать репозиторий:
```bash
git clone https://github.com/euchekavelo/hotel-reservation-system.git
```
2) Открыть через Intellij IDEA склонированный проект `hotel-reservation-system`.
3) Внутри проекта открыть справа контекстное меню Gradle и через значок **+** (если навести будет надпись **Link Gradle Project**) указать папки модулей `eureka-server`, `booking-service`, `hotel-management-service`, `api-gateway`.
3) Запустить первый модуль `eureka-server` через единственных метод ***main*** класса ***EurekaServerApplication***;
4) Запустить второй модуль `booking-service` через единственных метод ***main*** класса ***BookingServiceApplication***;
5) Запустить третий модуль `hotel-management-service` через единственных метод ***main*** класса ***HotelManagementServiceApplication***;
6) Запустить четвертый модуль `api-gateway` через единственных метод ***main*** класса ***ApiGatewayApplication***.


## Конфигурация JWT
Для демонстрации используется симметричный ключ HMAC, значение которого задаётся свойством `app-jwt-secret`, а время жизни свойством `app-jwt-token-expiration-milliseconds` сроком в 1 час в виде миллисекунд.
<br>
Указанные свойства можно найти в конфигурационных файлах ***application.yml*** в ресурсах модулей `booking-service` и `hotel-management-service` соответственно.
<br>
**Важно:** для продакшена рекомендуется заменить указанный механизм выдачи и проверки токенов на интеграцию с каким-либо провайдером, например, Keycloak.


## Быстрый старт по созданию брони (через Gateway на 8080)
1) Создать пользователя с ролью администратор
```bash
curl --location --request POST 'localhost:8080/booking-service/users/register' \
--header 'Content-Type: application/json' \
--data '{
    "username": "test1",
    "password": "test1",
    "role": "ADMIN"
}'
```
2) Выполнить вход под созданным администратором
```bash
curl --location --request POST 'localhost:8080/booking-service/users/auth' \
--header 'Content-Type: application/json' \
--data '{
    "username": "test1",
    "password": "test1"
}'
```
3) Создать отель (необходимо использовать JWT-токен для УЗ с ролью администратора):
```bash
curl --location --request POST 'localhost:8080/hotel-management-service/hotels' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <Тут токен админа>' \
--data '{
    "name": "test_hotel1",
    "address": "test_address1"
}'
```
4) Создать номер (необходимо использовать JWT-токен для УЗ с ролью администратора):
```bash
curl --location --request POST 'localhost:8080/hotel-management-service/rooms' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <Тут токен админа>' \
--data '{
    "hotelId": 1,
    "number": "test1"
}'
```
5) Зарегистрироваться из-под пользователя с ролью USER:
```bash
curl --location --request POST 'localhost:8080/booking-service/users/register' \
--header 'Content-Type: application/json' \
--data '{
    "username": "test2",
    "password": "test2"
}'
```
6) Авторизоваться из-под пользователя с ролью USER:
```bash
curl --location --request POST 'localhost:8080/booking-service/users/auth' \
--header 'Content-Type: application/json' \
--data '{
    "username": "test2",
    "password": "test2"
}'
```
7) Сформировать бронирование номера с его резервом из-под пользователя с ролью USER (необходимо использовать JWT-токен для УЗ с ролью USER):
```bash
curl --location --request POST 'localhost:8080/booking-service/bookings' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <Тут токен пользователя>' \
--data '{
    "roomId": 1,
    "autoSelect": false,
    "startDate": "2025-12-01",
    "endDate": "2025-12-10"
}'
```
8) Сформировать бронирование номера с его резервом из-под пользователя с ролью USER (необходимо использовать JWT-токен для УЗ с ролью USER):
```bash
curl --location --request POST 'localhost:8080/booking-service/bookings' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <Тут токен пользователя>' \
--data '{
    "roomId": 1,
    "autoSelect": false,
    "startDate": "2025-12-01",
    "endDate": "2025-12-10"
}'
```

## Основные эндпойнты
Через Gateway (8080):
- Booking Service:
    - POST `/booking-service/users/register` — регистрация (для админа добавить `"role": ADMIN`);
    - POST `/booking-service/users/auth` — получение JWT-токена;
    - POST `/booking-service/users` — создать пользователя (ADMIN);
    - DELETE `/booking-service/users/{userId}` — удалить пользователя (ADMIN);
    - PATCH `/booking-service/users/{userId}` — обновить пользователя (ADMIN);
    - POST `/booking-service/bookings` — создать бронирование (USER);
    - GET `/booking-service/bookings` — получить бронирования пользователя с пагинацией (USER);
    - GET `/booking-service/bookings/{bookingId}` — получить конкретное бронирование пользователя (USER);
    - DELETE `/booking-service/bookings/{bookingId}` — удалить конкретное бронирование пользователя (USER);
- Hotel Management Service:
    - POST `/hotel-management-service/hotels` — добавить отель (ADMIN);
    - POST `/hotel-management-service/rooms` — добавить номер (ADMIN);
    - GET `/hotel-management-service/hotels` — получить список отелей с пагинацией (USER);
    - GET `/hotel-management-service/rooms/recommend?startDate=2025-12-01&endDate=2025-12-15` — получить список рекомендованных отсортированных по возрастанию атрибута `times_booked` номеров с пагинацией, которые могут быть забронированы на указанные даты;
    - GET `/hotel-management-service/rooms?startDate=2025-11-01&endDate=2025-11-15` — получить список номеров без сортировки с пагинацией, которые могут быть забронированы на указанные даты (USER);
    - POST `/hotel-management-service/rooms/{roomId}}/confirm-availability` — подтвердить доступность номера;
    - POST `/hotel-management-service/rooms/1/release` - компенсирующее действие по отмене резерва номера на указанные даты;
    - DELETE `/hotel-management-service/room-reservations/by-booking/{bookingId}` - удалить резерв номера по конкретному номеру бронирования ***bookingId***.

**Примечание:** в некоторых эндпоинтах реализована функция пагинации (постраничного вывода информации). 
Для подробной информации необходимо ознакомиться по ссылке в Swagger OpenAPI, выбрав необходимый микросервис: http://localhost:8080/swagger-ui.html .


## Согласованность и надёжность
- Локальные транзакции внутри каждого сервиса (`@Transactional`);
- Двухшаговый процесс по созданию брони: запрос после шлюза проходит через Booking Service -> Hotel Management Service;
- Идемпотентность по `requestId` (повторные запросы не создают дубликаты и не меняют состояние повторно), а также потокобезопасность при создании резерва номера на указанные даты при помощи пессимистической блокировки номера во время операции;
- Повторы с backoff и тайм-ауты в удалённых вызовах к API Hotel Management Service через `WebClient`;
- Сквозной заголовок `X-Request-Id` логируется в Booking Service и пробрасывается в Hotel Management Service, позволяя отследить основные события в рамках запроса при бронировании;
- Дополнительно логируются некоторые события создаваемого бронирования по его идентификатору `bookingId` (сквозная корреляция), что также позволяет отслеживать изменения состояний для данного бронирования.


## Консоль встроенной базы данных H2
Включена для обоих основных сервисов:
- Для сервиса Booking Service по ссылке: http://localhost:8081/booking-service/h2-console ;
- Для сервиса Hotel Management Service по ссылке: http://localhost:8082/hotel-management-service/h2-console ;
<br>

Соответствующие параметры для подключения к выделенным БД расположены в файле ***application.yml*** в ресурсах каждого микросервиса.


## Swagger / OpenAPI
- Booking Service UI: `http://localhost:8081/booking-service/swagger-ui.html`
- Hotel Service UI: `http://localhost:8082/hotel-management-service/swagger-ui.html`
- Gateway (агрегация UI): `http://localhost:8080/swagger-ui.html` (переключатель спецификаций в шлюзе)


## Выводы и возможные улучшения
В рамках данного учебного проекта многие подходы и методики упрощены. Для полноценной работы, приближенной к продуктовой среде, 
можно интегрировать внешний провайдер для выдачи и проверки токенов, например, на базе Keycloak в связке с корпоративным LDAP.
Также отказоустойчивость можно усилить при помощи механизмов Circuit Breaker, централизованным логированием и трассировкой, 
а также выполнить настройку сбора ключевых метрик для мониторинга работы комплекса микросервисов.