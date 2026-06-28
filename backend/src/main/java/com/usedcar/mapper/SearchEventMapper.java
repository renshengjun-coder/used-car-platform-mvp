package com.usedcar.mapper;

import com.usedcar.domain.SearchEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SearchEventMapper {
    int insert(SearchEvent event);
    List<SearchEvent> findRecentBySession(@Param("sessionId") String sessionId,
                                          @Param("limit") int limit);
    void trimToLatest(@Param("sessionId") String sessionId, @Param("keep") int keep);
}
