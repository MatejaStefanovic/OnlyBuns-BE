package org.onlybuns.rateLimiterImplementation;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

@Service
public class InMemoryRateLimiter {


    private final ConcurrentHashMap<String, Deque<Long>> userRequestTimestamps = new ConcurrentHashMap<>();




    public InMemoryRateLimiter() {

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

        Deque<Long> timestamps = userRequestTimestamps.computeIfAbsent(userKey, k -> new ConcurrentLinkedDeque<>());

        long currentTime = Instant.now().toEpochMilli();


        synchronized (timestamps) {

            while (!timestamps.isEmpty() && timestamps.peekFirst() < currentTime - timeWindowMillis) {
                timestamps.removeFirst();
            }


            if (timestamps.size() < maxRequests) {
                timestamps.addLast(currentTime);
                return true;
            } else {
                return false;
            }
        }
    }

}
