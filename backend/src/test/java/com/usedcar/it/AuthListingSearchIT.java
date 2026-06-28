package com.usedcar.it;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.JdkClientHttpRequestFactory;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

class AuthListingSearchIT extends AbstractIntegrationTest {

    @Autowired
    TestRestTemplate rest;

    @BeforeEach
    void usePatchCapableClient() {
        // Default SimpleClientHttpRequestFactory (HttpURLConnection) rejects PATCH.
        rest.getRestTemplate().setRequestFactory(new JdkClientHttpRequestFactory());
    }

    private String registerSeller(String email) {
        ResponseEntity<Map> res = rest.postForEntity("/api/v1/auth/register",
                Map.of("email", email, "password", "password123", "displayName", "S"), Map.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED); // AC-001
        return (String) res.getBody().get("token");
    }

    private HttpHeaders authHeaders(String token) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.setBearerAuth(token);
        return h;
    }

    @SuppressWarnings("unchecked")
    private Number publish(String token, String make, String model, int price, String city) {
        Map<String, Object> body = new java.util.HashMap<>();
        body.put("title", model + " " + make);
        body.put("price", price);
        body.put("make", make);
        body.put("model", model);
        body.put("year", 2020);
        body.put("mileage", 30000);
        body.put("fuelType", "GASOLINE");
        body.put("transmission", "AUTOMATIC");
        body.put("city", city);
        body.put("description", "demo");
        body.put("photoUrls", List.of("https://img/x.jpg"));
        body.put("status", "PUBLISHED");
        ResponseEntity<Map> res = rest.exchange("/api/v1/listings", HttpMethod.POST,
                new HttpEntity<>(body, authHeaders(token)), Map.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED); // AC-003
        return (Number) res.getBody().get("id");
    }

    @Test
    void login_succeeds_after_register() { // AC-002
        registerSeller("login@demo.dev");
        ResponseEntity<Map> res = rest.postForEntity("/api/v1/auth/login",
                Map.of("email", "login@demo.dev", "password", "password123"), Map.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody().get("token")).isNotNull();
    }

    @Test
    void publish_indexes_and_is_searchable_then_unpublish_removes() {
        String token = registerSeller("flow@demo.dev");
        Number id = publish(token, "Toyota", "Camry", 158000, "Beijing");

        // AC-004 + AC-007 + AC-008: searchable within 5s, keyword + filter
        await().atMost(8, SECONDS).untilAsserted(() -> {
            ResponseEntity<Map> res = rest.getForEntity(
                    "/api/v1/listings?keyword=Camry&priceMin=100000&priceMax=200000&city=Beijing", Map.class);
            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
            List<?> content = (List<?>) res.getBody().get("content");
            assertThat(content).isNotEmpty();
        });

        // AC-009: detail returns 200 for published
        ResponseEntity<Map> detail = rest.getForEntity("/api/v1/listings/" + id, Map.class);
        assertThat(detail.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(detail.getBody().get("make")).isEqualTo("Toyota");

        // AC-006: unpublish removes from search + detail 404
        rest.exchange("/api/v1/listings/" + id + "/status", HttpMethod.PATCH,
                new HttpEntity<>(Map.of("status", "UNPUBLISHED"), authHeaders(token)), Map.class);

        await().atMost(8, SECONDS).untilAsserted(() -> {
            ResponseEntity<Map> res = rest.getForEntity("/api/v1/listings?keyword=Camry&city=Beijing", Map.class);
            List<?> content = (List<?>) res.getBody().get("content");
            assertThat(content).noneSatisfy(item ->
                    assertThat(((Map<?, ?>) item).get("id")).isEqualTo(id));
        });
        ResponseEntity<Map> detail404 = rest.getForEntity("/api/v1/listings/" + id, Map.class);
        assertThat(detail404.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void edit_updates_search_index() { // AC-005
        String token = registerSeller("edit@demo.dev");
        Number id = publish(token, "Honda", "Accord", 175000, "Shanghai");

        rest.exchange("/api/v1/listings/" + id, HttpMethod.PUT,
                new HttpEntity<>(Map.of("price", 169000), authHeaders(token)), Map.class);

        await().atMost(8, SECONDS).untilAsserted(() -> {
            ResponseEntity<Map> res = rest.getForEntity(
                    "/api/v1/listings?keyword=Accord&priceMax=170000", Map.class);
            List<?> content = (List<?>) res.getBody().get("content");
            assertThat(content).isNotEmpty();
        });
    }

    @Test
    void publish_without_photo_is_rejected() { // EC-004 / AC-003
        String token = registerSeller("nophoto@demo.dev");
        Map<String, Object> body = new java.util.HashMap<>();
        body.put("title", "No Photo Car");
        body.put("price", 100000);
        body.put("make", "VW");
        body.put("model", "Lavida");
        body.put("year", 2019);
        body.put("mileage", 50000);
        body.put("city", "Wuhan");
        body.put("photoUrls", List.of());
        body.put("status", "PUBLISHED");
        ResponseEntity<Map> res = rest.exchange("/api/v1/listings", HttpMethod.POST,
                new HttpEntity<>(body, authHeaders(token)), Map.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
