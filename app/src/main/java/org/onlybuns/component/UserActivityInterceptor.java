package org.onlybuns.component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.onlybuns.security.AuthenticationService;
import org.onlybuns.service.ActivityTrackingService;
import org.onlybuns.service.UserActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserActivityInterceptor implements HandlerInterceptor {

    @Autowired
    private ActivityTrackingService userActivityService;

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        // Izvuci korisnika iz JWT tokena
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                String userEmail = extractEmailFromToken(token); // implementirajte ovu metodu

                if (userEmail != null) {
                    userActivityService.recordUserActivity(userEmail);
                }
            } catch (Exception e) {
                // Log greške
            }
        }

        return true;
    }

    private String extractEmailFromToken(String token) {

        String usermail = authenticationService.getEmailFromJWT(token);
        return usermail;
    }


}
