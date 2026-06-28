package com.usedcar.search;

import com.usedcar.common.ApiException;
import com.usedcar.common.PageResponse;
import com.usedcar.listing.dto.ListingSummary;
import com.usedcar.search.dto.SearchQuery;
import com.usedcar.search.es.EsListingDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Service
public class SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

    private final ElasticsearchOperations operations;

    public SearchService(ElasticsearchOperations operations) {
        this.operations = operations;
    }

    public PageResponse<ListingSummary> search(SearchQuery q) {
        Criteria criteria = new Criteria("status").is("PUBLISHED");

        if (StringUtils.hasText(q.keyword())) {
            Criteria kw = new Criteria("title").matches(q.keyword())
                    .or(new Criteria("make").contains(q.keyword()))
                    .or(new Criteria("model").contains(q.keyword()));
            criteria = criteria.and(kw);
        }
        criteria = applyRange(criteria, "price", toD(q.priceMin()), toD(q.priceMax()));
        criteria = applyRange(criteria, "year", q.yearMin(), q.yearMax());
        criteria = applyRange(criteria, "mileage", q.mileageMin(), q.mileageMax());
        criteria = applyTerm(criteria, "city", q.city());
        criteria = applyTerm(criteria, "fuelType", q.fuelType());
        criteria = applyTerm(criteria, "transmission", q.transmission());
        criteria = applyTerm(criteria, "make", q.make());
        criteria = applyTerm(criteria, "model", q.model());

        CriteriaQuery query = new CriteriaQuery(criteria);
        query.setPageable(PageRequest.of(q.page(), q.size(),
                Sort.by(Sort.Direction.DESC, "publishedAt")));

        try {
            SearchHits<EsListingDocument> hits = operations.search(query, EsListingDocument.class);
            var content = hits.getSearchHits().stream()
                    .map(h -> toSummary(h.getContent())).toList();
            return PageResponse.of(content, q.page(), q.size(), hits.getTotalHits());
        } catch (Exception e) {
            log.error("Search failed: {}", e.getMessage());
            throw ApiException.serviceUnavailable("SEARCH_UNAVAILABLE", "Search is temporarily unavailable");
        }
    }

    private Criteria applyRange(Criteria criteria, String field, Number min, Number max) {
        if (min != null) criteria = criteria.and(new Criteria(field).greaterThanEqual(min));
        if (max != null) criteria = criteria.and(new Criteria(field).lessThanEqual(max));
        return criteria;
    }

    private Criteria applyTerm(Criteria criteria, String field, String value) {
        if (StringUtils.hasText(value)) criteria = criteria.and(new Criteria(field).is(value));
        return criteria;
    }

    private Double toD(BigDecimal v) { return v == null ? null : v.doubleValue(); }

    private ListingSummary toSummary(EsListingDocument d) {
        return new ListingSummary(d.getId(), d.getTitle(),
                d.getPrice() == null ? null : BigDecimal.valueOf(d.getPrice()),
                d.getMake(), d.getModel(), d.getYear(), d.getMileage(),
                d.getCity(), d.getThumbnailUrl(), d.getPublishedAt());
    }
}
