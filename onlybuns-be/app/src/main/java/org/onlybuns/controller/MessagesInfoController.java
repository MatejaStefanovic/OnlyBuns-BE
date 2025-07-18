package org.onlybuns.controller;

import org.onlybuns.model.GroupChat;
import org.onlybuns.model.GroupMember;
import org.onlybuns.model.Message;
import org.onlybuns.repository.GroupChatRepository;
import org.onlybuns.service.GroupChatService;
import org.onlybuns.service.GroupMemberService;
import org.onlybuns.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/mess")
@CrossOrigin(origins = "http://localhost:3000")
public class MessagesInfoController {

    private final MessageService messageService;
    private final GroupChatService groupChatService;
    private final GroupMemberService groupMemberService;

    public MessagesInfoController(GroupMemberService groupMemberService, GroupChatService groupChatService, MessageService messageService) {
        this.groupChatService = groupChatService;
        this.messageService = messageService;
        this.groupMemberService = groupMemberService;
    }


    @GetMapping("/senders")
    public ResponseEntity<Set<String>> getMessagesSenders(@RequestParam("username") String username){
        Set< String> senders = messageService.getAllSendersForUser(username);
        return ResponseEntity.ok(senders);
    }

    @GetMapping("/latest")
    public ResponseEntity<Message> getLastMessage(@RequestParam("sender") String sender, @RequestParam("receiver") String receiver){
        Message m = messageService.getLastMessageSent(sender, receiver);
        return ResponseEntity.ok(m);
    }

    @GetMapping("/grouplatest")
    public ResponseEntity<Message> getLastGroupMessage( @RequestParam("receiver") String receiver){
        Message m = messageService.getLastGroupMessageSent(receiver);
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
        GroupChat g = groupChatService.newGroup(group,users);
        return ResponseEntity.ok(g);
    }
    @GetMapping("/groupsForUser")
    public ResponseEntity<List<GroupChat>> getGroupsForUser(@RequestParam("username") String username){
        List<GroupChat> groups = groupChatService.getGroupsForUser(username);
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/groupMessages")
    public ResponseEntity<List<Message>> getMessagesInGroupChat(
            @RequestParam("groupId") int groupId,
            @RequestParam("username") String username) {
        GroupChat group = groupChatService.findById((long) groupId);
        if (group == null) {
            return ResponseEntity.notFound().build();
        }
        GroupMember member = groupMemberService.getMemberByUsername(group,username);
        if ((member==null) && group.getAdmin().equals(username)){
            List<Message> m = messageService.getAllByReceiverUsername(String.valueOf(groupId));
            return ResponseEntity.ok(m);
        }
        LocalDateTime joinDate = member.getJoinedAt();
        List<Message> finalMessages = messageService.getGroupMessagesByJoiningDate(joinDate , groupId);
        return ResponseEntity.ok(finalMessages);
    }

    @GetMapping("/addMember")
    public ResponseEntity<GroupChat> addMemberInGroupChat(@RequestParam("username") String username, @RequestParam("groupId") Long groupId, @RequestParam("adminUsername") String adminUsernam){
        GroupChat group = groupChatService.addMember(groupId, username,adminUsernam);
        System.out.println("Received group " + group );

        if (group == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(group);
    }

    @GetMapping("/deleteMember")
    public ResponseEntity<GroupChat> deleteGroupChatMember (@RequestParam("username") String username, @RequestParam("groupId") Long groupId, @RequestParam("adminUsername") String adminUsername){
        GroupChat group = groupChatService.deleteMember(groupId, username,adminUsername);
        System.out.println("Received group " + group );
        if (group == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(group);
    }

    @GetMapping("/getGroupMembers")
    public ResponseEntity<List<String>>getMembers(@RequestParam("groupId") Long groupId){
        GroupChat group = groupChatService.findById(groupId);
        List<String> members = group.getMembers()
                .stream()
                .map(GroupMember::getMemberUsername)
                .collect(Collectors.toList());
        return ResponseEntity.ok(members);
    }
}
