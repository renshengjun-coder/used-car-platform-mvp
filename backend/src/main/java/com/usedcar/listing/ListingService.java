package com.usedcar.listing;

import com.usedcar.common.ApiException;
import com.usedcar.config.AppProperties;
import com.usedcar.domain.Listing;
import com.usedcar.listing.dto.CreateListingRequest;
import com.usedcar.listing.dto.UpdateListingRequest;
import com.usedcar.mapper.ListingMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ListingService {

    private static final List<String> PUBLIC_STATUSES = List.of("PUBLISHED");

    private final ListingMapper listingMapper;
    private final ApplicationEventPublisher events;
    private final AppProperties props;

    public ListingService(ListingMapper listingMapper, ApplicationEventPublisher events, AppProperties props) {
        this.listingMapper = listingMapper;
        this.events = events;
        this.props = props;
    }

    @Transactional
    public Listing create(Long sellerId, CreateListingRequest req) {
        String status = req.status() == null ? "DRAFT" : req.status().toUpperCase();
        List<String> photos = req.photoUrls() == null ? List.of() : req.photoUrls();
        validatePhotos(photos, status);

        Listing listing = new Listing();
        listing.setSellerId(sellerId);
        listing.setTitle(req.title());
        listing.setPrice(req.price());
        listing.setMake(req.make());
        listing.setModel(req.model());
        listing.setYear(req.year());
        listing.setMileage(req.mileage());
        listing.setFuelType(req.fuelType());
        listing.setTransmission(req.transmission());
        listing.setCity(req.city());
        listing.setDescription(req.description());
        listing.setStatus(status);
        listing.setPublishedAt("PUBLISHED".equals(status) ? LocalDateTime.now() : null);
        listingMapper.insert(listing);

        if (!photos.isEmpty()) {
            listingMapper.insertPhotos(listing.getId(), photos);
        }
        listing.setPhotoUrls(photos);

        events.publishEvent(ListingChangedEvent.upsert(listing.getId()));
        return listing;
    }

    @Transactional
    public Listing update(Long sellerId, Long id, UpdateListingRequest req) {
        Listing existing = requireOwned(sellerId, id);
        existing.setTitle(req.title() != null ? req.title() : existing.getTitle());
        existing.setPrice(req.price() != null ? req.price() : existing.getPrice());
        existing.setMake(req.make() != null ? req.make() : existing.getMake());
        existing.setModel(req.model() != null ? req.model() : existing.getModel());
        existing.setYear(req.year() != null ? req.year() : existing.getYear());
        existing.setMileage(req.mileage() != null ? req.mileage() : existing.getMileage());
        existing.setFuelType(req.fuelType() != null ? req.fuelType() : existing.getFuelType());
        existing.setTransmission(req.transmission() != null ? req.transmission() : existing.getTransmission());
        existing.setCity(req.city() != null ? req.city() : existing.getCity());
        existing.setDescription(req.description() != null ? req.description() : existing.getDescription());
        listingMapper.update(existing);

        if (req.photoUrls() != null) {
            validatePhotos(req.photoUrls(), existing.getStatus());
            listingMapper.deletePhotos(id);
            if (!req.photoUrls().isEmpty()) listingMapper.insertPhotos(id, req.photoUrls());
            existing.setPhotoUrls(req.photoUrls());
        }
        events.publishEvent(ListingChangedEvent.upsert(id));
        return getOwned(sellerId, id);
    }

    @Transactional
    public Listing changeStatus(Long sellerId, Long id, String status) {
        Listing existing = requireOwned(sellerId, id);
        String normalized = status.toUpperCase();
        if ("PUBLISHED".equals(normalized)) {
            List<String> photos = listingMapper.findPhotoUrls(id);
            if (photos.isEmpty()) {
                throw ApiException.badRequest("VALIDATION_ERROR", "At least one photo required to publish");
            }
        }
        listingMapper.updateStatus(id, normalized, "PUBLISHED".equals(normalized));
        if ("PUBLISHED".equals(normalized)) {
            events.publishEvent(ListingChangedEvent.upsert(id));
        } else {
            events.publishEvent(ListingChangedEvent.removed(id));
        }
        return getOwned(sellerId, id);
    }

    @Transactional
    public void delete(Long sellerId, Long id) {
        requireOwned(sellerId, id);
        listingMapper.updateStatus(id, "DELETED", false);
        events.publishEvent(ListingChangedEvent.removed(id));
    }

    public Listing getPublic(Long id) {
        Listing listing = listingMapper.findById(id);
        if (listing == null || !PUBLIC_STATUSES.contains(listing.getStatus())) {
            throw ApiException.notFound("Listing not found");
        }
        listing.setPhotoUrls(listingMapper.findPhotoUrls(id));
        return listing;
    }

    public Listing getOwned(Long sellerId, Long id) {
        Listing listing = requireOwned(sellerId, id);
        listing.setPhotoUrls(listingMapper.findPhotoUrls(id));
        return listing;
    }

    public List<Listing> listBySeller(Long sellerId) {
        List<Listing> listings = listingMapper.findBySeller(sellerId);
        listings.forEach(l -> l.setPhotoUrls(listingMapper.findPhotoUrls(l.getId())));
        return listings;
    }

    private Listing requireOwned(Long sellerId, Long id) {
        Listing listing = listingMapper.findById(id);
        if (listing == null || "DELETED".equals(listing.getStatus())) {
            throw ApiException.notFound("Listing not found");
        }
        if (!listing.getSellerId().equals(sellerId)) {
            throw ApiException.forbidden("NOT_OWNER", "You do not own this listing");
        }
        return listing;
    }

    private void validatePhotos(List<String> photos, String status) {
        if ("PUBLISHED".equals(status) && (photos == null || photos.isEmpty())) {
            throw ApiException.badRequest("VALIDATION_ERROR", "At least one photo required to publish");
        }
        if (photos != null && photos.size() > props.getUpload().getMaxPhotos()) {
            throw ApiException.badRequest("INVALID_PHOTO",
                    "Too many photos (max " + props.getUpload().getMaxPhotos() + ")");
        }
    }
}
