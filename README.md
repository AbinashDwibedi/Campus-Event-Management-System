# 🎓 Campus Event Management System

A secure, RESTful backend application built with **Spring Boot 3** for managing campus clubs, events, and student registrations. The system uses **JWT-based stateless authentication** via Spring Security, with role-based access control for Students, Club Leaders, and Admins.

---

## 🚀 Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Core language |
| Spring Boot | 3.5.14 | Application framework |
| Spring Security | 6.x | Authentication & authorization |
| Spring Data JPA | — | ORM & database access |
| PostgreSQL | — | Relational database |
| JJWT (jjwt-api) | 0.12.5 | JWT token generation & validation |
| ModelMapper | 3.2.4 | DTO ↔ Entity mapping |
| Lombok | — | Boilerplate reduction |
| Maven | — | Build tool |

---

## ✨ Features

- 🔐 **JWT Authentication** — Stateless login with JWT stored in an `HttpOnly` cookie
- 👥 **Role-Based Access Control** — Three roles: `STUDENT`, `CLUB_LEADER`, `ADMIN`
- 🏛️ **Club Management** — Clubs with unique codes, categories (Technical / Cultural / Sports), and membership tracking
- 📅 **Event Management** — Events with capacity limits, status lifecycle (UPCOMING → ONGOING → COMPLETED / CANCELLED), and indexed queries
- 📋 **Event Registration** — Students can register for events with duplicate-prevention constraints
- ✅ **Input Validation** — Strong validation on all DTOs (username format, password strength, etc.)
- 🛡️ **Global Exception Handling** — Centralized error responses via `@ControllerAdvice`
- 🌐 **CORS Support** — Configurable CORS for frontend integration

---

## 🗂️ Project Structure

```
src/main/java/com/abinash/campus_management/
│
├── CampusManagementSystem.java          # Main Spring Boot entry point
│
├── config/
│   └── BasicCofig.java                 # ModelMapper bean configuration
│
├── controller/
│   └── UserAuth.java                   # /auth/register, /auth/login endpoints
│
├── dto/
│   ├── MyUserDto.java                  # User registration DTO (with validations)
│   ├── MyUserLoginDto.java             # Login DTO
│   └── SuccessResponse.java            # Generic success response wrapper
│
├── entity/
│   ├── MyUser.java                     # User account (maps to `users` table)
│   ├── Students.java                   # Student profile (maps to `students` table)
│   ├── Clubs.java                      # Campus club (maps to `clubs` table)
│   ├── Events.java                     # Club event (maps to `events` table)
│   ├── ClubMemberships.java            # Club membership link table
│   └── EventRegistrations.java         # Event registration link table
│
├── enums/
│   ├── Authorities.java                # ROLE_STUDENT | ROLE_CLUB_LEADER | ROLE_ADMIN
│   ├── Category.java                   # TECHNICAL | CULTURAL | SPORTS
│   └── Status.java                     # UPCOMING | ONGOING | COMPLETED | CANCELLED
│
├── exception/
│   ├── ApiException.java               # Custom application exception
│   ├── ErrorResponse.java              # Structured error response body
│   └── GlobalExceptionHandler.java     # @ControllerAdvice for all exceptions
│
├── repository/
│   └── MyUserRepository.java           # JPA repository for users
│
├── security/
│   ├── JwtAuthFilter.java              # OncePerRequestFilter — JWT cookie extraction
│   └── SecurityConfig.java            # Security chain, CORS, auth provider config
│
└── services/
    ├── JwtService.java                 # JWT generation, validation, claim extraction
    ├── MyUserDetailsService.java       # UserDetailsService implementation
    └── MyUserService.java              # User registration business logic
```

---

## 🗄️ Database Schema

### `users`
| Column | Type | Constraints |
|---|---|---|
| id | BIGINT | PK, auto-generated |
| name | VARCHAR(30) | NOT NULL, UNIQUE |
| password | VARCHAR(255) | NOT NULL |
| authorities | VARCHAR(255) | NOT NULL, CHECK (ROLE_STUDENT / ROLE_CLUB_LEADER / ROLE_ADMIN) |
| created_at | TIMESTAMP | NOT NULL, auto-set |

### `students`
| Column | Type | Constraints |
|---|---|---|
| id | BIGINT | PK |
| user_id | BIGINT | FK → users, UNIQUE |
| roll_number | VARCHAR(255) | UNIQUE, NOT NULL |
| name | VARCHAR(255) | NOT NULL |
| email | VARCHAR(255) | UNIQUE, NOT NULL |
| department | VARCHAR(255) | NOT NULL |
| joining_year | INTEGER | NOT NULL |

### `clubs`
| Column | Type | Constraints |
|---|---|---|
| id | BIGINT | PK |
| club_code | VARCHAR(6) | UNIQUE, NOT NULL |
| name | VARCHAR(255) | UNIQUE, NOT NULL |
| category | VARCHAR(255) | CHECK (TECHNICAL / CULTURAL / SPORTS) |
| contact_email | VARCHAR(255) | UNIQUE, NOT NULL |
| is_active | BOOLEAN | NOT NULL, default true |

