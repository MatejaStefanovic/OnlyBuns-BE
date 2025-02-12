package org.onlybuns.service;
import org.onlybuns.model.GroupMember;
import org.onlybuns.repository.GroupMemberRepository;
import org.springframework.stereotype.Service;

@Service
public class GroupMemberService {
    private final GroupMemberRepository groupMemberRepository;
    public GroupMemberService(GroupMemberRepository groupMemberRepository){
        this.groupMemberRepository = groupMemberRepository;
    }


}
