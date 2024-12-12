package org.onlybuns.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IpRateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, RequestCounter> ipRequestCounts = new ConcurrentHashMap<>();
    private final int MAX_REQUESTS = 5;  // Set your threshold
    private final long TIME_WINDOW = 60 * 1000;  // 1 minute in milliseconds
    private final String LOGIN_ENDPOINT = "/api/user/login";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Check if the request is for the login endpoint
        if (request.getRequestURI().equals(LOGIN_ENDPOINT)) {
            String clientIp = request.getRemoteAddr();
            long currentTime = System.currentTimeMillis();

            // Clean up expired entries
            ipRequestCounts.entrySet().removeIf(entry -> currentTime - entry.getValue().startTime > TIME_WINDOW);

            // Check or update request count
            ipRequestCounts.compute(clientIp, (ip, counter) -> {
                if (counter == null || currentTime - counter.startTime > TIME_WINDOW) {
                    return new RequestCounter(1, currentTime);  // Reset counter
                } else {
                    counter.count++;
                    return counter;
                }
            });

            // Enforce rate limit
            RequestCounter counter = ipRequestCounts.get(clientIp);
            if (counter != null && counter.count > MAX_REQUESTS) {
                long remainingTime = TIME_WINDOW - (currentTime - counter.startTime);
                long remainingSeconds = remainingTime / 1000;

                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                String jsonResponse = "{\"error\": \"Rate limit exceeded. Please try again in " + remainingSeconds + " seconds.\"}";

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(jsonResponse);
                return;
            }
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }

    // Inner class to store request count and time
    private static class RequestCounter {
        int count;
        long startTime;

        RequestCounter(int count, long startTime) {
            this.count = count;
            this.startTime = startTime;
        }
    }
}