package org.onlybuns.controller;


import org.onlybuns.model.GroupChat;
import org.onlybuns.model.Message;
import org.onlybuns.repository.GroupChatRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@CrossOrigin(origins = "http://localhost:3000")
public class MessageController {
    private final SimpMessagingTemplate messagingTemplate;
    private final GroupChatRepository groupChatRepository;
    public MessageController(SimpMessagingTemplate messagingTemplate, GroupChatRepository groupChatRepository) {
        this.messagingTemplate = messagingTemplate;
        this.groupChatRepository = groupChatRepository;
    }

    //Grupni chat
    @MessageMapping("/group/{groupId}")
    public void sendGroupMessage( Message message) {

        Long groupId = Long.valueOf(message.getReceiverUsername());
        GroupChat group = groupChatRepository.findById(groupId).orElse(null);

        if (group != null && group.getMembers().contains(message.getSenderUsername())) {
            String destination = "/group/" + message.getReceiverUsername();
            messagingTemplate.convertAndSend(destination, message);
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
            messagingTemplate.convertAndSend(destination, message);
        }
    }


}
