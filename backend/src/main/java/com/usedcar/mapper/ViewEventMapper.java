package com.usedcar.mapper;

import com.usedcar.domain.ViewEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface ViewEventMapper {
    int insert(ViewEvent event);
    int existsRecent(@Param("sessionId") String sessionId, @Param("carId") Long carId,
                     @Param("since") LocalDateTime since);
    List<Long> findRecentCarIdsBySession(@Param("sessionId") String sessionId,
                                         @Param("limit") int limit);
    List<Map<String, Object>> countByCarSince(@Param("since") LocalDateTime since,
                                              @Param("limit") int limit);
}
