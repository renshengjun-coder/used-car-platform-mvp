---
artifact_id: TP-001-strategy
artifact_type: test-plan
package_id: used-car-mvp
version: v1
status: reviewed
owner: ""
created_at: 2026-06-28
traces:
  - derives_from: "artifacts/used-car-mvp/02-design/architecture.md@v1"
related:
  - artifacts/used-car-mvp/03-test-plan/test-cases.md@v1
---

# Test Strategy — used-car-mvp

## Levels

| Level | Scope | Tools |
|-------|-------|-------|
| Unit (BE) | Services, scoring logic, validators | JUnit 5, Mockito |
| Unit (FE) | Components, api client, hooks | Vitest, React Testing Library |
| Integration (BE) | Controllers + MyBatis + ES against test containers | Spring Boot Test, Testcontainers (MySQL + Elasticsearch) |
| Contract | OpenAPI schema validation | springdoc + schema assertions |
| E2E | Browser flows: publish, search, recommend | Playwright |
| Performance (smoke) | Search p95 < 500ms on seeded 10k docs | k6 or JMeter (demo-scale) |

## Environments

- **Local/dev:** docker-compose (MySQL 8, Elasticsearch 8, BE, FE).
- **CI:** Testcontainers for BE integration; Playwright against compose stack.
- Seed dataset: scripted ≤ 10k listings + synthetic view/search events.

## Entry / exit criteria

- Entry: implementation phase produces buildable FE + BE; compose stack boots.
- Exit: all AC-mapped TCs pass; no blocking defects; search perf smoke meets AC-008.

## Coverage targets (demo)

- Every AC (AC-001–AC-016) has ≥ 1 automated test case.
- BE service-layer line coverage ≥ 70% for listing/search/recommendation modules.
- Critical E2E happy paths (publish, search+filter, recommendation display) automated.

## Risk-based focus

- Highest: ES↔MySQL sync (R-001), search perf (R-007), recommendation correctness (R-002).
- Security: seller auth + ownership (AC-002, FS-008/009), photo validation (R-006).
