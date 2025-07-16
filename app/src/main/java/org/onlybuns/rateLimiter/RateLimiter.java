package org.onlybuns.rateLimiter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;


@Target(ElementType.METHOD) // Može se primeniti samo na metode
@Retention(RetentionPolicy.RUNTIME) // Dostupna u runtime-u
public @interface RateLimiter {

    // Ključ koji će se koristiti za identifikaciju korisnika/zahteva.
    // Ako je prazan, očekuje se da Aspect dohvati ključ iz konteksta (npr. korisnički ID).
    String key() default "";

    long maxRequests() default 3; // Podrazumevani maksimalni broj zahteva
    long timeWindow() default 1;  // Podrazumevani vremenski prozor
    TimeUnit unit() default TimeUnit.MINUTES; // Podrazumevana jedinica za vremenski prozor

}
