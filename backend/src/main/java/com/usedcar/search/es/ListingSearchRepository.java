package com.usedcar.search.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ListingSearchRepository
        extends ElasticsearchRepository<EsListingDocument, Long> {
}
