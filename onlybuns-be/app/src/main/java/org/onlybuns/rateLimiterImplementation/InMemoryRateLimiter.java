package org.onlybuns.rateLimiterImplementation;

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

    // Uklanjamo fiksne konstante iz konstruktora
    // MAX_REQUESTS i TIME_WINDOW_MILLIS sada dolaze kao argumenti u tryAcquire

    // Nema više konstruktora koji prima limite, jer ih metoda tryAcquire prima
    public InMemoryRateLimiter() {
        // Prazan konstruktor je u redu, jer @Service anotacija zahteva default konstruktor
    }

    /**
     * Proverava da li je korisniku dozvoljen zahtev, koristeći prosleđene limite.
     *
     * @param userKey           Jedinstveni identifikator korisnika (npr. ID korisnika, IP adresa)
     * @param maxRequests       Maksimalan broj zahteva dozvoljenih unutar vremenskog prozora.
     * @param timeWindowMillis  Vremenski prozor u milisekundama.
     * @return true ako je zahtev dozvoljen, false inače.
     */
    public boolean tryAcquire(String userKey, long maxRequests, long timeWindowMillis) {
        // Dobijamo (ili kreiramo) Deque za datog korisnika.
        // computeIfAbsent osigurava atomičnost kreiranja Deque-a ako ne postoji
        Deque<Long> timestamps = userRequestTimestamps.computeIfAbsent(userKey, k -> new ConcurrentLinkedDeque<>());

        long currentTime = Instant.now().toEpochMilli(); // Trenutno vreme u milisekundama

        // Sinkronizovani blok da se osigura Thread-safe operacija nad Deque-om
        // Sinhronizacija na samom Deque-u je ključna za thread-safe rad
        synchronized (timestamps) {
            // 1. Ukloni sve timestampe koji su van vremenskog prozora
            // While petlja je bolja od if, jer može biti više isteklih zahteva u redu
            while (!timestamps.isEmpty() && timestamps.peekFirst() < currentTime - timeWindowMillis) {
                timestamps.removeFirst(); // Ukloni najstariji istekli timestamp
            }

            // 2. Proveri da li je dozvoljen novi zahtev
            if (timestamps.size() < maxRequests) {
                timestamps.addLast(currentTime); // Dodaj trenutni timestamp
                return true; // Zahtev dozvoljen
            } else {
                return false; // Prekoračen limit
            }
        }
    }

}
