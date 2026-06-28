# Used Car Platform MVP

A Guazi-style used-car marketplace demo built with a front-end/back-end separated architecture.

## Features (MVP scope)

1. **Seller publishing** — sellers register/login and publish used-car listings with photos.
2. **Browse & search** — buyers browse a paginated list and filter by keyword, price, year, mileage, city, fuel, and transmission (Elasticsearch-backed).
3. **Recommendations** — in-app suggestions based on view/search history and platform popularity, shown at the **top of the listing page** and within the **car detail page**.

## Tech stack

| Layer | Technology |
|-------|------------|
| Frontend | TypeScript, Next.js 15 (App Router), Tailwind CSS |
| Backend | Java 21, Spring Boot 3.3 (REST) |
| Persistence | MySQL 8 + MyBatis (system of record) |
| Search / recommendations | Elasticsearch 8 (derived store) |

MySQL is the source of truth; listing changes sync to Elasticsearch after commit.

## Project layout

```
backend/    Spring Boot API (Maven)
frontend/   Next.js app
docker-compose.yml  MySQL + Elasticsearch + backend + frontend
```

## Run with Docker (full stack)

```bash
docker compose up --build
```

- Frontend: http://localhost:3000
- Backend API + Swagger UI: http://localhost:8080/swagger-ui.html
- Demo seller: `demo@usedcar.dev` / `demo1234` (seeded with sample listings)

## Run locally (dev)

Start datastores:

```bash
docker compose up mysql elasticsearch
```

Backend:

```bash
cd backend
mvn spring-boot:run
```

Frontend:

```bash
cd frontend
cp .env.example .env.local
npm install
npm run dev
```

## Tests

```bash
# backend unit + integration (requires MySQL + Elasticsearch)
docker compose up -d mysql elasticsearch
cd backend && mvn verify

# frontend type check
cd frontend && npm run typecheck

# Playwright E2E (requires full stack: datastores + backend + frontend)
docker compose up -d mysql elasticsearch
cd backend && mvn -DskipTests package && java -jar target/used-car-backend-0.1.0.jar &
cd frontend && npm ci && NEXT_PUBLIC_API_BASE=http://localhost:8080 npm run build && npm run start &
cd frontend && npx playwright install chromium && npm run e2e
```

### CI

GitHub Actions runs on every push/PR to `main`/`master`:

- **backend-verify** — `mvn verify` against MySQL 8.4 + Elasticsearch 8.13.4 service containers
- **e2e** — builds and starts the backend JAR + Next.js production server, then runs Playwright (`e2e/recommendation.spec.ts`)

See [`.github/workflows/ci.yml`](.github/workflows/ci.yml).

## API overview

All endpoints are under `/api/v1`. See Swagger UI for the full contract.

| Area | Endpoint |
|------|----------|
| Auth | `POST /auth/register`, `POST /auth/login` |
| Listings (seller) | `POST /listings`, `PUT /listings/{id}`, `PATCH /listings/{id}/status`, `DELETE /listings/{id}` |
| Browse/detail | `GET /listings`, `GET /listings/{id}` |
| Behavior | `POST /behavior/view`, `POST /behavior/search` |
| Recommendations | `GET /recommendations`, `GET /popular` |

## Notes

- Recommendations are **in-app only** (no email/outbound).
- Recommendation scoring is rule-based and configurable in `application.yml` (`app.recommendation.*`).
- This is a demo MVP; see `artifacts/used-car-mvp/01-requirements/out-of-scope.md` for explicit exclusions.
