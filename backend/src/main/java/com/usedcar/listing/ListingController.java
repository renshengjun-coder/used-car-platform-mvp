package com.usedcar.listing;

import com.usedcar.common.SecurityUtils;
import com.usedcar.listing.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ListingController {

    private final ListingService listingService;

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @PostMapping("/listings")
    public ResponseEntity<ListingResponse> create(@Valid @RequestBody CreateListingRequest req) {
        Long sellerId = SecurityUtils.currentSellerId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ListingResponse.from(listingService.create(sellerId, req)));
    }

    @PutMapping("/listings/{id}")
    public ResponseEntity<ListingResponse> update(@PathVariable Long id,
                                                  @RequestBody UpdateListingRequest req) {
        Long sellerId = SecurityUtils.currentSellerId();
        return ResponseEntity.ok(ListingResponse.from(listingService.update(sellerId, id, req)));
    }

    @PatchMapping("/listings/{id}/status")
    public ResponseEntity<ListingResponse> changeStatus(@PathVariable Long id,
                                                        @Valid @RequestBody UpdateStatusRequest req) {
        Long sellerId = SecurityUtils.currentSellerId();
        return ResponseEntity.ok(ListingResponse.from(listingService.changeStatus(sellerId, id, req.status())));
    }

    @DeleteMapping("/listings/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        listingService.delete(SecurityUtils.currentSellerId(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/listings/{id}")
    public ResponseEntity<ListingResponse> getPublic(@PathVariable Long id) {
        return ResponseEntity.ok(ListingResponse.from(listingService.getPublic(id)));
    }

    @GetMapping("/seller/listings")
    public ResponseEntity<List<ListingResponse>> sellerListings() {
        Long sellerId = SecurityUtils.currentSellerId();
        return ResponseEntity.ok(listingService.listBySeller(sellerId)
                .stream().map(ListingResponse::from).toList());
    }
}
