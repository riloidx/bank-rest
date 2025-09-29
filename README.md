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

### Вариант 1: Docker Compose (Рекомендуется)

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

### Вариант 2: Локальный запуск

1. Установите PostgreSQL и создайте базу данных:
```sql
CREATE DATABASE bank_db;
```

2. Настройте переменные окружения или обновите `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bank_db
    username: postgres
    password: postgres
```

3. Запустите приложение:
```bash
./mvnw spring-boot:run
```

### Вариант 3: Сборка JAR

1. Соберите проект:
```bash
./mvnw clean package
```

2. Запустите JAR файл:
```bash
java -jar target/bank-rest-0.0.1-SNAPSHOT.jar
```

## 📚 API Документация

После запуска приложения документация Swagger UI доступна по адресу:
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/api-docs

## 🔑 Аутентификация

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

### Использование токена
Добавьте полученный JWT токен в заголовок Authorization:
```bash
Authorization: Bearer <your-jwt-token>
```

## 📊 Основные эндпоинты

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

## 🧪 Тестирование

### Запуск всех тестов
```bash
./mvnw test
```

### Запуск только unit тестов
```bash
./mvnw test -Dtest="*Test"
```

### Запуск интеграционных тестов
```bash
./mvnw test -Dtest="*IntegrationTest"
```

## 🔧 Конфигурация

### Переменные окружения

| Переменная | Описание | По умолчанию |
|------------|----------|--------------|
| `SPRING_DATASOURCE_URL` | URL базы данных | `jdbc:postgresql://localhost:5432/bank_db` |
| `SPRING_DATASOURCE_USERNAME` | Пользователь БД | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Пароль БД | `postgres` |
| `JWT_SECRET_KEY` | Секретный ключ JWT | (см. application.yml) |
| `ENCRYPTION_SECRET_KEY` | Ключ шифрования карт | (см. application.yml) |

### Профили Spring

- `default` - Локальная разработка
- `docker` - Запуск в Docker
- `test` - Тестирование (H2 база данных)

## 📁 Структура проекта

```
src/
├── main/
│   ├── java/org/matvey/bankrest/
│   │   ├── config/          # Конфигурация
│   │   ├── controller/      # REST контроллеры
│   │   ├── dto/            # DTO объекты
│   │   ├── entity/         # JPA сущности
│   │   ├── exception/      # Обработка исключений
│   │   ├── mapper/         # MapStruct мапперы
│   │   ├── repository/     # JPA репозитории
│   │   ├── security/       # Безопасность и JWT
│   │   ├── service/        # Бизнес-логика
│   │   ├── util/          # Утилиты
│   │   └── validation/    # Кастомная валидация
│   └── resources/
│       ├── db/migration/   # Liquibase миграции
│       └── application*.yml # Конфигурация
└── test/                   # Тесты
```

## 🐛 Известные ограничения

1. Переводы возможны только между картами одного пользователя
2. Нет ограничений на количество карт у пользователя
3. Отсутствует история транзакций (планируется в следующих версиях)
4. Нет rate limiting для API запросов

## 🤝 Вклад в проект

1. Форкните репозиторий
2. Создайте ветку для новой функции (`git checkout -b feature/amazing-feature`)
3. Зафиксируйте изменения (`git commit -m 'Add amazing feature'`)
4. Отправьте в ветку (`git push origin feature/amazing-feature`)
5. Откройте Pull Request

## 📄 Лицензия

Этот проект лицензирован под MIT License - см. файл [LICENSE](LICENSE) для деталей.

## 📞 Поддержка

Если у вас есть вопросы или проблемы, создайте issue в репозитории или свяжитесь с командой разработки.