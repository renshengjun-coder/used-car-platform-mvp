package com.usedcar.recommendation;

import com.usedcar.config.AppProperties;
import com.usedcar.domain.Listing;
import com.usedcar.listing.dto.ListingSummary;
import com.usedcar.mapper.ListingMapper;
import com.usedcar.mapper.ViewEventMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/** Popular cars by N-day view count with newest fallback (AC-012). */
@Service
public class PopularService {

    private final ViewEventMapper viewEventMapper;
    private final ListingMapper listingMapper;
    private final AppProperties props;

    public PopularService(ViewEventMapper viewEventMapper, ListingMapper listingMapper, AppProperties props) {
        this.viewEventMapper = viewEventMapper;
        this.listingMapper = listingMapper;
        this.props = props;
    }

    public List<ListingSummary> getPopular(int limit) {
        LocalDateTime since = LocalDateTime.now().minusDays(props.getRecommendation().getPopularWindowDays());
        List<Map<String, Object>> counts = viewEventMapper.countByCarSince(since, limit);

        List<Long> orderedIds = counts.stream()
                .map(m -> ((Number) m.get("carId")).longValue())
                .toList();

        LinkedHashMap<Long, Listing> byId = new LinkedHashMap<>();
        if (!orderedIds.isEmpty()) {
            Map<Long, Listing> fetched = new HashMap<>();
            for (Listing l : listingMapper.findByIds(orderedIds)) {
                if ("PUBLISHED".equals(l.getStatus())) fetched.put(l.getId(), l);
            }
            for (Long id : orderedIds) {
                if (fetched.containsKey(id)) byId.put(id, fetched.get(id));
            }
        }

        // Fallback to newest when fewer than 5 cars have views (AC-012).
        if (byId.size() < 5) {
            for (Listing l : listingMapper.findNewestPublished(limit)) {
                byId.putIfAbsent(l.getId(), l);
                if (byId.size() >= limit) break;
            }
        }

        return byId.values().stream().limit(limit).map(this::toSummary).toList();
    }

    ListingSummary toSummary(Listing l) {
        List<String> photos = listingMapper.findPhotoUrls(l.getId());
        String thumb = photos.isEmpty() ? null : photos.get(0);
        return new ListingSummary(l.getId(), l.getTitle(), l.getPrice(), l.getMake(), l.getModel(),
                l.getYear(), l.getMileage(), l.getCity(), thumb, l.getPublishedAt());
    }
}
