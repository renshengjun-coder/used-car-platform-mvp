---
artifact_id: DES-002-api-design
artifact_type: design
package_id: used-car-mvp
version: v1
status: reviewed
owner: ""
created_at: 2026-06-28
traces:
  - derives_from: "artifacts/used-car-mvp/01-requirements/acceptance-criteria.md@v1"
related:
  - artifacts/used-car-mvp/02-design/architecture.md@v1
---

# API Design — used-car-mvp

## Conventions (AC-015)

- Base path: `/api/v1`
- JSON request/response; `Content-Type: application/json`
- Auth: `Authorization: Bearer <JWT>` for seller-only endpoints
- Session: `X-Session-Id` cookie (set by FE) for behavior + recommendations
- Errors: `{ "error": { "code": "STRING", "message": "...", "details": {} } }`, standard HTTP status codes
- Pagination: `?page=0&size=20`; response `{ content, page, size, totalElements, totalPages }`
- OpenAPI served at `/v3/api-docs` and Swagger UI at `/swagger-ui.html` (springdoc)
- CORS: allow FE origin(s) from `app.cors.allowed-origins`

## Auth (AC-001, AC-002)

### POST /api/v1/auth/register
Request: `{ "email": "a@b.com", "password": "min8chars", "displayName": "Dealer A" }`
Response 201: `{ "userId": 1, "email": "a@b.com", "token": "<jwt>" }`
Errors: 409 `EMAIL_TAKEN`, 400 `WEAK_PASSWORD`

### POST /api/v1/auth/login
Request: `{ "email", "password" }`
Response 200: `{ "userId", "email", "token" }`
Errors: 401 `INVALID_CREDENTIALS`

## Listings (AC-003, AC-005, AC-006, AC-009)

### POST /api/v1/listings  (seller auth)
Request:
```json
{
  "title": "2019 Toyota Camry",
  "price": 158000,
  "make": "Toyota", "model": "Camry",
  "year": 2019, "mileage": 42000,
  "fuelType": "GASOLINE", "transmission": "AUTOMATIC",
  "city": "Beijing",
  "description": "One owner...",
  "photoUrls": ["https://.../1.jpg"],
  "status": "PUBLISHED"
}
```
Response 201: full listing with `id`, `sellerId`, `publishedAt`.
Validation: required fields present, ≥ 1 photo if `PUBLISHED`. Errors: 400 `VALIDATION_ERROR`, 401.

### PUT /api/v1/listings/{id}  (seller auth, owner only)
Updates editable fields. Errors: 403 `NOT_OWNER`, 404 `NOT_FOUND`.

### PATCH /api/v1/listings/{id}/status  (seller auth, owner only)
Request: `{ "status": "PUBLISHED" | "UNPUBLISHED" | "DRAFT" }` — triggers ES sync (AC-006).

### DELETE /api/v1/listings/{id}  (seller auth, owner only)
Soft-delete; removes from ES. Response 204.

### GET /api/v1/listings/{id}  (public)
Response 200: full listing if `PUBLISHED`. Errors: 404 for unpublished/missing (AC-009).

### GET /api/v1/seller/listings  (seller auth)
Lists caller's own listings (any status).

## Search (AC-007, AC-008)

### GET /api/v1/listings  (public)
Query params (all optional, ANDed):
`keyword, priceMin, priceMax, yearMin, yearMax, mileageMin, mileageMax, city, fuelType, transmission, make, model, page=0, size=20, sort=publishedAt,desc`

Behavior:
- No params → all published, sorted `publishedAt desc` (AC-007).
- `keyword` → ES `multi_match` over `title, make, model`.
- Ranges → ES `range` filters; enums/city → `term` filters.
Response: paginated listing summaries `{ id, title, price, make, model, year, mileage, city, thumbnailUrl, publishedAt }`.
NFR: p95 < 500ms on ≤ 10k docs (AC-008).

## Behavior (AC-010, AC-011)

### POST /api/v1/behavior/view
Request: `{ "carId": 123 }` (sessionId from cookie, userId from JWT if present)
Response 202. Dedupe identical `(session, car)` within 5 min (AC-010).

### POST /api/v1/behavior/search
Request: `{ "keyword": "...", "filters": { ... } }`
Response 202. Retain latest 20 search events per session (AC-011).

## Recommendations (AC-012, AC-013, AC-014, AC-016)

### GET /api/v1/recommendations?context=LISTING_TOP|DETAIL&excludeCarId=&limit=10
- Uses session/user history server-side.
- `context=DETAIL&excludeCarId=123` → "You may also like" excludes current car.
- `context=LISTING_TOP` → strip at top of listing page.
Response: `{ "items": [ <listing summary> ], "strategy": "PERSONALIZED" | "POPULAR_FALLBACK" }`
- No history → `POPULAR_FALLBACK` (AC-014).
- Sparse history → ≥ 30% popular blended (AC-013).

### GET /api/v1/popular?limit=10  (public)
Top cars by 7-day view count, fallback to newest if < 5 (AC-012).

## Notes
- No email/notification endpoints — recommendations are in-app only (AC-016, out-of-scope confirmed).
- All write endpoints idempotency: listing create is not idempotent; behavior endpoints tolerate retries via dedupe.
