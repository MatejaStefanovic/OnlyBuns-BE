package org.onlybuns.service;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Component
public class ActivityTrackingService {

    private final MeterRegistry meterRegistry;
    private final Set<String> activeUsers = ConcurrentHashMap.newKeySet();
    private final Map<String, LocalDateTime> userLastActivity = new ConcurrentHashMap<>();

    public ActivityTrackingService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        // Registracija Gauge metrike - ovo je ispravna sintaksa za Micrometer 1.13+
        meterRegistry.gauge("active_users_count", this, ActivityTrackingService::getActiveUsersCount);
    }

    public void recordUserActivity(String userEmail) {
        activeUsers.add(userEmail);
        userLastActivity.put(userEmail, LocalDateTime.now());
    }

    public double getActiveUsersCount() {
        cleanupInactiveUsers();
        return activeUsers.size();
    }

    private void cleanupInactiveUsers() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);
        userLastActivity.entrySet().removeIf(entry -> {
            if (entry.getValue().isBefore(threshold)) {
                activeUsers.remove(entry.getKey());
                return true;
            }
            return false;
        });
    }

    @Scheduled(fixedRate = 300000) // svakih 5 minuta
    public void scheduleCleanup() {
        cleanupInactiveUsers();
    }
}



