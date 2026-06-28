package com.usedcar.recommendation;

import com.usedcar.common.SessionResolver;
import com.usedcar.config.AppProperties;
import com.usedcar.listing.dto.ListingSummary;
import com.usedcar.recommendation.dto.RecommendationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final PopularService popularService;
    private final SessionResolver sessionResolver;
    private final AppProperties props;

    public RecommendationController(RecommendationService recommendationService, PopularService popularService,
                                   SessionResolver sessionResolver, AppProperties props) {
        this.recommendationService = recommendationService;
        this.popularService = popularService;
        this.sessionResolver = sessionResolver;
        this.props = props;
    }

    @GetMapping("/recommendations")
    public RecommendationResponse recommendations(
            @RequestParam(required = false) String context,
            @RequestParam(required = false) Long excludeCarId,
            @RequestParam(required = false) Integer limit,
            HttpServletRequest request, HttpServletResponse response) {
        String sessionId = sessionResolver.resolve(request, response);
        int max = limit != null ? limit : props.getRecommendation().getMaxResults();
        return recommendationService.recommend(sessionId, excludeCarId, max);
    }

    @GetMapping("/popular")
    public List<ListingSummary> popular(@RequestParam(required = false) Integer limit) {
        int max = limit != null ? limit : props.getRecommendation().getMaxResults();
        return popularService.getPopular(max);
    }
}
