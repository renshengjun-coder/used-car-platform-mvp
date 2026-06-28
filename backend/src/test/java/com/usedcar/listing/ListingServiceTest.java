package com.usedcar.listing;

import com.usedcar.common.ApiException;
import com.usedcar.config.AppProperties;
import com.usedcar.listing.dto.CreateListingRequest;
import com.usedcar.mapper.ListingMapper;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class ListingServiceTest {

    private final ListingMapper listingMapper = mock(ListingMapper.class);
    private final ApplicationEventPublisher events = mock(ApplicationEventPublisher.class);
    private final AppProperties props = new AppProperties();

    private final ListingService service = new ListingService(listingMapper, events, props);

    @Test
    void publishWithoutPhoto_isRejected() {
        CreateListingRequest req = new CreateListingRequest(
                "2019 Toyota Camry", new BigDecimal("158000"), "Toyota", "Camry",
                2019, 42000, "GASOLINE", "AUTOMATIC", "Beijing", "desc",
                List.of(), "PUBLISHED");

        assertThatThrownBy(() -> service.create(1L, req))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("photo");
    }

    @Test
    void tooManyPhotos_isRejected() {
        List<String> photos = java.util.stream.IntStream.range(0, 20)
                .mapToObj(i -> "https://img/" + i + ".jpg").toList();
        CreateListingRequest req = new CreateListingRequest(
                "2019 Toyota Camry", new BigDecimal("158000"), "Toyota", "Camry",
                2019, 42000, "GASOLINE", "AUTOMATIC", "Beijing", "desc",
                photos, "PUBLISHED");

        assertThatThrownBy(() -> service.create(1L, req))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Too many photos");
    }
}
