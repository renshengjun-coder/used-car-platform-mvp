package com.usedcar.behavior;

import com.usedcar.behavior.dto.SearchEventRequest;
import com.usedcar.behavior.dto.ViewRequest;
import com.usedcar.common.SecurityUtils;
import com.usedcar.common.SessionResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/behavior")
public class BehaviorController {

    private final BehaviorService behaviorService;
    private final SessionResolver sessionResolver;

    public BehaviorController(BehaviorService behaviorService, SessionResolver sessionResolver) {
        this.behaviorService = behaviorService;
        this.sessionResolver = sessionResolver;
    }

    @PostMapping("/view")
    public ResponseEntity<Void> recordView(@Valid @RequestBody ViewRequest req,
                                           HttpServletRequest request, HttpServletResponse response) {
        String sessionId = sessionResolver.resolve(request, response);
        behaviorService.recordView(sessionId, SecurityUtils.currentSellerIdOrNull(), req.carId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping("/search")
    public ResponseEntity<Void> recordSearch(@RequestBody SearchEventRequest req,
                                             HttpServletRequest request, HttpServletResponse response) {
        String sessionId = sessionResolver.resolve(request, response);
        behaviorService.recordSearch(sessionId, SecurityUtils.currentSellerIdOrNull(), req);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
