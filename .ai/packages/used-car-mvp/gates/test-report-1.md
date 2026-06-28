# Gate: test-report (attempt 1)

result: pass
profile: standard
mode: loop

artifacts_checked:
  - artifacts/used-car-mvp/06-test-report/test-execution-summary.md@v2
  - artifacts/used-car-mvp/06-test-report/unit-test-report.md@v2
  - artifacts/used-car-mvp/06-test-report/integration-test-report.md@v2
  - artifacts/used-car-mvp/06-test-report/e2e-test-report.md@v2
  - artifacts/used-car-mvp/06-test-report/coverage-report.md@v2
  - artifacts/used-car-mvp/06-test-report/defects.md@v2
  - artifacts/used-car-mvp/06-test-report/release-recommendation.md@v2
  - artifacts/used-car-mvp/06-test-report/review-log.md@v2
  - traceability/used-car-mvp/matrix.md
  - traceability/used-car-mvp/package-evidence-index.md

checklist:
  - [x] L1 self-review complete, no blocking failures
  - [x] Required artifacts exist for profile (7 reports + review-log)
  - [x] Traces to upstream requirements present (AC-001–AC-016 in frontmatter)
  - [x] Human approval recorded (N/A — test-report not in human_gates for standard)
  - [x] Package evidence index and matrix reflect this phase outcome (16/16 ACs covered)
  - [x] Exact evidence bindings recorded in artifacts_checked

findings:
  - severity: non-blocking
    message: TC-008b large-seed perf probe deferred (GAP-003); JaCoCo not configured (GAP-004); dual-store failure-path tests not automated (GAP-005).
    action: Post-MVP follow-ups; do not block MVP release per release-recommendation GO.

reentry: 0
next: release
