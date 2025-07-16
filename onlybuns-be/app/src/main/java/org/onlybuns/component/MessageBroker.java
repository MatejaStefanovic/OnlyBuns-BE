package org.onlybuns.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Map;


@Component
public class MessageBroker {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "http://localhost:8081/messages";

    @PostConstruct
    public void init() {
        connect();
    }

    public void connect() {
        String url = baseUrl + "/connect?appId=onlybuns";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            System.out.println("Connection status: " + response.getBody());
        } catch (Exception e) {
            System.err.println("Connection status: " + e.getMessage());
        }
    }

    public void disconnect() {
        String url = baseUrl + "/disconnect?appId=onlybuns";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            System.out.println("Connection status:" + response.getBody());
        } catch (Exception e) {
            System.err.println("Connection status:" + e.getMessage());
        }
    }
}
