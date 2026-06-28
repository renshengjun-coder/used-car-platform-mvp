---
artifact_id: CR-002-security-review
artifact_type: code-review
package_id: used-car-mvp
version: v1
status: reviewed
owner: ""
created_at: 2026-06-28
traces:
  - reviews: "artifacts/used-car-mvp/04-implementation/changed-files.md@v1"
related: []
---

# Security Review — used-car-mvp

Files inspected: SecurityConfig, JwtAuthFilter, JwtService, AuthService, GlobalExceptionHandler, SessionResolver, ListingService (ownership), application.yml, FE fetch components.

| ID | Severity | Location | Finding | Action |
|----|----------|----------|---------|--------|
| SEC-001 | non-blocking | frontend/src/components/RecommendationStrip.tsx, ViewTracker.tsx; backend SessionResolver | `sid` cookie has no explicit `SameSite`. Works for same-site localhost demo; cross-domain prod with `credentials: include` requires `SameSite=None; Secure`. | Set SameSite/Secure per environment before non-local deploy. |
| SEC-002 | non-blocking | backend/common/GlobalExceptionHandler.java:handleGeneric | Generic 500 returns `ex.getMessage()`, may leak internal detail. | Return opaque message in prod; log detail server-side. |
| SEC-003 | non-blocking | backend/src/main/resources/application.yml | Demo `JWT_SECRET` default committed (overridable by env). Not a real secret. | Require env-provided secret in prod; fail fast if default used. |
| SEC-004 | non-blocking | backend/listing/ListingService validatePhotos | Photo URL accepted without content-type/size validation (URL-based, IA-005). | Validate/allowlist hosts and add server-side size/type checks when binary upload is added. |

## Positive controls verified

- Passwords hashed with BCrypt (`SecurityConfig.passwordEncoder`, `AuthService`).
- Stateless JWT; seller-only endpoints require auth (`SecurityConfig` matchers for POST/PUT/PATCH/DELETE listings + `/seller/**`).
- Ownership enforced server-side: `ListingService.requireOwned` → 403 `NOT_OWNER` (FS-008).
- Invalid/expired JWT clears context → 401 on protected routes (FS-009).
- Bean validation on inputs (`@Valid`, `@Size`, `@Email`, `@NotBlank/@NotNull`).
- No SQL injection: MyBatis parameterized `#{}` bindings throughout mapper XML (no `${}` string interpolation).
- No secrets in source beyond overridable demo default.
- No PII in behavior events (session id + car id only).

**Blocking security findings:** 0
