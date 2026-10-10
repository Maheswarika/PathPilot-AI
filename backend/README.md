# PathPilot AI backend

Spring Boot 3.4, Java 21, MySQL, Spring Security, and JWT. The default JDBC URL targets the existing `pathpilot_ai_db` schema on local MySQL; Hibernate's existing `ddl-auto=update` setting updates mapped tables after a successful connection.

## Configure MySQL

Create the schema only if it does not already exist. This does not remove or reset existing records:

```sql
CREATE DATABASE IF NOT EXISTS pathpilot_ai_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

Set environment variables for a MySQL account that can connect to that schema and a private JWT signing key of at least 32 bytes. The application does not supply an empty database password or a source-code JWT secret fallback.

PowerShell, for the current terminal session:

```powershell
$env:DB_URL = 'jdbc:mysql://localhost:3306/pathpilot_ai_db'
$env:DB_USERNAME = 'your-mysql-user'
$env:DB_PASSWORD = '<your-mysql-password>'
$env:JWT_SECRET = '<a private random value of at least 32 bytes>'
$env:CORS_ALLOWED_ORIGINS = 'http://localhost:5173,http://localhost:5174,http://127.0.0.1:5173,http://127.0.0.1:5174'
```

Do not commit real values or print them in logs. Configure persistent environment variables using your operating system's secret/configuration management rather than adding credentials to this repository. `DB_URL` defaults to `jdbc:mysql://localhost:3306/pathpilot_ai_db`; `DB_USERNAME` defaults to `root`. `DB_PASSWORD` and `JWT_SECRET` must be set explicitly. For non-local frontend hosts, set `CORS_ALLOWED_ORIGINS` to a comma-separated list of exact trusted origins.

The user entity maps to the `users` table (`id`, `name`, unique `email`, BCrypt-encoded `password`, and optional profile fields); roles are stored in the `roles` and `user_roles` tables. The repository uses `findByEmail`, `existsByEmail`, and JPA save/flush methods. Registration normalizes email, checks for duplicates, relies on a database unique constraint as a second line of defense, and only saves BCrypt-encoded passwords. Login delegates password verification to Spring Security's DAO authentication provider and BCrypt encoder.

## Start and test

From this directory:

```powershell
mvn spring-boot:run
```

The backend listens on port `8081` by default. Run tests without connecting to MySQL:

```powershell
mvn test
```

Tests activate the `test` profile and use an isolated in-memory H2 database. They cover persisted registration, duplicate emails, BCrypt password matching, successful and failed logins, token validity, protected endpoint access, and the Vite CORS preflight. They do not verify credentials or existing user records in your MySQL instance.

The auth endpoints are `POST /api/auth/register` with `name`, `email`, `password`, and `confirmPassword`, and `POST /api/auth/login` with `email` and `password`. Both are public. Protected endpoints require `Authorization: Bearer <token>`. Database connection errors return a sanitized HTTP 503 response; SQL errors and secrets are not included in the response.
