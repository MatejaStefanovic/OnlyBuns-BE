package org.rabbitcare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.rabbitcare.model.*;

@Component
public class MessageSender {

    private final List<Map<String, Object>> dataPool = List.of(
        Map.of(
            "type", "vet",
            "location", Map.of("street", "Rocks Lane", "city", "London", "country", "United Kingdom")
        ),
        Map.of(
            "type", "shelter",
            "location", Map.of("street", "Honor Oak Park", "city", "London", "country", "United Kingdom")
        ),
        Map.of(
            "type", "vet",
            "location", Map.of("street", "Parmiter Street", "city", "London", "country", "United Kingdom")
        )
    );

    private final String targetId = "onlybuns";
    private final String queueId = "RCOB";
    private final String baseUrl = "http://localhost:8081/messages";
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void sendMessages() {
        connect();
         
        new Thread(() -> {
            for (Map<String, Object> institution : dataPool) {
                try {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ignored) {}
                    
                    // Serialize data to JSON string
                    String jsonData = objectMapper.writeValueAsString(institution);
                    
                    // Create the inner Message object
                    Message message = new Message();
                    message.setTimestamp(Instant.now().toEpochMilli());
                    message.setData(jsonData);
                    
                    // Create the MessageRequest wrapper
                    MessageRequest messageRequest = new MessageRequest();
                    messageRequest.setSenderId("rabbitCare");
                    messageRequest.setTargetId(targetId);
                    messageRequest.setQueueId(queueId);
                    messageRequest.setMessage(message);
                    
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<MessageRequest> requestEntity = new HttpEntity<>(messageRequest, headers);
                    ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, requestEntity, String.class);
                    
                    System.out.println("Sent message: " + jsonData);
                    System.out.println("Response status: " + response.getStatusCode());
                } catch (Exception e) {
                    System.err.println("Error sending message: " + e.getMessage());
                }
            }
            System.out.println("All messages sent.");
            disconnect();
        }).start();
    }
    
    public void connect() {
        String url = baseUrl + "/connect?appId=rabbitCare";

        HttpHeaders headers = new HttpHeaders();

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        System.out.println("Connection status: " + response.getBody());
    }

    public void disconnect() {
        String url = baseUrl + "/disconnect?appId=rabbitCare";

        HttpHeaders headers = new HttpHeaders();

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        System.out.println("Connection status: " + response.getBody());
    }
}
