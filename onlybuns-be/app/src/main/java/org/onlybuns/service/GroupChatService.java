package org.onlybuns.service;

import org.onlybuns.model.GroupChat;
import org.onlybuns.model.GroupMember;
import org.onlybuns.model.Message;
import org.onlybuns.repository.GroupChatRepository;
import org.onlybuns.repository.GroupMemberRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class GroupChatService {

    private final GroupChatRepository groupChatRepository ;
    private final GroupMemberRepository groupMemberRepository ;

    public GroupChatService (GroupChatRepository groupChatRepository, GroupMemberRepository groupMemberRepository){
        this.groupChatRepository = groupChatRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    public GroupChat newGroup(GroupChat group, List<String> users){
        for(String u : users){
            GroupMember gm = new GroupMember(u);
            group.getMembers().add(gm);
        }
        GroupChat g = groupChatRepository.save(group);
        return g;
    }

    public List<GroupChat> getGroupsForUser(String username) {
        List<GroupChat> groups = groupChatRepository.findGroupsByMember(username);
        List<GroupChat> additionalGroups = groupChatRepository.findAllByAdmin(username);
        for (GroupChat g : additionalGroups) {
            groups.add(g);
        }
        return groups;
    }
    public GroupChat findById( long groupId){
        GroupChat group = groupChatRepository.findByIdWithMembers((long) groupId).orElse(null);
        if (group == null) {
            return null;
        }
        return group;
    }

    public GroupChat addMember(long groupId, String username, String adminUsername){
        GroupChat group = groupChatRepository.findByIdWithMembers(groupId).orElse(null);
        if(group.getAdmin().equals(adminUsername)) {
            GroupMember gm = new GroupMember(username);
            group.getMembers().add(gm);
            GroupChat g = groupChatRepository.save(group);
            return g;
        }else{
            System.out.println("Admin username " + group.getAdmin() );
            return null;
        }
    }
    public GroupChat deleteMember(long groupId, String username, String adminUsername){
        GroupChat group = groupChatRepository.findByIdWithMembers(groupId).orElse(null);
        if(!group.getAdmin().equals(adminUsername)) {return null;}
        boolean removed = group.getMembers().removeIf(member -> {
            if (member.getMemberUsername().equals(username)) {
                groupMemberRepository.deleteById(member.getId());
                return true;
            }
            return false;
        });
        GroupChat g = groupChatRepository.save(group);
        return g;
    }
}
