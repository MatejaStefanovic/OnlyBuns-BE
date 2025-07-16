package org.onlybuns.rateLimiter;


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
    private final JWTAuthenticationFilter jwtAuthenticationFilter;// Pretpostavka da imaš TokenProvider za JWT

    @Autowired
    public RateLimiterAspect(InMemoryRateLimiter rateLimiter, TokenProvider tokenProvider, JWTAuthenticationFilter jwtAuthenticationFilter) {
        this.rateLimiter = rateLimiter;
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Around("@annotation(org.onlybuns.rateLimiter.RateLimiter)")
    public Object applyRateLimiting(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RateLimiter rateLimited = signature.getMethod().getAnnotation(RateLimiter.class);

        String userKey = rateLimited.key();

        // Dohvati ključ korisnika ako nije eksplicitno definisan u anotaciji
        if (userKey.isEmpty()) {
            // Primer: dohvatanje korisničkog ID-a iz Spring Security konteksta ili iz tokena
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                // Ako je principal User objekat (npr. ako koristiš custom UserDetails)
                userKey = ((Long)((User) authentication.getPrincipal()).getId()).toString();
            } else if (authentication != null && authentication.getPrincipal() instanceof String) {
                // Ako je principal korisničko ime (string), a ne User objekat
                // Ovde bi trebalo da dohvatiš JWT token iz zahteva i validiraš ga
                // da bi dobio korisnički ID ili neko drugo jedinstveno obeležje.
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
                String jwt = jwtAuthenticationFilter.extractToken(request); // Pretpostavka da TokenProvider ima ovu metodu

                if (jwt != null && tokenProvider.validateToken(jwt)) {
                    userKey = tokenProvider.getUserIdFromToken(jwt).toString(); // Pretpostavka da TokenProvider ima ovu metodu
                } else {
                    // Ako nema autentifikovanog korisnika ili validnog tokena, koristi IP adresu
                    userKey = request.getRemoteAddr();
                }
            } else {
                // Ako nije autentifikovan korisnik, koristi IP adresu kao ključ
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
                userKey = request.getRemoteAddr();
            }
        }

        // Ako je userKey i dalje prazan (npr. fallback nije uspeo), baci grešku
        if (userKey.isEmpty() || "anonymousUser".equals(userKey)) {
            // Ovo se može desiti ako anonimni korisnik pokuša da komentariše, a ne želimo da ga limitiramo
            // ili želimo da ga limitiramo po IP adresi. Ako se koristi @RateLimited, mora postojati ključ.
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            userKey = request.getRemoteAddr();
        }

        // Proveri da li je zahtev dozvoljen
        if (!rateLimiter.tryAcquire(userKey)) {
            //System.out.println("premasio 5!.");
            throw new TooManyRequestsException("Prekoračen broj zahteva. Molimo pokušajte kasnije.");
        }

        // Ako je dozvoljeno, nastavi sa izvršavanjem metode kontrolera
        return joinPoint.proceed();
    }
}
