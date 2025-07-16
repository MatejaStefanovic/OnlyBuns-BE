package org.onlybuns.loadbalancer;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RoundRobinLoadBalancer {

    // Lista URL-ova backend servisa
    @Value("${backend.service.urls}")
    private String serviceUrlsString;

    private List<String> serviceUrls;

    // brojac, koji je sledeći server na redu.
    private final AtomicInteger counter = new AtomicInteger(0);

    // Inicijalizuje listu URL-ova iz Stringa
    @PostConstruct
    public void init() {
        if (serviceUrlsString == null || serviceUrlsString.isEmpty()) {
            throw new IllegalStateException("Backend service URLs (backend.service.urls) are empty or not found in application.properties.");
        }

        this.serviceUrls = Arrays.asList(serviceUrlsString.split(","));

        if (serviceUrls.isEmpty()) {
            throw new IllegalStateException("Backend service URLs are empty after parsing the property string. Check 'backend.service.urls' format (e.g., ensure no trailing comma).");
        }

        System.out.println("RoundRobinLoadBalancer initialized with URLs: " + serviceUrls);
    }

    //Vraća URL sledećeg backend servisa koristeći Round Robin algoritam
      public String getNextServiceUrl() {
        if (serviceUrls.isEmpty() ) {
            throw new IllegalStateException("No backend service URLs available for Round Robin.");
        }

        // Dohvati trenutnu vrednost brojača, zatim je inkrementiraj i vrati brojac na 0 na kraju liste
        int index = counter.getAndIncrement() % serviceUrls.size();

        if (counter.get() >= Integer.MAX_VALUE - 100) { // Resetuj blizu maksimuma
            counter.set(0);
        }

        String selectedUrl = serviceUrls.get(index);
        System.out.println("Selected URL for next request (Round Robin): " + selectedUrl);
        return selectedUrl;
    }
}