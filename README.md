# Система Управления Банковскими Картами

REST API система для управления банковскими картами с полной аутентификацией, авторизацией и безопасностью.

## Стек Технологий

- **Java 21** - Основной язык программирования
- **Spring Boot 3.5.6** - Фреймворк для создания приложений
- **Spring Security** - Безопасность и аутентификация
- **JWT** - Токены доступа
- **PostgreSQL** - Основная база данных
- **Liquibase** - Миграции базы данных
- **Swagger/OpenAPI** - Документация API
- **Docker** - Контейнеризация
- **Maven** - Управление зависимостями

## Функциональность

### Пользователи
- Регистрация и аутентификация
- Просмотр своих карт с пагинацией и фильтрацией
- Создание новых карт
- Блокировка своих карт
- Переводы между своими картами
- Просмотр баланса

###  Администраторы
- Все функции пользователя
- Просмотр всех карт в системе
- Управление картами (создание, блокировка, активация, удаление)
- Управление пользователями

### Безопасность
- JWT аутентификация
- Ролевая авторизация (USER, ADMIN)
- Шифрование номеров карт (AES)
- Маскирование номеров карт (**** **** **** 1234)
- Валидация всех входных данных

## Установка и запуск

### Docker Compose

1. Клонируйте репозиторий:
```bash
git clone https://github.com/riloidx/bank-rest
cd bank-rest
```

2. Запустите с помощью Docker Compose:
```bash
docker-compose up -d
```

3. Приложение будет доступно по адресу: http://localhost:8080/api

## API Документация

После запуска приложения документация Swagger UI доступна по адресу:
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/api-docs

## Аутентификация

### Регистрация
```bash
POST /api/registration
Content-Type: application/json

{
  "name": "Иван Иванов",
  "email": "user@example.com",
  "password": "Password123"
}
```

### Вход в систему
```bash
POST /api/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "Password123"
}
```

### Вход в систему с админ ролью (создан в миграциях)
```bash
POST /api/login
Content-Type: application/json

{
  "email": "matveybabashka@gmail.com",
  "password": "admin123"
}
```

### Использование токена
Добавьте полученный JWT токен в заголовок Authorization:
```bash
Authorization: Bearer <your-jwt-token>
```

## Основные эндпоинты

### Карты
- `GET /api/cards/my` - Получить свои карты
- `POST /api/cards` - Создать новую карту
- `POST /api/cards/{id}/block` - Заблокировать карту
- `POST /api/cards/transfer` - Перевод между картами

### Администрирование
- `GET /api/cards` - Все карты (только админ)
- `GET /api/admin/users` - Все пользователи (только админ)
- `POST /api/cards/{id}/activate` - Активировать карту (только админ)

### Пользователи
- `GET /api/users/me` - Информация о текущем пользователе

## Конфигурация

### Переменные окружения

| Переменная | Описание | По умолчанию |
|------------|----------|--------------|
| `SPRING_DATASOURCE_URL` | URL базы данных | `jdbc:postgresql://localhost:5432/bank_db` |
| `SPRING_DATASOURCE_USERNAME` | Пользователь БД | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Пароль БД | `postgres` |
| `JWT_SECRET_KEY` | Секретный ключ JWT | (см. application.yml) |
| `ENCRYPTION_SECRET_KEY` | Ключ шифрования карт | (см. application.yml) |
