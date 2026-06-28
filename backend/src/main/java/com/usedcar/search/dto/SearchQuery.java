package com.usedcar.search.dto;

import java.math.BigDecimal;

public record SearchQuery(
        String keyword,
        BigDecimal priceMin,
        BigDecimal priceMax,
        Integer yearMin,
        Integer yearMax,
        Integer mileageMin,
        Integer mileageMax,
        String city,
        String fuelType,
        String transmission,
        String make,
        String model,
        int page,
        int size
) {
    public SearchQuery {
        if (size <= 0) size = 20;
        if (page < 0) page = 0;
    }
}
