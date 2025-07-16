package org.messagebroker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.nio.charset.StandardCharsets;
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
        if (connectedApps.contains(appId))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error - App already connected: " + appId);
        
        connectedApps.add(appId);
        return ResponseEntity.ok("Connected: " + appId);
    }
    
    @PostMapping("/disconnect")
    public ResponseEntity<String> disconnect(@RequestParam String appId) {
        if (!connectedApps.contains(appId))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error - app is not connected: " + appId);
        
        connectedApps.remove(appId);
        return ResponseEntity.ok("Disconnected: " + appId);
    }
    
    @PostMapping
    public ResponseEntity<String> postMessage(@RequestBody MessageRequest request) {
        String senderAppId = request.getSenderId();
        String targetAppId = request.getTargetId();
        
        if (!connectedApps.contains(senderAppId))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error - sender isn't connected: " + senderAppId);
        
        if (senderAppId.equals(targetAppId))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error - cannot send message to the same app");
        
        String channelKey = createChannelKey(senderAppId, targetAppId);
        
        messageQueues
            .computeIfAbsent(channelKey, k -> new ConcurrentHashMap<>())
            .computeIfAbsent(request.getQueueId(), k -> new ArrayList<>())
            .add(request.getMessage());
        
        return ResponseEntity.ok().body("Success");
    }
    
    @GetMapping
    public ResponseEntity<MessageResponse> getMessages(
            @RequestParam String targetId,
            @RequestParam String senderId,
            @RequestParam String queueId) {

        if (!connectedApps.contains(targetId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new MessageResponse("Error - recipient is not connected: " + targetId, 
                                                                        Collections.emptyList()));
        }

        if (targetId.equals(senderId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new MessageResponse("Error - cannot retrieve messages from the same app",
                                                                        Collections.emptyList()));
        }

        String channelKey = createChannelKey(senderId, targetId);
        Map<String, List<Message>> channelQueues = messageQueues.get(channelKey);

        if (channelQueues == null) {
            return ResponseEntity.ok(new MessageResponse("No messages found", Collections.emptyList()));
        }

        List<Message> queue = channelQueues.get(queueId);
        if (queue == null || queue.isEmpty()) {
            return ResponseEntity.ok(new MessageResponse("Queue is empty", Collections.emptyList()));
        }

        List<Message> messagesToReturn = new ArrayList<>(queue);
        queue.clear();

        return ResponseEntity.ok(new MessageResponse("Success", messagesToReturn));
    }
    
    private String createChannelKey(String app1, String app2) {
         return app1 + "->" + app2;
    }

}
