# DAO Hibernate Security — Spring Boot + Spring Security

Учебный проект по работе с базой данных и обеспечению безопасности приложения на **двух уровнях**:
- **URL-уровень** — через `SecurityFilterChain` в `SecurityConfig`.
- **Метод-уровень** — через `@Secured`, `@RolesAllowed` и `@PreAuthorize`.

---

## 📋 Описание

Приложение демонстрирует:
- стандартную форму логина Spring Security;
- разграничение доступа по URL (публичные и защищённые эндпоинты);
- разграничение доступа на уровне методов с помощью трёх типов аннотаций;
- проверку владения данными через SpEL-выражение (`#username == authentication.name`).

---

## 🛠️ Стек технологий

| Технология | Версия / назначение |
|-----------|---------------------|
| Java | 17+ |
| Spring Boot | 3.x / 4.x |
| Spring Web | `spring-boot-starter-web` |
| Spring Security | `spring-boot-starter-security` |
| Spring Data JPA | `spring-boot-starter-data-jpa` |
| Hibernate | ORM |
| PostgreSQL | база данных |
| Maven | сборка проекта |

---

## 📁 Структура проекта

```
DAO_Hibernate_Security_OAuth2/
├── src/
│   ├── main/
│   │   ├── java/ru/netology/DAO_Hibernate/
│   │   │   ├── DaoHibernateApplication.java        # главный класс
│   │   │   ├── SecurityConfig.java                 # настройка Spring Security
│   │   │   ├── controller/
│   │   │   │   ├── PersonController.java           # REST-контроллер (URL-уровень)
│   │   │   │   └── MethodSecurityController.java   # контроллер с аннотациями
│   │   │   └── repository/
│   │   │       └── PersonRepository.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── ...
│   └── test/
├── pom.xml
├── .gitignore
└── README.md
```

---

## 🔐 Пользователи и роли

В проекте три пользователя, заданных через `InMemoryUserDetailsManager`:

| Логин | Пароль | Роли |
|-------|--------|------|
| `reader` | `reader123` | `ROLE_READ` |
| `writer` | `writer123` | `ROLE_READ`, `ROLE_WRITE` |
| `admin` | `admin123` | `ROLE_READ`, `ROLE_WRITE`, `ROLE_DELETE` |

> ⚠️ `{noop}` перед паролем означает «без шифрования» — только для учебного примера. В реальном проекте используйте `{bcrypt}`.

---

## 🛡️ Уровень 1: Защита по URL (`SecurityFilterChain`)

В `SecurityConfig`:

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/persons").permitAll()
                    .anyRequest().authenticated()
            )
            .formLogin(form -> form.permitAll())
            .logout(logout -> logout.permitAll());

    return http.build();
}
```

| URL | Доступ |
|-----|--------|
| `/api/persons` | Публичный (без авторизации) |
| Все остальные | Только после логина |

---

## 🛡️ Уровень 2: Защита на уровне методов

В `SecurityConfig` включено:

```java
@EnableMethodSecurity(
        prePostEnabled = true,   // @PreAuthorize / @PostAuthorize
        securedEnabled = true,   // @Secured
        jsr250Enabled = true     // @RolesAllowed
)
```

### Эндпоинты `MethodSecurityController`

| Метод | URL | Аннотация | Кто пройдёт |
|-------|-----|-----------|-------------|
| `readOnly()` | `/api/method-security/read-only` | `@Secured("ROLE_READ")` | `reader`, `writer`, `admin` |
| `writeOnly()` | `/api/method-security/write-only` | `@RolesAllowed("WRITE")` | `writer`, `admin` |
| `writeOrDelete()` | `/api/method-security/write-or-delete` | `@PreAuthorize("hasAnyAuthority('ROLE_WRITE', 'ROLE_DELETE')")` | `writer`, `admin` |
| `self()` | `/api/method-security/self?username=...` | `@PreAuthorize("#username == authentication.name")` | только владелец имени |

### Код контроллера

```java
@RestController
@RequestMapping("/api/method-security")
public class MethodSecurityController {

    @Secured("ROLE_READ")
    @GetMapping("/read-only")
    public String readOnly() {
        return "Доступно пользователям с ролью READ";
    }

