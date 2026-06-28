package com.usedcar.listing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record CreateListingRequest(
        @NotBlank String title,
        @NotNull @DecimalMin("0.0") BigDecimal price,
        @NotBlank String make,
        @NotBlank String model,
        @NotNull Integer year,
        @NotNull Integer mileage,
        String fuelType,
        String transmission,
        @NotBlank String city,
        String description,
        List<String> photoUrls,
        String status // DRAFT or PUBLISHED
) {}
