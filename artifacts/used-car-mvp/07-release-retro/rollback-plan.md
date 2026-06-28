---
artifact_id: REL-004-rollback-plan
artifact_type: release
package_id: used-car-mvp
version: v1
status: reviewed
owner: ""
created_at: 2026-06-28
traces:
  - derives_from: "artifacts/used-car-mvp/04-implementation/changed-files.md@v1"
related: []
---

# Rollback Plan — used-car-mvp v0.1.0

This is a greenfield MVP (no prior production release). Rollback means reverting to **no deployed application** or restoring a pre-release snapshot.

## Pre-release snapshot (recommended before first deploy)

```bash
# Tag the release commit once git history exists
git tag -a v0.1.0-mvp -m "used-car-mvp MVP demo"
git push origin v0.1.0-mvp
```

## Rollback: Docker Compose deployment

1. **Stop all services:**
   ```bash
   docker compose down
   ```
2. **Remove application containers and images (optional full revert):**
   ```bash
   docker compose down --rmi local
   docker rm -f usedcar-mysql-it 2>/dev/null   # if standalone IT container was used
   ```
3. **Remove persistent volumes (destroys demo data):**
   ```bash
   docker compose down -v
   ```
   This deletes `mysql_data` and `es_data` volumes defined in `docker-compose.yml`.

4. **Verify rollback:** `curl http://localhost:8080/actuator/health` and `http://localhost:3000` should fail to connect.

## Rollback: local dev processes

If running without Compose:

```bash
# Stop background processes (adjust PIDs as needed)
pkill -f "spring-boot:run" || true
pkill -f "next dev" || true
```

## Database / search index rollback

- **MySQL:** drop and recreate the `usedcar` database, or restore from a mysqldump taken before deploy:
  ```bash
  docker exec usedcar-mysql-it mysqldump -uroot -proot usedcar > backup.sql
  # restore: docker exec -i usedcar-mysql-it mysql -uroot -proot usedcar < backup.sql
  ```
- **Elasticsearch:** delete the listings index:
  ```bash
  curl -XDELETE http://localhost:9200/listings
  ```
  On next backend boot with seeding enabled, demo data is re-inserted.

## Configuration rollback

- Revert `.env` changes (JWT secret, DB credentials, CORS origins) to pre-release values.
- If `UsedCarApplication` UTC pinning (DEF-001 fix) is reverted, dedup/popularity windows break on non-UTC hosts — do not revert unless replacing with an explicit `Instant`/UTC strategy everywhere.

## Verification after rollback

| Check | Expected |
|-------|----------|
| Backend port 8080 | Not listening |
| Frontend port 3000 | Not listening |
| MySQL data | Empty or restored snapshot |
| ES listings index | Absent or restored snapshot |

## Escalation

If partial rollback leaves ES/MySQL out of sync (listings in ES but not MySQL or vice versa), run a full volume wipe (`docker compose down -v`) and redeploy from the tagged commit.
