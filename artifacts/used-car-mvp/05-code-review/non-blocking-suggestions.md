---
artifact_id: CR-007-non-blocking-suggestions
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

# Non-Blocking Suggestions — used-car-mvp

| ID | Lens | Summary |
|----|------|---------|
| SEC-001 | security | Set cookie `SameSite=None; Secure` for cross-domain production with credentialed fetch |
| SEC-002 | security | Avoid returning raw exception message in generic 500 handler in prod |
| SEC-003 | security | Enforce env-provided JWT secret in prod; fail fast on demo default |
| SEC-004 | security | Add host allowlist + content validation for photos when binary upload is added |
| PERF-001 | performance | Replace leading-wildcard `contains` keyword match with analyzed `match`/`multi_match` for scale |
| PERF-002 | performance | Batch photo fetches to avoid N+1 in seller list / popular set assembly |
| PERF-003 | performance | Reuse computed popularity map instead of re-calling getPopular for sparse floor |
| PERF-004 | performance | Move recommendation scoring into ES function_score if catalog grows |
| MNT-001 | maintainability | Introduce enums for status/fuel/transmission |
| MNT-002 | maintainability | Make DataSeeder ES indexing resilient / profile-gated |
| MNT-003 | maintainability | Split sell page into AuthForm + ListingForm |
| MNT-004 | maintainability | Move recommendation thresholds to AppProperties |
| TST-001..005 | testability | Add Testcontainers IT (search, ES sync, behavior, recommendation) + Playwright E2E for AC-016 |

These are optional improvements; none block advancement. TST items are scheduled for execution in the test-report phase.
