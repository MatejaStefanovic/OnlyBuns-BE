package org.messagebroker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.messagebroker.model.*;

@RestController
@RequestMapping("/messages")
public class MessageBrokerController {

    private final Map<String, Map<String, List<Message>>> messageQueues = new ConcurrentHashMap<>();
    private final Set<String> connectedApps = ConcurrentHashMap.newKeySet();

    @PostMapping("/connect")
    public ResponseEntity<String> connect(@RequestParam String appId) {
        connectedApps.add(appId);
        return ResponseEntity.ok("Connected: " + appId);
    }

    @PostMapping("/disconnect")
    public ResponseEntity<String> disconnect(@RequestParam String appId) {
        connectedApps.remove(appId);
        return ResponseEntity.ok("Disconnected: " + appId);
    }

    @PostMapping
    public ResponseEntity<Void> postMessage(@RequestBody MessageRequest request) {
        if (!connectedApps.contains(request.getAppId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        messageQueues
            .computeIfAbsent(request.getAppId(), k -> new ConcurrentHashMap<>())
            .computeIfAbsent(request.getQueueId(), k -> new ArrayList<>())
            .add(request.getMessage());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Message>> getMessages(
            @RequestParam String appId,
            @RequestParam String queueId) {

        Map<String, List<Message>> appQueues = messageQueues.get(appId);
        if (appQueues == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<Message> queue = appQueues.get(queueId);
        if (queue == null || queue.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        // Copy messages to return
        List<Message> messagesToReturn = new ArrayList<>(queue);

        // Clear the queue (consume)
        queue.clear();

        return ResponseEntity.ok(messagesToReturn);
    }
}
