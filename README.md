# Zelix Backend

Spring Boot 4.1.x · Java 21 · PostgreSQL · Redis · JWT

## Structure

Package-by-feature, not package-by-layer. Each module under `com.zelix` is
self-contained:

```
com.zelix.<module>/
  controller/   - REST endpoints
  service/      - business logic
  repository/   - Spring Data JPA interfaces
  entity/       - JPA entities (tables)
  dto/          - request/response objects (never expose entities directly over the API)
```

Modules: `auth`, `users`, `stores`, `products`, `search`, `followers`,
`messaging`, `orders`, `notifications`, `analytics`, `admin`, `reports`.

`common/` holds cross-cutting code shared by every module:
- `config/` - Spring config classes (security config, CORS, Redis config, etc.)
- `exception/` - global exception handler + custom exceptions
- `security/` - JWT filter, token provider, `@AuthenticationPrincipal` helpers
- `util/` - small shared helpers (e.g. Haversine distance calculator)

This is a **monolith**, organized so a future split into services (if ever
needed) is easier — not a Spring Modulith setup. There's no enforced boundary
between modules; that's an intentional tradeoff for build speed right now.

## First-time setup

1. **Start Postgres locally** (Docker is easiest):
   ```
   docker run --name zelix-db -e POSTGRES_PASSWORD=postgres \
     -e POSTGRES_DB=zelix_dev -p 5432:5432 -d postgres
   ```

2. **Start Redis locally**:
   ```
   docker run --name zelix-redis -p 6379:6379 -d redis
   ```

3. **Copy `.env.example` to `.env`** and fill in real local values
   (`.env` is gitignored — never commit it).

4. **Run the app** (defaults to the `dev` profile):
   ```
   ./mvnw spring-boot:run
   ```

5. Confirm it boots cleanly before writing your first entity.

## Profiles

- `dev` — local Postgres, verbose logging, Hibernate auto-updates schema
- `test` — disposable test DB, schema recreated every run
- `prod` — everything from environment variables, schema changes via
  migration only (`ddl-auto=validate`, never `update`)

Activate a profile with `--spring.profiles.active=prod` or the
`SPRING_PROFILES_ACTIVE` env var. Never hardcode which profile is active
for prod inside a committed file.

## Build order (recommended)

auth → users → stores → products → search/proximity → followers →
messaging → orders → notifications → analytics

Each step builds on patterns from the last. Don't jump around.
