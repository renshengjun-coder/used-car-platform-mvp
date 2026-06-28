package com.usedcar.it;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Integration test base. Runs the real Spring Boot app (random port) against live
 * MySQL + Elasticsearch reachable on localhost — provisioned via `docker compose up -d
 * mysql elasticsearch`. Endpoints are overridable via env so the same tests run in CI.
 *
 * We intentionally do NOT use Testcontainers here: the bundled docker-java client is
 * incompatible with the local Docker Engine 29 socket (HTTP 400 on /info), while the
 * Docker CLI works. Using CLI-started services keeps these tests runnable on this host.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        String mysqlHost = env("MYSQL_HOST", "localhost");
        String mysqlPort = env("MYSQL_PORT", "3306");
        String mysqlDb = env("MYSQL_DB", "usedcar");
        String mysqlUser = env("MYSQL_USER", "usedcar");
        String mysqlPassword = env("MYSQL_PASSWORD", "usedcar");
        String esUris = env("ES_URIS", "http://localhost:9200");

        registry.add("spring.datasource.url", () ->
                "jdbc:mysql://" + mysqlHost + ":" + mysqlPort + "/" + mysqlDb
                        + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        registry.add("spring.datasource.username", () -> mysqlUser);
        registry.add("spring.datasource.password", () -> mysqlPassword);
        registry.add("spring.elasticsearch.uris", () -> esUris);
        // Each test seeds its own data deterministically.
        registry.add("app.seed.enabled", () -> "false");
    }

    private static String env(String key, String def) {
        String v = System.getenv(key);
        return (v == null || v.isBlank()) ? def : v;
    }
}
