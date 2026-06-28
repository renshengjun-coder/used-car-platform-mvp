package com.usedcar.behavior;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usedcar.behavior.dto.SearchEventRequest;
import com.usedcar.domain.SearchEvent;
import com.usedcar.domain.ViewEvent;
import com.usedcar.mapper.SearchEventMapper;
import com.usedcar.mapper.ViewEventMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BehaviorService {

    private static final int DEDUPE_MINUTES = 5;
    private static final int MAX_SEARCH_EVENTS = 20;

    private final ViewEventMapper viewEventMapper;
    private final SearchEventMapper searchEventMapper;
    private final ObjectMapper objectMapper;

    public BehaviorService(ViewEventMapper viewEventMapper, SearchEventMapper searchEventMapper,
                           ObjectMapper objectMapper) {
        this.viewEventMapper = viewEventMapper;
        this.searchEventMapper = searchEventMapper;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void recordView(String sessionId, Long userId, Long carId) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(DEDUPE_MINUTES);
        if (viewEventMapper.existsRecent(sessionId, carId, since) > 0) {
            return; // dedupe within window (AC-010)
        }
        ViewEvent event = new ViewEvent();
        event.setSessionId(sessionId);
        event.setUserId(userId);
        event.setCarId(carId);
        viewEventMapper.insert(event);
    }

    @Transactional
    public void recordSearch(String sessionId, Long userId, SearchEventRequest req) {
        SearchEvent event = new SearchEvent();
        event.setSessionId(sessionId);
        event.setUserId(userId);
        event.setKeyword(req.keyword());
        event.setFiltersJson(toJson(req.filters()));
        searchEventMapper.insert(event);
        searchEventMapper.trimToLatest(sessionId, MAX_SEARCH_EVENTS); // AC-011
    }

    private String toJson(Object value) {
        if (value == null) return null;
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
