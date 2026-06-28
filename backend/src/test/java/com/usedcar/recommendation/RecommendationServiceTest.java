package com.usedcar.recommendation;

import com.usedcar.config.AppProperties;
import com.usedcar.listing.dto.ListingSummary;
import com.usedcar.mapper.ListingMapper;
import com.usedcar.mapper.SearchEventMapper;
import com.usedcar.mapper.ViewEventMapper;
import com.usedcar.recommendation.dto.RecommendationResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RecommendationServiceTest {

    private final ViewEventMapper viewEventMapper = mock(ViewEventMapper.class);
    private final SearchEventMapper searchEventMapper = mock(SearchEventMapper.class);
    private final ListingMapper listingMapper = mock(ListingMapper.class);
    private final PopularService popularService = mock(PopularService.class);
    private final AppProperties props = new AppProperties();

    private RecommendationService service() {
        return new RecommendationService(viewEventMapper, searchEventMapper,
                listingMapper, popularService, props);
    }

    @Test
    void coldStart_returnsPopularFallback() {
        when(viewEventMapper.findRecentCarIdsBySession("s1", 20)).thenReturn(List.of());
        when(searchEventMapper.findRecentBySession("s1", 20)).thenReturn(List.of());
        when(popularService.getPopular(10)).thenReturn(List.of(summary(1L), summary(2L)));

        RecommendationResponse res = service().recommend("s1", null, 10);

        assertThat(res.strategy()).isEqualTo(RecommendationResponse.POPULAR_FALLBACK);
        assertThat(res.items()).hasSize(2);
        verify(popularService).getPopular(10);
    }

    private ListingSummary summary(Long id) {
        return new ListingSummary(id, "Car " + id, BigDecimal.valueOf(100000), "Toyota", "Camry",
                2020, 30000, "Beijing", null, LocalDateTime.now());
    }
}
