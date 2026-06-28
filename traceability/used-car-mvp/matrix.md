# Traceability Matrix — used-car-mvp

| Req/AC ID | Design Section | Test Case(s) | Code File(s) | Status | Notes |
|-----------|----------------|--------------|--------------|--------|-------|
| AC-001 | architecture §Auth; api-design §Auth | TC-001, EC-001, EC-002 | auth/AuthService.java, auth/AuthController.java | covered | AuthListingSearchIT.login_succeeds_after_register (register 201) |
| AC-002 | architecture §Auth; api-design §Auth | TC-002, EC-003 | auth/JwtService.java, config/JwtAuthFilter.java, config/SecurityConfig.java | covered | AuthListingSearchIT (login returns JWT) |
| AC-003 | architecture §Publish flow; api-design §Listings POST | TC-003, EC-004 | listing/ListingService.java, listing/ListingController.java | covered | ListingServiceTest (unit) + AuthListingSearchIT.publish_without_photo_is_rejected |
| AC-004 | architecture §ES Sync; data-model ES index | TC-004 | search/es/EsSyncService.java, listing/ListingChangedEvent.java | covered | AuthListingSearchIT.publish_indexes_and_is_searchable… |
| AC-005 | architecture §ES Sync; api-design §Listings PUT | TC-005, EC-005 | listing/ListingService.java (update), search/es/EsSyncService.java | covered | AuthListingSearchIT.edit_updates_search_index |
| AC-006 | architecture §ES Sync; api-design §status/DELETE | TC-006 | listing/ListingService.java (changeStatus/delete) | covered | AuthListingSearchIT (unpublish → search removal + detail 404) |
| AC-007 | architecture §Search flow; api-design §Search | TC-007 | search/SearchService.java, search/SearchController.java, app/page.tsx | covered | AuthListingSearchIT (keyword search) |
| AC-008 | architecture §Search module; api-design §Search | TC-008, TC-008b, EC-007, EC-008 | search/SearchService.java (criteria), components/FilterBar.tsx | covered | Functional filter covered (AuthListingSearchIT); perf TC-008b deferred (GAP-003) |
| AC-009 | architecture §Listing; api-design §GET by id | TC-009, EC-009 | listing/ListingService.java (getPublic), app/cars/[id]/page.tsx | covered | AuthListingSearchIT (detail 200 / 404) |
| AC-010 | data-model view_events; api-design §behavior/view | TC-010, EC-014 | behavior/BehaviorService.java, components/ViewTracker.tsx | covered | BehaviorRecommendationIT.view_dedupes_within_window (DEF-001 fixed) |
| AC-011 | data-model search_events; api-design §behavior/search | TC-011 | behavior/BehaviorService.java (trim), mapper/SearchEventMapper.xml | covered | BehaviorRecommendationIT.search_events_trim_to_latest_20 |
| AC-012 | architecture §Recommendation scoring; api-design §popular | TC-012 | recommendation/PopularService.java | covered | BehaviorRecommendationIT.popular_returns_published_with_fallback |
| AC-013 | architecture §Recommendation scoring | TC-013, EC-015, EC-016 | recommendation/RecommendationService.java | covered | BehaviorRecommendationIT.recommendation_cold_start_then_personalized |
| AC-014 | architecture §Recommendation scoring; failure FS-004 | TC-014 | recommendation/RecommendationService.java (cold start) | covered | RecommendationServiceTest (unit) + BehaviorRecommendationIT |
| AC-015 | architecture §Interfaces; api-design §Conventions | TC-015 | config/OpenApiConfig.java, config/SecurityConfig.java (CORS), lib/api.ts | covered | ApiContractIT (OpenAPI published) |
| AC-016 | architecture FE §Recommendation strip; api-design §Recommendations | TC-016 | components/RecommendationStrip.tsx, app/page.tsx, app/cars/[id]/page.tsx | covered | Playwright recommendation.spec.ts (3/3) |

**Status values:** `pending`, `covered`, `failed`, `N/A`

## Traceability self-check

- Every AC (AC-001–AC-016) has a row with Design Section + Test Case(s) + Code File(s): pass
- Code column populated from 04-implementation/changed-files.md on 2026-06-28
- Status: **16/16 ACs `covered`** by executed, passing tests (3 unit + 9 integration + 3 E2E) on 2026-06-28
- Deferred (non-blocking): TC-008b perf probe (GAP-003); JaCoCo coverage instrumentation (GAP-004); dual-store failure-path tests (GAP-005)
