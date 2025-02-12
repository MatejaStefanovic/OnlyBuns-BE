package org.onlybuns.controller;


import org.onlybuns.model.GroupChat;
import org.onlybuns.model.Message;
import org.onlybuns.model.Post;
import org.onlybuns.repository.GroupChatRepository;
import org.onlybuns.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin(origins = "http://localhost:3000")
public class MessageController {
    private final SimpMessagingTemplate messagingTemplate;
    private final GroupChatRepository groupChatRepository;

    private final MessageService messageService;
    public MessageController(SimpMessagingTemplate messagingTemplate, GroupChatRepository groupChatRepository, MessageService messageService) {
        this.messagingTemplate = messagingTemplate;
        this.groupChatRepository = groupChatRepository;
        this.messageService = messageService;
    }

    //Grupni chat
    @MessageMapping("/group/{groupId}")
    @SendTo("/group/{groupId}")
    public void sendGroupMessage( Message message) {

        Long groupId = Long.valueOf(message.getReceiverUsername());
        GroupChat group = groupChatRepository.findByIdWithMembers(groupId).orElse(null);

        if (group != null && (  (group.getMembers().stream().anyMatch(member -> member.getMemberUsername().equals(message.getSenderUsername())))|| (group.getAdmin().equals(message.getSenderUsername()))  )) {
            String destination = "/topic/group/" + message.getReceiverUsername();
            messagingTemplate.convertAndSend(destination, message);
            messageService.save(message);
            System.out.println("Grupna poruka za: " + group.getGroupName());
            System.out.println("GRupna poruka naa: " + destination);
            System.out.println("poruka glasi: " + message.getContent());
            System.out.println("poruka od: " + message.getSenderUsername());

        } else {
            System.out.println("Group not found or sender is not a member.");
        }
    }

    //Privatne poruke
    @MessageMapping("/private-chat")
    public void sendPrivateMessage(Message message) {
        if (message.getReceiverUsername() != null) {
            String destination = "/user/" + message.getReceiverUsername() + "/queue/messages";
            System.out.println("Privatna poruka za: " + message.getReceiverUsername());
            System.out.println("Privatna poruka naa: " + destination);
            System.out.println("poruka glasi: " + message.getContent());
            System.out.println("poruka od: " + message.getSenderUsername());
            messagingTemplate.convertAndSend(destination, message);
            messageService.save(message);
        }
    }


}
