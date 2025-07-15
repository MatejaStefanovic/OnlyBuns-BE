package org.onlybuns.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    // Inject TokenProvider through constructor
    public JWTAuthenticationFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String jwt = extractToken(request);

        // Validate the token and set authentication if valid
        if (jwt != null && tokenProvider.validateToken(jwt)) {
            String email = tokenProvider.getSubjectFromJWT(jwt);

            // Create an authentication object with the email and set it in the context
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, null); // No authorities here; add if needed
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Set authentication in context
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Proceed with the next filter in the chain
        chain.doFilter(request, response);
    }

    // Extract token from Authorization header
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
