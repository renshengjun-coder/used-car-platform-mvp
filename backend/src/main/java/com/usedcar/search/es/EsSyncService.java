package com.usedcar.search.es;

import com.usedcar.domain.Listing;
import com.usedcar.listing.ListingChangedEvent;
import com.usedcar.mapper.ListingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Syncs MySQL listing changes into Elasticsearch AFTER the DB transaction commits,
 * keeping ES as a derived store (design tradeoff D2). Only PUBLISHED listings are indexed.
 */
@Service
public class EsSyncService {

    private static final Logger log = LoggerFactory.getLogger(EsSyncService.class);

    private final ListingMapper listingMapper;
    private final ListingSearchRepository searchRepository;

    public EsSyncService(ListingMapper listingMapper, ListingSearchRepository searchRepository) {
        this.listingMapper = listingMapper;
        this.searchRepository = searchRepository;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onListingChanged(ListingChangedEvent event) {
        try {
            sync(event);
        } catch (Exception e) {
            // Drift recovery handled by reindexAll(); MySQL remains source of truth.
            log.error("ES sync failed for listing {} (removed={}): {}",
                    event.listingId(), event.removed(), e.getMessage());
        }
    }

    public void sync(ListingChangedEvent event) {
        if (event.removed()) {
            searchRepository.deleteById(event.listingId());
            return;
        }
        Listing listing = listingMapper.findById(event.listingId());
        if (listing == null || !"PUBLISHED".equals(listing.getStatus())) {
            searchRepository.deleteById(event.listingId());
            return;
        }
        listing.setPhotoUrls(listingMapper.findPhotoUrls(listing.getId()));
        searchRepository.save(EsListingDocument.from(listing));
    }

    /** Full reindex for drift recovery (FS-002). */
    public int reindexAll() {
        var published = listingMapper.findNewestPublished(10000);
        published.forEach(l -> {
            l.setPhotoUrls(listingMapper.findPhotoUrls(l.getId()));
            searchRepository.save(EsListingDocument.from(l));
        });
        return published.size();
    }
}
