package com.usedcar.recommendation;

import com.usedcar.config.AppProperties;
import com.usedcar.domain.Listing;
import com.usedcar.domain.SearchEvent;
import com.usedcar.listing.dto.ListingSummary;
import com.usedcar.mapper.ListingMapper;
import com.usedcar.mapper.SearchEventMapper;
import com.usedcar.mapper.ViewEventMapper;
import com.usedcar.recommendation.dto.RecommendationResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Rule-based recommendations (A-007). Blends attribute overlap, popularity, and recency.
 * Falls back to popular on cold start (AC-014) and guarantees a popular floor when history
 * is sparse (AC-013). Excludes already-viewed cars in the session.
 */
@Service
public class RecommendationService {

    private static final int CANDIDATE_POOL = 200;
    private static final int SPARSE_SIGNAL_THRESHOLD = 3;
    private static final double POPULAR_FLOOR_RATIO = 0.3;

    private final ViewEventMapper viewEventMapper;
    private final SearchEventMapper searchEventMapper;
    private final ListingMapper listingMapper;
    private final PopularService popularService;
    private final AppProperties props;

    public RecommendationService(ViewEventMapper viewEventMapper, SearchEventMapper searchEventMapper,
                                 ListingMapper listingMapper, PopularService popularService,
                                 AppProperties props) {
        this.viewEventMapper = viewEventMapper;
        this.searchEventMapper = searchEventMapper;
        this.listingMapper = listingMapper;
        this.popularService = popularService;
        this.props = props;
    }

    public RecommendationResponse recommend(String sessionId, Long excludeCarId, int limit) {
        List<Long> recentViewedIds = viewEventMapper.findRecentCarIdsBySession(sessionId, 20);
        List<SearchEvent> searches = searchEventMapper.findRecentBySession(sessionId, 20);

        if (recentViewedIds.isEmpty() && searches.isEmpty()) {
            return new RecommendationResponse(popularService.getPopular(limit),
                    RecommendationResponse.POPULAR_FALLBACK);
        }

        Set<Long> exclude = new HashSet<>(recentViewedIds);
        if (excludeCarId != null) exclude.add(excludeCarId);

        Preferences prefs = derivePreferences(recentViewedIds, searches);

        List<Listing> pool = listingMapper.findNewestPublished(CANDIDATE_POOL);
        Map<Long, Integer> popularity = popularityMap();
        int maxCount = popularity.values().stream().max(Integer::compareTo).orElse(1);

        List<Scored> scored = new ArrayList<>();
        for (int i = 0; i < pool.size(); i++) {
            Listing l = pool.get(i);
            if (exclude.contains(l.getId())) continue;
            double recency = 1.0 - ((double) i / Math.max(pool.size(), 1));
            double pop = maxCount == 0 ? 0 : (double) popularity.getOrDefault(l.getId(), 0) / maxCount;
            double attr = attrMatch(l, prefs);
            double score = props.getRecommendation().getWeightAttrMatch() * attr
                    + props.getRecommendation().getWeightPopularity() * pop
                    + props.getRecommendation().getWeightRecency() * recency;
            scored.add(new Scored(l, score));
        }
        scored.sort(Comparator.comparingDouble((Scored s) -> s.score).reversed());

        List<ListingSummary> result = new ArrayList<>();
        LinkedHashSet<Long> chosen = new LinkedHashSet<>();

        boolean sparse = (recentViewedIds.size() + searches.size()) < SPARSE_SIGNAL_THRESHOLD;
        if (sparse) {
            int floor = (int) Math.ceil(limit * POPULAR_FLOOR_RATIO);
            for (ListingSummary p : popularService.getPopular(limit)) {
                if (result.size() >= floor) break;
                if (!exclude.contains(p.id()) && chosen.add(p.id())) result.add(p);
            }
        }

        for (Scored s : scored) {
            if (result.size() >= limit) break;
            if (chosen.add(s.listing.getId())) result.add(popularService.toSummary(s.listing));
        }

        return new RecommendationResponse(result.stream().limit(limit).toList(),
                RecommendationResponse.PERSONALIZED);
    }

    private Preferences derivePreferences(List<Long> viewedIds, List<SearchEvent> searches) {
        Preferences p = new Preferences();
        if (!viewedIds.isEmpty()) {
            for (Listing l : listingMapper.findByIds(viewedIds)) {
                if (l.getMake() != null) p.makes.add(l.getMake());
                if (l.getModel() != null) p.models.add(l.getModel());
                if (l.getCity() != null) p.cities.add(l.getCity());
                if (l.getPrice() != null) p.prices.add(l.getPrice());
            }
        }
        for (SearchEvent s : searches) {
            if (s.getKeyword() != null && !s.getKeyword().isBlank()) {
                p.keywords.add(s.getKeyword().toLowerCase());
            }
        }
        return p;
    }

    private double attrMatch(Listing l, Preferences p) {
        double score = 0;
        if (l.getMake() != null && p.makes.contains(l.getMake())) score += 2;
        if (l.getModel() != null && p.models.contains(l.getModel())) score += 1;
        if (l.getCity() != null && p.cities.contains(l.getCity())) score += 1;
        if (l.getPrice() != null && priceWithinBand(l.getPrice(), p.prices)) score += 1;
        if (matchesKeyword(l, p.keywords)) score += 1;
        return score / 6.0;
    }

    private boolean priceWithinBand(BigDecimal price, Set<BigDecimal> prefs) {
        for (BigDecimal pref : prefs) {
            double lo = pref.doubleValue() * 0.8;
            double hi = pref.doubleValue() * 1.2;
            if (price.doubleValue() >= lo && price.doubleValue() <= hi) return true;
        }
        return false;
    }

    private boolean matchesKeyword(Listing l, Set<String> keywords) {
        for (String kw : keywords) {
            String hay = (l.getTitle() + " " + l.getMake() + " " + l.getModel()).toLowerCase();
            if (hay.contains(kw)) return true;
        }
        return false;
    }

    private Map<Long, Integer> popularityMap() {
        LocalDateTime since = LocalDateTime.now().minusDays(props.getRecommendation().getPopularWindowDays());
        Map<Long, Integer> map = new HashMap<>();
        for (Map<String, Object> row : viewEventMapper.countByCarSince(since, CANDIDATE_POOL)) {
            map.put(((Number) row.get("carId")).longValue(), ((Number) row.get("viewCount")).intValue());
        }
        return map;
    }

    private static class Preferences {
        Set<String> makes = new HashSet<>();
        Set<String> models = new HashSet<>();
        Set<String> cities = new HashSet<>();
        Set<BigDecimal> prices = new HashSet<>();
        Set<String> keywords = new HashSet<>();
    }

    private record Scored(Listing listing, double score) {}
}
