package org.onlybuns.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import org.onlybuns.loadbalancer.RoundRobinLoadBalancer;

@RestController
public class LoadBalancerController {

    @Value("${server.port}")
    private String currentInstancePort;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private RoundRobinLoadBalancer roundRobinLoadBalancer;

    // Endpoint - port instance koja je primila zahtjev
    @GetMapping("/hello-from-backend")
    public String helloFromBackend() {
        System.out.println(">>> Request received on backend instance: " + currentInstancePort);
        return "Hello from backend service running on port: " + currentInstancePort;
    }


    // Endpoint poziv ka drugim instancama istog servisa
    @GetMapping("/call-backend-manual-lb")
    public String callSelfViaLoadBalancer() {
        int maxAttempts = 5; // Maksimalan broj pokusaja
        long initialDelayMillis = 1000; // Pocetna pauza između pokusaja (1 sekunda)
        double multiplier = 2.0;

        String targetUrl = "";

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                //  sledeci URL
                targetUrl = roundRobinLoadBalancer.getNextServiceUrl() + "/hello-from-backend";
                System.out.println("--- Pokusaj " + attempt + ": Iniciram poziv na '" + targetUrl + "' s porta: " + currentInstancePort + " ---");

                String response = restTemplate.getForObject(targetUrl, String.class);
                System.out.println("--- Pokusaj " + attempt + ": Primljen odgovor: " + response + " ---");
                return "Manual Load Balancer je pozvao: " + response; // Uspjesan odgovor
            } catch (ResourceAccessException e) {
                // Hvata greske povezivanja
                System.err.println("!!! Pokusaj " + attempt + ": Nije se uspjelo spojiti na backend instancu na " + targetUrl + ": " + e.getMessage());

                if (attempt < maxAttempts) {
                    // Izracunaj kasnjenje za sledeci pokusaj
                    long currentDelay = (long) (initialDelayMillis * Math.pow(multiplier, attempt - 1));
                    System.out.println("!!! Cekam " + currentDelay + "ms prije ponovnog pokusaja...");
                    try {
                        Thread.sleep(currentDelay); // Pauza prije sledeceg pokusaja
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        System.err.println("!!! Ponovni pokusaj je prekinut.");
                        throw new RuntimeException("Ponovni pokusaj je prekinut", ie);
                    }
                } else {
                    // Ako je ovo bio posljednji pokusaj i nije uspio
                    System.err.println("!!! Dostignut maksimalni broj pokušaja. Nema dostupnih backend instanci.");
                    throw new RuntimeException("Nema dostupnih backend instanci nakon " + maxAttempts + " pokusaja.", e);
                }
            } catch (Exception e) {
                //neocekivane gresje
                System.err.println("!!! Doslo je do  greske pri pozivu load balancera: " + e.getMessage());
                throw new RuntimeException("Greška pri pozivu load balancera", e);
            }
        }
        return "Nije se uspjelo pozvati backend nakon " + maxAttempts + " pokusaja.";
    }
}