package com.usedcar.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static Long currentSellerId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Long sellerId)) {
            throw ApiException.unauthorized("Authentication required");
        }
        return sellerId;
    }

    public static Long currentSellerIdOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long sellerId) {
            return sellerId;
        }
        return null;
    }
}
