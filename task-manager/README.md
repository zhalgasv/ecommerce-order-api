# Task Manager

Учебный REST API: Java 21, Spring Boot 3.5.3, Maven, PostgreSQL 16, Liquibase, Swagger.
Без авторизации и UI. Все клиенты имеют доступ ко всем данным; userId определяет владельца задачи, но не подтверждает личность клиента.

## Запуск

Откройте эту папку как отдельный Maven-проект в IntelliJ IDEA. Выберите Project SDK и Maven Runner JDK 21.
Docker Desktop должен быть запущен.

```bash
cd task-manager
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
docker compose up -d --wait
./mvnw spring-boot:run
```

Если терминал уже открыт в папке task-manager, первую команду пропустите.
API: http://localhost:8082/api/tasks
Swagger: http://localhost:8082/swagger-ui.html
OpenAPI: http://localhost:8082/v3/api-docs

PostgreSQL в IntelliJ: host localhost, port 5434, database task_manager, user/password task_manager.
Настройки приложения можно переопределить через SERVER_PORT, DB_URL, DB_USERNAME, DB_PASSWORD.
Liquibase создаёт схему при запуске, Hibernate проверяет соответствие entity и таблиц.
Остановка БД без удаления данных: `docker compose stop`.

## API

| Ресурс | Создать | Список | По ID | Полная замена | Удалить |
|---|---|---|---|---|---|
| Пользователи | POST /api/users | GET /api/users | GET /api/users/{id} | PUT /api/users/{id} | DELETE /api/users/{id} |
| Категории | POST /api/categories | GET /api/categories | GET /api/categories/{id} | PUT /api/categories/{id} | DELETE /api/categories/{id} |
| Задачи | POST /api/tasks | GET /api/tasks | GET /api/tasks/{id} | PUT /api/tasks/{id} | DELETE /api/tasks/{id} |

Фильтрация: `GET /api/tasks?categoryId=1`, можно добавить `userId=1`.
Пагинация: `?page=0&size=20&sort=id,asc`. Списки возвращают `content` и метаданные `page`.
Неизвестная категория/пользователь в фильтре возвращает 404.

Сначала создайте пользователя и категорию; подставьте их реальные ID при создании задачи:

```json
{"name":"Alice","email":"alice@example.com"}
```

```json
{"name":"Work"}
```

```json
{"title":"Learn Spring","description":"Understand DI","status":"TODO","userId":1,"categoryId":1}
```

Статусы: TODO, IN_PROGRESS, DONE. PUT требует все обязательные поля.
201 + Location при создании; 200 при чтении/обновлении; 204 при удалении;
400 при неправильных данных; 404 при отсутствии ресурса; 409 при конфликте.
Email хранится в нижнем регистре и уникален. Названия категорий уникальны с учётом регистра.
Пользователя/категорию с задачами удалить нельзя: сначала удалите задачи либо переназначьте их через PUT.

## Postman

Импортируйте `postman/TaskManager.postman_collection.json` и запустите Collection Runner в исходном порядке.
Коллекция сохраняет ID созданных ресурсов автоматически. Проверяет CRUD, фильтр, ошибки 400/404/409 и удаляет созданные данные.
baseUrl по умолчанию http://localhost:8082.

## Тесты

```bash
./mvnw test
```

TaskApiH2Test проверяет HTTP-сценарии и миграцию на H2 в PostgreSQL-режиме.
TaskApiIntegrationTest выполняет те же сценарии на настоящем PostgreSQL через Testcontainers, если Docker доступен.
Без Docker PostgreSQL-тест пропускается; H2 не заменяет проверку поведения PostgreSQL.

## Как читать код

Начните с TaskController → TaskService → TaskRepository → Task.
Controller принимает HTTP и валидирует DTO. Service проверяет связанные объекты и задаёт границу транзакции.
Spring Data создаёт реализацию Repository; Hibernate переводит операции в SQL, JDBC-драйвер отправляет их PostgreSQL.
Entity описывает таблицы и связи. Request/Response отделяют контракт API от entity.
Зависимости сервисов передаются через конструкторы. DTO формируются внутри транзакции; Open Session in View отключён.
При PUT загруженная entity управляется Hibernate: изменения сохраняются через dirty checking при commit, поэтому повторный save не нужен.
Внешние ключи и уникальные ограничения защищают данные также при конкурентных запросах.
