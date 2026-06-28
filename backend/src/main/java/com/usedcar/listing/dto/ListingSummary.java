package com.usedcar.listing.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ListingSummary(
        Long id,
        String title,
        BigDecimal price,
        String make,
        String model,
        Integer year,
        Integer mileage,
        String city,
        String thumbnailUrl,
        LocalDateTime publishedAt
) {}
