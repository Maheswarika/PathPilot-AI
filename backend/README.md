# PathPilot AI backend

Spring Boot 3.4.13, Java 21, Maven, Spring Security, JWT, Spring Data JPA, and MySQL-compatible TiDB Cloud. The production configuration requires `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, and `JWT_SECRET` to be supplied through the environment. For local runs from this directory, the app also imports the repository-root `../.env` file as a properties file. Hibernate's existing `ddl-auto=update` setting updates mapped tables after a successful database connection.

For local development, copy `../.env.example` to `../.env`, replace its TiDB placeholders with the connection values from your cluster, and replace `JWT_SECRET` with a random secret of at least 32 bytes. The root `.env` is ignored by Git; do not commit it or share its contents. Spring Boot reads it when Maven is started from this backend directory.

From the backend directory in PowerShell:

```powershell
Copy-Item ..\.env.example ..\.env
# Edit ..\.env and replace the TiDB placeholders before starting.
mvn test
mvn spring-boot:run
```

## Configure TiDB Cloud (Windows CMD)

Use the connection host, database name, username, and password shown for your TiDB Cloud cluster. TiDB Cloud MySQL-compatible connections use port `4000`; keep TLS certificate and hostname verification enabled with `sslMode=VERIFY_IDENTITY`. Replace the angle-bracket placeholders before running these commands. Run them in the same CMD window that you will use to launch Maven:

```cmd
set "SPRING_DATASOURCE_URL=jdbc:mysql://<tidb-host>:4000/<database>?sslMode=VERIFY_IDENTITY"
set "SPRING_DATASOURCE_USERNAME=<tidb-cloud-username>"
set "SPRING_DATASOURCE_PASSWORD=<tidb-cloud-password>"
for /f "delims=" %G in ('powershell -NoProfile -Command "[Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(48))"') do set "JWT_SECRET=%G"
set "CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:5174,http://127.0.0.1:5173,http://127.0.0.1:5174"
```

The JWT command generates a fresh random secret for this terminal session; the application requires at least 32 bytes. Keep it private. Generating a different secret invalidates tokens signed with the previous one. These `set` commands affect only the current CMD window and do not save credentials in the project or permanently in Windows. If you put the `for` command in a `.bat` file, use `%%G` instead of `%G`.

To check required variables without printing credential values:

```cmd
if defined SPRING_DATASOURCE_URL (echo SPRING_DATASOURCE_URL is set) else (echo SPRING_DATASOURCE_URL is MISSING)
if defined SPRING_DATASOURCE_USERNAME (echo SPRING_DATASOURCE_USERNAME is set) else (echo SPRING_DATASOURCE_USERNAME is MISSING)
if defined SPRING_DATASOURCE_PASSWORD (echo SPRING_DATASOURCE_PASSWORD is set) else (echo SPRING_DATASOURCE_PASSWORD is MISSING)
if defined JWT_SECRET (echo JWT_SECRET is set) else (echo JWT_SECRET is MISSING)
```

Do not commit real values or print them in logs. The application has no production JWT-secret fallback. Set `CORS_ALLOWED_ORIGINS` to a comma-separated list of exact trusted frontend origins when they differ from the local development origins above.

The user entity maps to the `users` table (`id`, `name`, unique `email`, BCrypt-encoded `password`, and optional profile fields); roles are stored in the `roles` and `user_roles` tables. The repository uses `findByEmail`, `existsByEmail`, and JPA save/flush methods. Registration normalizes email, checks for duplicates, relies on a database unique constraint as a second line of defense, and only saves BCrypt-encoded passwords. Login delegates password verification to Spring Security's DAO authentication provider and BCrypt encoder.

## Start and test

From this directory, in the same CMD window where the environment variables were set:

```cmd
mvn test
mvn spring-boot:run
```

The backend listens on port `8081` by default. Tests activate the `test` profile and use an isolated in-memory H2 database, so they do not connect to TiDB. They cover persisted registration, duplicate emails, BCrypt password matching, successful and failed logins, token validity, protected endpoint access, and the Vite CORS preflight. They do not verify credentials or existing user records in your TiDB instance.

The auth endpoints are `POST /api/auth/register` with `name`, `email`, `password`, and `confirmPassword`, and `POST /api/auth/login` with `email` and `password`. Both are public. Protected endpoints require `Authorization: Bearer <token>`. Database connection errors return a sanitized HTTP 503 response; SQL errors and secrets are not included in the response.