### `events`
| Column | Type | Constraints |
|---|---|---|
| id | BIGINT | PK |
| title | VARCHAR(255) | NOT NULL |
| description | TEXT | NOT NULL |
| start_time | TIMESTAMP | NOT NULL |
| venue | VARCHAR(255) | NOT NULL |
| max_capacity | INTEGER | NOT NULL |
| status | VARCHAR(255) | CHECK (UPCOMING / ONGOING / COMPLETED / CANCELLED) |
| club_id | BIGINT | FK → clubs |

### `club_memberships`
| Column | Type | Constraints |
|---|---|---|
| id | BIGINT | PK |
| club_id | BIGINT | FK → clubs |
| user_id | BIGINT | FK → users |
| designation | VARCHAR(255) | NOT NULL |
| has_edit_access | BOOLEAN | NOT NULL |
| joined_at | TIMESTAMP | NOT NULL, auto-set |
| — | — | UNIQUE(club_id, user_id) |

### `event_registrations`
| Column | Type | Constraints |
|---|---|---|
| id | BIGINT | PK |
| event_id | BIGINT | FK → events |
| student_id | BIGINT | FK → students |
| registered_at | TIMESTAMP | NOT NULL, auto-set |
| — | — | UNIQUE(event_id, student_id) |

---

## 🔐 Authentication Flow

```
Client                         Server
  │                              │
  │── POST /api/auth/register ──▶│  Validate DTO → BCrypt hash password → Save user
  │◀── 200 OK (UserDto) ─────────│
  │                              │
  │── POST /api/auth/login ─────▶│  AuthenticationManager authenticates credentials
  │                              │  JwtService generates signed JWT token
  │◀── 200 OK + Set-Cookie(jwt) ─│  Token stored in HttpOnly cookie
  │                              │
  │── GET /api/... (+ cookie) ──▶│  JwtAuthFilter reads JWT from cookie
  │                              │  Validates token → sets SecurityContext
  │◀── 200 OK ───────────────────│  Request proceeds to controller
```

All endpoints under `/api/auth/**` are **publicly accessible**. Everything else requires a valid JWT.

---

## 🛡️ Roles & Permissions

| Role | Description |
|---|---|
| `ROLE_STUDENT` | Default role on registration. Can browse and register for events. |
| `ROLE_CLUB_LEADER` | Can manage club events (create, update status). |
| `ROLE_ADMIN` | Full system access — manage clubs, users, and memberships. |

---

## ⚙️ Configuration

Edit `src/main/resources/application.properties`:

```properties
spring.application.name=campus-management-system

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/test
spring.datasource.username=postgres
spring.datasource.password=your_password

# JPA
spring.jpa.hibernate.ddl-auto=create   # Use 'update' or 'validate' in production
spring.jpa.show-sql=true
spring.jpa.open-in-view=false

# Server
server.servlet.context-path=/api
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# JWT
secret.key=YourSuperSecretKeyForJWTGenerationMustBeVeryLongAndSecure!
secret.expiry=1000 * 60 * 60
```

> ⚠️ **Important:** Change `spring.jpa.hibernate.ddl-auto=create` to `update` or `validate` after first run to prevent data loss on restart.

---

## 🏃 Running the Application

### Prerequisites
- Java 21+
- PostgreSQL running locally
- Maven 3.8+

### Steps

```bash
# 1. Clone the repository
git clone https://github.com/AbinashDwibedi/Campus-Event-Management-System.git
cd Campus-Event-Management-System

# 2. Create the PostgreSQL database
psql -U postgres -c "CREATE DATABASE test;"

# 3. Update credentials in application.properties

# 4. Run the application
./mvnw spring-boot:run
```

The server starts at: **`http://localhost:8080/api`**

---

## 📡 API Endpoints

### Auth (Public)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Register a new user |
| `POST` | `/api/auth/login` | Login and receive JWT cookie |

### Register — Request Body
```json
{
  "name": "abinash123",
  "password": "Secure@123"
}
```
> **Validation rules:** Username 3–20 chars, no spaces. Password must contain uppercase, lowercase, digit, and special character.

### Login — Request Body
```json
{
  "name": "abinash123",
  "password": "Secure@123"
}
```
> On success, a `jwt` cookie is set automatically by the server.

---

## 🧪 Running Tests

```bash
./mvnw test
```

The test class `CampusManagementSystemTests` verifies the Spring application context loads correctly.

---

## 🔮 Future Enhancements

- [ ] Club management endpoints (create, update, deactivate clubs)
- [ ] Event CRUD endpoints with capacity enforcement
- [ ] Student event registration endpoints
- [ ] Admin dashboard endpoints
- [ ] Refresh token support
- [ ] Email notifications on event registration
- [ ] Pagination & filtering for events

---

## 👨‍💻 Author

**Abinash Dwibedi**  
GitHub: [@AbinashDwibedi](https://github.com/AbinashDwibedi)
