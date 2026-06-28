package com.usedcar.it;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class ApiContractIT extends AbstractIntegrationTest { // AC-015

    @Autowired
    TestRestTemplate rest;

    @Test
    void openapi_doc_is_published() {
        ResponseEntity<String> res = rest.getForEntity("/v3/api-docs", String.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).contains("/api/v1/listings");
        assertThat(res.getBody()).contains("/api/v1/recommendations");
    }
}
