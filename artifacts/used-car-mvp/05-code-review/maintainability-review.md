---
artifact_id: CR-004-maintainability-review
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

# Maintainability Review — used-car-mvp

| ID | Severity | Location | Finding | Action |
|----|----------|----------|---------|--------|
| MNT-001 | non-blocking | backend status/fuel/transmission strings | Status and enum-like values are bare strings (`"PUBLISHED"`, `"GASOLINE"`). | Introduce Java enums for status/fuel/transmission to reduce typo risk. |
| MNT-002 | non-blocking | backend/seed/DataSeeder.java | `reindexAll()` at startup throws if ES unavailable, which could fail boot in non-compose runs. | Wrap seed indexing in try/log, or gate behind a profile. |
| MNT-003 | non-blocking | frontend/src/app/sell/page.tsx | Single client component handles auth + publish form (~190 lines, under 300 limit but dense). | Optionally split AuthForm and ListingForm components. |
| MNT-004 | non-blocking | backend recommendation weights | Magic ratios (0.3 floor, threshold 3) are constants in service. | Already partially configurable; consider moving thresholds to AppProperties. |

## Positives

- Feature-based package layout (`auth`, `listing`, `search`, `behavior`, `recommendation`) matches user architecture rules.
- Clear separation: controllers (thin) → services (logic) → mappers (data); no business logic in UI components (scoring server-side).
- Shared utilities under `common/` and `lib/` per project rules.
- All components under 300 lines; consistent naming; DTOs as records.
- No circular dependencies observed (events decouple listing → ES sync).

**Blocking maintainability findings:** 0
