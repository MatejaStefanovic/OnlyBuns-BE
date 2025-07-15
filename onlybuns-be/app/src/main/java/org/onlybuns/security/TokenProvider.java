package org.onlybuns.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.onlybuns.model.User;
import org.onlybuns.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class TokenProvider {

    // Secret key for signing the JWT token
    @Value("${app.jwt.secret}")
    private String secretKey;

    // JWT expiration time (1 hour in milliseconds)
    @Value("${app.jwt.expiration}")
    private long expirationTime;

    @Autowired
    private UserRepository userRepository;

    public TokenProvider(UserRepository repository) {
       this.userRepository = repository;
    }
    // Generate JWT token from email
    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        User user = userRepository.findByEmail(email);
        user.setLastActivity(expiryDate);
        userRepository.save(user);

        // Create a signing key from the secret
        SecretKey key = getSecretKey();

       return  Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    private SecretKey getSecretKey() {
        CharSequence jwtSecret;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getSubjectFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    // Validate the JWT token by checking its expiration and signature
    public boolean validateToken(String token) {
        try {
            // Parse the JWT token and check for expiration
            SecretKey key = getSecretKey();
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token); // If the token is expired or invalid, it will throw an exception

            return true; // Token is valid
        } catch (ExpiredJwtException e) {
            // Token has expired
            System.out.println("JWT token has expired: " + e.getMessage());
        } catch (JwtException e) {
            // Invalid token or other JWT exceptions
            System.out.println("Invalid JWT token: " + e.getMessage());
        }
        return false; // Token is invalid
    }

    public Date getExpirationDateFromToken(String token) {
        try {
            SecretKey key = getSecretKey();
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getExpiration(); // Dohvatanje datuma isteka iz JWT-a
        } catch (JwtException e) {
            // Ako JWT nije validan, rukujte izuzecima po potrebi
            System.out.println("Invalid JWT token: " + e.getMessage());
            return null;
        }
    }


}
