package com.usedcar.listing;

public record ListingChangedEvent(Long listingId, boolean removed) {
    public static ListingChangedEvent upsert(Long id) { return new ListingChangedEvent(id, false); }
    public static ListingChangedEvent removed(Long id) { return new ListingChangedEvent(id, true); }
}
