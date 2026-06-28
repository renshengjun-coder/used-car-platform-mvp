package com.usedcar.listing.dto;

import java.math.BigDecimal;
import java.util.List;

public record UpdateListingRequest(
        String title,
        BigDecimal price,
        String make,
        String model,
        Integer year,
        Integer mileage,
        String fuelType,
        String transmission,
        String city,
        String description,
        List<String> photoUrls
) {}
