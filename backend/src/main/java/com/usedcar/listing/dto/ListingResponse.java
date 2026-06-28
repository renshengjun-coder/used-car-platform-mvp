package com.usedcar.listing.dto;

import com.usedcar.domain.Listing;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ListingResponse(
        Long id,
        Long sellerId,
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
        String status,
        List<String> photoUrls,
        LocalDateTime publishedAt,
        LocalDateTime createdAt
) {
    public static ListingResponse from(Listing l) {
        return new ListingResponse(l.getId(), l.getSellerId(), l.getTitle(), l.getPrice(),
                l.getMake(), l.getModel(), l.getYear(), l.getMileage(), l.getFuelType(),
                l.getTransmission(), l.getCity(), l.getDescription(), l.getStatus(),
                l.getPhotoUrls(), l.getPublishedAt(), l.getCreatedAt());
    }
}
