package org.onlybuns.controller;

import org.onlybuns.model.GroupChat;
import org.onlybuns.model.GroupMember;
import org.onlybuns.model.Message;
import org.onlybuns.repository.GroupChatRepository;
import org.onlybuns.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;



@RestController
@RequestMapping("/api/mess")
@CrossOrigin(origins = "http://localhost:3000")
public class MessagesInfoController {

    private final MessageService messageService;
    private final GroupChatRepository groupChatRepository;

    public MessagesInfoController(GroupChatRepository groupChatRepository, MessageService messageService) {
        this.groupChatRepository = groupChatRepository;
        this.messageService = messageService;
    }


    @GetMapping("/senders")
    public ResponseEntity<Set<String>> getMessagesSenders(@RequestParam("username") String username){
        Set< String> senders = messageService.getAllSendersForUser(username);
        return ResponseEntity.ok(senders);
    }

    @GetMapping("/latest")
    public ResponseEntity<Message> getMessagesSenders(@RequestParam("sender") String sender, @RequestParam("receiver") String receiver){
        Message m = messageService.getLastMessageSent(sender, receiver);
        return ResponseEntity.ok(m);
    }

    @GetMapping("/previousMessages")
    public ResponseEntity<List<Message>> getPreviousMessages(@RequestParam("sender") String sender, @RequestParam("receiver") String receiver){
        List<Message> m = messageService.getPreviousMessages(sender, receiver);
        return ResponseEntity.ok(m);
    }
    @PostMapping("/read")
    public ResponseEntity<Boolean> markasRead(@RequestParam("sender") String sender, @RequestParam("receiver") String receiver){
        messageService.markRead(sender, receiver);
        return ResponseEntity.ok(true);
    }
    @PutMapping("/newGroup")
    public ResponseEntity<GroupChat> newGroup(@RequestBody GroupChat group, @RequestParam List<String> users){
        for(String u : users){
            GroupMember gm = new GroupMember(u);
            group.getMembers().add(gm);
        }
        GroupChat g = groupChatRepository.save(group);
        return ResponseEntity.ok(g);
    }
    @GetMapping("/groupsForUser")
    public ResponseEntity<List<GroupChat>> getGroupsForUser(@RequestParam("username") String username){
        List<GroupChat> groups = groupChatRepository.findGroupsByMember(username);
        List<GroupChat> additionalGroups = groupChatRepository.findAllByAdmin(username);
        for (GroupChat g : additionalGroups){
            groups.add(g);
        }
        return ResponseEntity.ok(groups);
    }
/*
    @GetMapping("/groupMessages")
    public ResponseEntity<List<Message>> getMessagesInGroupChat(@RequestParam("groupId") int groupId,@RequestParam("username") String username){
        List<Message> m = messageService.getAllByReceiverUsername(String.valueOf(groupId));
        return ResponseEntity.ok(m);
    }*/

    @GetMapping("/groupMessages")
    public ResponseEntity<List<Message>> getMessagesInGroupChat(
            @RequestParam("groupId") int groupId,
            @RequestParam("username") String username) {

        GroupChat group = groupChatRepository.findByIdWithMembers((long) groupId).orElse(null);
        if (group == null) {
            return ResponseEntity.notFound().build();
        }

        GroupMember member = group.getMembers()
                .stream()
                .filter(m -> m.getMemberUsername().equals(username))
                .findFirst()
                .orElse(null);
        if ((member==null) && group.getAdmin().equals(username)){
            List<Message> m = messageService.getAllByReceiverUsername(String.valueOf(groupId));
            return ResponseEntity.ok(m);
        }
        LocalDateTime joinDate = member.getJoinedAt();

        List<Message> allMessages = messageService.getAllByReceiverUsername(String.valueOf(groupId));


        List<Message> afterJoinDate = allMessages.stream()
                .filter(msg -> msg.getTime().isAfter(joinDate))
                .toList();

        List<Message> beforeJoinDate = allMessages.stream()
                .filter(msg -> msg.getTime().isBefore(joinDate))
                .sorted(Comparator.comparing(Message::getTime).reversed()) // Sort descending
                .limit(10) // Keep only last 10 messages before join date
                .toList();
        List<Message> finalMessages = new ArrayList<>();
        finalMessages.addAll(beforeJoinDate);
        finalMessages.addAll(afterJoinDate);
        finalMessages = finalMessages.stream()
                .sorted(Comparator.comparing(Message::getTime))
                .toList();


        return ResponseEntity.ok(finalMessages);
    }

    @GetMapping("/addMember")
    public ResponseEntity<GroupChat> getMessagesInGroupChat(@RequestParam("username") String username, @RequestParam("groupId") Long groupId){
        GroupChat group = groupChatRepository.findByIdWithMembers(groupId).orElse(null);
        GroupMember gm = new GroupMember(username);
        group.getMembers().add(gm);
        GroupChat g= groupChatRepository.save(group);
        return ResponseEntity.ok(g);
    }

}
