package org.onlybuns.rateLimiterImplementation;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.onlybuns.exceptions.RateLimiter.TooManyRequestsException;
import org.onlybuns.model.User;
import org.onlybuns.security.JWTAuthenticationFilter;
import org.onlybuns.security.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component("customRateLimiterAspect")
public class RateLimiterAspect {
    private final InMemoryRateLimiter rateLimiter;
    private final TokenProvider tokenProvider;
    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public RateLimiterAspect(InMemoryRateLimiter rateLimiter, TokenProvider tokenProvider, JWTAuthenticationFilter jwtAuthenticationFilter) {
        this.rateLimiter = rateLimiter;
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Around("@annotation(org.onlybuns.rateLimiterImplementation.RateLimiter)")
    public Object applyRateLimiting(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RateLimiter rateLimited = signature.getMethod().getAnnotation(RateLimiter.class);

        // --- IZVLAČENJE VREDNOSTI IZ ANOTACIJE ---
        long maxRequests = rateLimited.maxRequests();
        long timeWindowMillis = rateLimited.unit().toMillis(rateLimited.timeWindow());
        // ------------------------------------------

        String userKey = rateLimited.key();

        if (userKey.isEmpty()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

            if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
                if (authentication.getPrincipal() instanceof User) {
                    userKey = String.valueOf(((User) authentication.getPrincipal()).getId());
                } else if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
                    String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
                    String jwt = jwtAuthenticationFilter.extractToken(request);
                    if (jwt != null && tokenProvider.validateToken(jwt)) {
                        Long userId = tokenProvider.getUserIdFromToken(jwt);
                        if (userId != null) {
                            userKey = String.valueOf(userId);
                        }
                    }
                }
            }

            if (userKey.isEmpty()) {
                String jwt = jwtAuthenticationFilter.extractToken(request);
                if (jwt != null && tokenProvider.validateToken(jwt)) {
                    Long userId = tokenProvider.getUserIdFromToken(jwt);
                    if (userId != null) {
                        userKey = String.valueOf(userId);
                    }
                }
            }

            if (userKey.isEmpty() || "anonymousUser".equals(userKey)) {
                userKey = request.getRemoteAddr();
            }
        }

        // --- POZIVANJE tryAcquire SA IZVUČENIM VREDNOSTIMA ---
        if (!rateLimiter.tryAcquire(userKey, maxRequests, timeWindowMillis)) {
            throw new TooManyRequestsException("Prekoračen broj zahteva. Molimo pokušajte kasnije.");
        }
        // ----------------------------------------------------

        return joinPoint.proceed();
    }
}