    @RolesAllowed("WRITE")
    @GetMapping("/write-only")
    public String writeOnly() {
        return "Доступно пользователям с ролью WRITE";
    }

    @PreAuthorize("hasAnyAuthority('ROLE_WRITE', 'ROLE_DELETE')")
    @GetMapping("/write-or-delete")
    public String writeOrDelete() {
        return "Доступно пользователям с ролью WRITE или DELETE";
    }

    @PreAuthorize("#username == authentication.name")
    @GetMapping("/self")
    public String self(@RequestParam("username") String username) {
        return "Доступно только пользователю с именем: " + username;
    }
}
```

---

## 🎯 Разное поведение аннотаций

В **Spring Security 6/7** аннотации ведут себя **по-разному** с префиксом `ROLE_`.

| Аннотация | Добавляет `ROLE_` сам? | Что писать для роли `ROLE_WRITE` |
|-----------|------------------------|-----------------------------------|
| `@Secured(...)` | ❌ Нет | `@Secured("ROLE_WRITE")` |
| `@RolesAllowed(...)` | ✅ Да | `@RolesAllowed("WRITE")` |
| `@PreAuthorize("hasRole('...')")` | ✅ Да | `hasRole('WRITE')` |
| `@PreAuthorize("hasAuthority('...')")` | ❌ Нет | `hasAuthority('ROLE_WRITE')` |
| `@PreAuthorize("hasAnyAuthority(...)")` | ❌ Нет | `hasAnyAuthority('ROLE_WRITE', 'ROLE_DELETE')` |

**Правило:**
- `hasRole` и `@RolesAllowed` — **добавляют** префикс 
- `hasAuthority` и `@Secured` — **не добавляют** префикс

Пример ошибки: `@RolesAllowed("ROLE_WRITE")` → Spring сделает `ROLE_ROLE_WRITE` → 403.

---

## 🚀 Запуск

### 1. Требования

- JDK 17+
- Maven
- PostgreSQL (локально, порт `5432`)
- Созданная база данных (например, `DAO_Hibernate`)

### 2. `application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/DAO_Hibernate
spring.datasource.username=postgres
spring.datasource.password=ВАШ_ПАРОЛЬ
```

### 3. Сборка и запуск

```bash
mvn clean install
mvn spring-boot:run
```

Или запустить `DaoHibernateApplication` из IntelliJ IDEA.

Приложение поднимется на `http://localhost:8080`.

---

## 🧪 Проверка безопасности

### Публичный эндпоинт

```bash
curl http://localhost:8080/api/persons
```
Работает **без логина**.

### `@Secured`

1. Инкогнито → `http://localhost:8080/api/method-security/read-only`
2. Войти как `reader` / `reader123`
3. Ожидаемо: `Доступно пользователям с ролью READ`

### `@RolesAllowed`

1. Инкогнито → `http://localhost:8080/api/method-security/write-only`
2. Войти как `reader` → **403**
3. Инкогнито → войти как `writer` → **200**

### `@PreAuthorize` с ролями

1. Инкогнито → `/write-or-delete`
2. `reader` → **403**
3. `writer` или `admin` → **200**

### `@PreAuthorize` с проверкой имени

| URL | Залогинен | Результат |
|-----|-----------|-----------|
| `/self?username=reader` | `reader` | ✅ 200 |
| `/self?username=writer` | `reader` | ❌ 403 |
| `/self?username=writer` | `writer` | ✅ 200 |

---

## 📌 Ключевые классы

- **`SecurityConfig`** — настройка `SecurityFilterChain`, `UserDetailsService`, включение method security.
- **`MethodSecurityController`** — демонстрация всех трёх типов аннотаций.
- **`PersonController`** — REST-контроллер для работы с данными (URL-уровень защиты).

---

## 🎯 Основные моменты проекта

- Как включить method security через `@EnableMethodSecurity`.
- Чем отличаются `@Secured`, `@RolesAllowed` и `@PreAuthorize`.
- Как работают SpEL-выражения в `@PreAuthorize` (`hasRole`, `hasAuthority`, `#param`, `authentication.name`).
- Где и когда Spring Security сам добавляет префикс `ROLE_`, а где — нет.
- Как отлаживать 403 через DEBUG-логи (`logging.level.org.springframework.security=DEBUG`).

---
