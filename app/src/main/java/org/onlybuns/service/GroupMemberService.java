package org.onlybuns.service;
import org.onlybuns.model.GroupChat;
import org.onlybuns.model.GroupMember;
import org.onlybuns.repository.GroupMemberRepository;
import org.springframework.stereotype.Service;

@Service
public class GroupMemberService {
    private final GroupMemberRepository groupMemberRepository;
    public GroupMemberService(GroupMemberRepository groupMemberRepository){
        this.groupMemberRepository = groupMemberRepository;
    }

public GroupMember getMemberByUsername(GroupChat group, String username){
    GroupMember member = group.getMembers()
            .stream()
            .filter(m -> m.getMemberUsername().equals(username))
            .findFirst()
            .orElse(null);
    return member;
}
}
