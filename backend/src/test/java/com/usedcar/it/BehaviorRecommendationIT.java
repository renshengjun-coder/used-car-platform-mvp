package com.usedcar.it;

import com.usedcar.mapper.SearchEventMapper;
import com.usedcar.mapper.ViewEventMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

class BehaviorRecommendationIT extends AbstractIntegrationTest {

    @Autowired TestRestTemplate rest;
    @Autowired ViewEventMapper viewEventMapper;
    @Autowired SearchEventMapper searchEventMapper;

    private String register(String email) {
        ResponseEntity<Map> res = rest.postForEntity("/api/v1/auth/register",
                Map.of("email", email, "password", "password123"), Map.class);
        return (String) res.getBody().get("token");
    }

    @SuppressWarnings("unchecked")
    private Number publish(String token, String make, String model) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.setBearerAuth(token);
        Map<String, Object> body = new java.util.HashMap<>();
        body.put("title", make + " " + model);
        body.put("price", 150000);
        body.put("make", make);
        body.put("model", model);
        body.put("year", 2021);
        body.put("mileage", 20000);
        body.put("fuelType", "GASOLINE");
        body.put("transmission", "AUTOMATIC");
        body.put("city", "Beijing");
        body.put("description", "demo");
        body.put("photoUrls", List.of("https://img/x.jpg"));
        body.put("status", "PUBLISHED");
        ResponseEntity<Map> res = rest.exchange("/api/v1/listings", HttpMethod.POST,
                new HttpEntity<>(body, h), Map.class);
        return (Number) res.getBody().get("id");
    }

    private HttpHeaders session(String sid) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.set("X-Session-Id", sid);
        return h;
    }

    @Test
    void view_dedupes_within_window() { // AC-010
        String token = register("view@demo.dev");
        Number id = publish(token, "Toyota", "Corolla");
        String sid = "sess-view-1";

        for (int i = 0; i < 3; i++) {
            rest.exchange("/api/v1/behavior/view", HttpMethod.POST,
                    new HttpEntity<>(Map.of("carId", id), session(sid)), Void.class);
        }
        int count = viewEventMapper.existsRecent(sid, id.longValue(), LocalDateTime.now().minusHours(1));
        assertThat(count).isEqualTo(1);
    }

    @Test
    void search_events_trim_to_latest_20() { // AC-011
        String sid = "sess-search-1";
        for (int i = 0; i < 25; i++) {
            rest.exchange("/api/v1/behavior/search", HttpMethod.POST,
                    new HttpEntity<>(Map.of("keyword", "kw" + i), session(sid)), Void.class);
        }
        assertThat(searchEventMapper.findRecentBySession(sid, 100)).hasSize(20);
    }

    @Test
    void popular_returns_published_with_fallback() { // AC-012
        String token = register("pop@demo.dev");
        publish(token, "BMW", "3Series");
        publish(token, "Audi", "A4L");

        ResponseEntity<List> res = rest.getForEntity("/api/v1/popular?limit=10", List.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotEmpty(); // fallback to newest when <5 viewed
    }

    @Test
    void recommendation_cold_start_then_personalized() { // AC-014 + AC-013
        String token = register("rec@demo.dev");
        Number toyota = publish(token, "Toyota", "RAV4");
        publish(token, "Tesla", "Model3");
        String sid = "sess-rec-1";

        // Cold start: no history -> POPULAR_FALLBACK
        ResponseEntity<Map> cold = rest.exchange("/api/v1/recommendations",
                HttpMethod.GET, new HttpEntity<>(session(sid)), Map.class);
        assertThat(cold.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(cold.getBody().get("strategy")).isEqualTo("POPULAR_FALLBACK");

        // Build history: view a Toyota
        rest.exchange("/api/v1/behavior/view", HttpMethod.POST,
                new HttpEntity<>(Map.of("carId", toyota), session(sid)), Void.class);

        await().atMost(5, SECONDS).untilAsserted(() -> {
            ResponseEntity<Map> personalized = rest.exchange("/api/v1/recommendations",
                    HttpMethod.GET, new HttpEntity<>(session(sid)), Map.class);
            assertThat(personalized.getBody().get("strategy")).isEqualTo("PERSONALIZED");
            // excludes the already-viewed car (AC-013)
            List<Map<String, Object>> items = (List<Map<String, Object>>) personalized.getBody().get("items");
            assertThat(items).noneSatisfy(it ->
                    assertThat(((Number) it.get("id")).longValue()).isEqualTo(toyota.longValue()));
        });
    }
}
