package org.onlybuns.rateLimiter;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

@Service
public class InMemoryRateLimiter {

    // Mapa koja čuva Deque (dvostrani red) timestampova za svakog korisnika (identifikovanog po ključu)
    private final ConcurrentHashMap<String, Deque<Long>> userRequestTimestamps = new ConcurrentHashMap<>();

    private final long MAX_REQUESTS; // Maksimalan broj zahteva
    private final long TIME_WINDOW_MILLIS; // Vremenski prozor u milisekundama

    public InMemoryRateLimiter() {
        this.MAX_REQUESTS = 5; // 5 zahteva
        this.TIME_WINDOW_MILLIS = TimeUnit.MINUTES.toMillis(1); // u 1 minuti
    }

    public InMemoryRateLimiter(long maxRequests, long timeWindow, TimeUnit unit) {
        this.MAX_REQUESTS = maxRequests;
        this.TIME_WINDOW_MILLIS = unit.toMillis(timeWindow);
    }

    /**
     * Proverava da li je korisniku dozvoljen zahtev.
     *
     * @param userKey Jedinstveni identifikator korisnika (npr. ID korisnika, IP adresa)
     * @return true ako je zahtev dozvoljen, false inače.
     */
    public boolean tryAcquire(String userKey) {
        // Dobijamo (ili kreiramo) Deque za datog korisnika.
        // computeIfAbsent osigurava atomičnost kreiranja Deque-a ako ne postoji
        Deque<Long> timestamps = userRequestTimestamps.computeIfAbsent(userKey, k -> new ConcurrentLinkedDeque<>());

        long currentTime = Instant.now().toEpochMilli(); // Trenutno vreme u milisekundama

        // Sinkronizovani blok da se osigura Thread-safe operacija nad Deque-om
        synchronized (timestamps) {
            // 1. Ukloni sve timestampe koji su van vremenskog prozora
            // While petlja je bolja od if, jer može biti više isteklih zahteva u redu
            while (!timestamps.isEmpty() && timestamps.peekFirst() < currentTime - TIME_WINDOW_MILLIS) {
                timestamps.removeFirst(); // Ukloni najstariji istekli timestamp
            }

            // 2. Proveri da li je dozvoljen novi zahtev
            if (timestamps.size() < MAX_REQUESTS) {
                timestamps.addLast(currentTime); // Dodaj trenutni timestamp
                return true; // Zahtev dozvoljen
            } else {
                return false; // Prekoračen limit
            }
        }
    }
}
