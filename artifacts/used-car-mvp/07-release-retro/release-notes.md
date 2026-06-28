---
artifact_id: REL-001-release-notes
artifact_type: release
package_id: used-car-mvp
version: v1
status: reviewed
owner: ""
created_at: 2026-06-28
traces:
  - derives_from: "artifacts/used-car-mvp/06-test-report/release-recommendation.md@v2"
  - derives_from: "artifacts/used-car-mvp/01-requirements/user-stories.md@v1"
related:
  - artifacts/used-car-mvp/01-requirements/acceptance-criteria.md@v1
---

# Release Notes — used-car-mvp v0.1.0 (MVP Demo)

**Release date:** 2026-06-28  
**Validation:** GO (MVP) — 16/16 ACs pass (3 unit + 9 integration + 3 E2E)

## What's new

### Seller flows (US-001, US-002, US-003)
- **Register and log in** with email and password (JWT-based auth).
- **Publish used-car listings** with vehicle details, at least one photo, and city/price/year/mileage.
- **Edit or unpublish** your own listings; unpublished cars disappear from search and detail pages.

### Buyer discovery (US-004, US-005, US-006)
- **Browse a paginated car list** on the home page.
- **Search and filter** by keyword, price range, year, mileage, city, fuel type, and transmission (Elasticsearch-backed).
- **View car detail pages** with photos and full specifications.

### Recommendations & behavior (US-007, US-008, US-009)
- **Behavior tracking:** search events and car detail views recorded per session (deduped within a 5-minute window; search history trimmed to latest 20).
- **Popular cars** endpoint with fallback to newest listings when view data is sparse.
- **Personalized recommendations** blending view/search history with popular listings; cold-start users see popular cars.
- **In-app recommendation placement:** a strip at the **top of the listing page** and a "You may also like" section on the **car detail page** — no email or outbound notifications.

## Tech stack shipped

| Layer | Stack |
|-------|-------|
| Frontend | Next.js 15.5.19, React 19, TypeScript, Tailwind CSS |
| Backend | Java 21, Spring Boot 3.3.5, MyBatis, JWT |
| Data | MySQL 8 (system of record), Elasticsearch 8 (search + recommendations) |
| Infra | Docker Compose (MySQL, ES, backend, frontend) |

## Configuration & setup

1. Copy `.env.example` to `.env` and adjust secrets (`JWT_SECRET`, DB credentials).
2. Start services: `docker compose up --build` (or run datastores + `mvn spring-boot:run` + `npm run dev` locally).
3. Backend API: `http://localhost:8080` · Swagger UI: `/swagger-ui.html`
4. Frontend: `http://localhost:3000`
5. Demo data is seeded on first backend boot (`app.seed.enabled=true` by default).

## Bug fix included in this release

- **DEF-001 (timezone):** view-dedup and time-windowed popularity now work correctly on non-UTC hosts by pinning the JVM to UTC (`UsedCarApplication`).

## Out of scope (MVP)

- Payment, messaging, dealer CRM, multi-region, mobile apps, admin moderation UI.
