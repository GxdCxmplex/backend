# Player Backend

A REST API for player accounts, built with Spring Boot. Players can register, log in to get a JWT, and manage their own profile. It's the foundation for a game backend (inventory, economy, matchmaking and leaderboards are planned).

## Tech stack

- Java, Maven
- Spring Boot 4 (Web, Validation, Data JPA, Security)
- PostgreSQL
- JWT authentication ([jjwt](https://github.com/jwtk/jjwt) 0.12.6), BCrypt password hashing
- JUnit 5, Mockito, MockMvc

## Features

- Registration with validated input and BCrypt-hashed passwords
- Login that returns a short-lived (15 minute) JWT
- Stateless authentication through a custom JWT filter
- Players can only read and delete **their own** account (`/players/me`), so one player can't touch another's data
- Consistent JSON error responses through a global exception handler (400, 401, 404, 409)
- Duplicate usernames are rejected with a friendly check **and** a database unique constraint, so simultaneous registrations can't slip through

## API

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/players/register` | No | Create an account |
| POST | `/players/login` | No | Log in, returns a JWT |
| GET | `/players/me` | Bearer token | Your own profile |
| DELETE | `/players/me` | Bearer token | Delete your own account |
| GET | `/players` | Bearer token | List players |

### Examples

Register:

```bash
curl -X POST http://localhost:8080/players/register \
  -H "Content-Type: application/json" \
  -d '{"username": "art", "password": "a-strong-password"}'
```

```json
{ "id": 1, "username": "art", "level": 1 }
```

Log in:

```bash
curl -X POST http://localhost:8080/players/login \
  -H "Content-Type: application/json" \
  -d '{"username": "art", "password": "a-strong-password"}'
```

```json
{ "token": "<jwt>" }
```

Get your profile:

```bash
curl http://localhost:8080/players/me -H "Authorization: Bearer <jwt>"
```

### Error responses

| Status | When |
|--------|------|
| 400 | Request body fails validation |
| 401 | Missing, malformed, invalid or expired token; wrong username or password |
| 404 | Player not found |
| 409 | Username already taken |

## Running locally

### Prerequisites

- JDK (version in `pom.xml`)
- PostgreSQL running locally with an empty database named `backend`

### Configuration

Secrets are read from environment variables and are never committed.

| Variable | Required | Description |
|----------|----------|-------------|
| `DB_PASSWORD` | Yes | PostgreSQL password |
| `DB_USERNAME` | No | PostgreSQL user (default: `postgres`) |
| `JWT_SECRET` | Yes | Base64-encoded signing key, at least 32 bytes |

Generate a `JWT_SECRET`:

```bash
# macOS / Linux / Git Bash
openssl rand -base64 48
```

```powershell
# Windows PowerShell
$b = New-Object byte[] 48; [Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($b); [Convert]::ToBase64String($b)
```

### Start the app

```bash
export DB_PASSWORD=your-password
export JWT_SECRET=your-generated-secret
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
$env:DB_PASSWORD="your-password"
$env:JWT_SECRET="your-generated-secret"
.\mvnw.cmd spring-boot:run
```

The API runs on `http://localhost:8080`.

### Run the tests

```bash
./mvnw test
```

## Design decisions

- **Stateless JWT instead of sessions.** The server keeps no session state, so the app scales horizontally. CSRF protection is disabled for that reason, and the session policy is explicitly `STATELESS`.
- **The JWT filter never rejects requests itself.** It authenticates when it can and otherwise continues the chain. Spring Security's entry point returns the 401 on protected routes only, so a stale token can't block `/login` or `/register`.
- **Players act on `/me`, not `/{id}`.** The identity comes from the token, which removes a whole class of broken-access-control bugs. Player progression (level) isn't writable by clients.
- **The database is the source of truth for uniqueness.** The service checks first for a friendly error, then catches the unique-constraint violation for the concurrent case.
- **The same error for wrong username and wrong password,** so login can't be used to find out which usernames exist.
- **Entities are never returned directly.** Responses use DTOs, so the password hash can't leak.

## Roadmap

- [ ] Integration tests with Testcontainers (auth edge cases: missing, malformed, wrong-key and expired tokens)
- [ ] Flyway migrations (switch `ddl-auto` to `validate`)
- [ ] Docker Compose (app + PostgreSQL)
- [ ] GitHub Actions CI (build and test on every push)
- [ ] Inventory and purchase/trade endpoints with transactional guarantees
- [ ] Redis caching, then Kafka for async events
- [ ] Live deployment
