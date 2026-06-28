package com.usedcar.recommendation.dto;

import com.usedcar.listing.dto.ListingSummary;

import java.util.List;

public record RecommendationResponse(List<ListingSummary> items, String strategy) {
    public static final String PERSONALIZED = "PERSONALIZED";
    public static final String POPULAR_FALLBACK = "POPULAR_FALLBACK";
}
