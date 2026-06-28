package com.usedcar.common;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;

/** Resolves an anonymous session id from cookie or X-Session-Id header (A-003). */
@Component
public class SessionResolver {

    public static final String COOKIE_NAME = "sid";

    public String resolve(HttpServletRequest request, HttpServletResponse response) {
        String header = request.getHeader("X-Session-Id");
        if (StringUtils.hasText(header)) return header;

        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (COOKIE_NAME.equals(c.getName()) && StringUtils.hasText(c.getValue())) {
                    return c.getValue();
                }
            }
        }
        String sid = UUID.randomUUID().toString();
        Cookie cookie = new Cookie(COOKIE_NAME, sid);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 60 * 24 * 30);
        response.addCookie(cookie);
        return sid;
    }
}
