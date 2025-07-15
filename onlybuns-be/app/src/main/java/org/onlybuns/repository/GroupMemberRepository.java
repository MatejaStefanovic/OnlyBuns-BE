package org.onlybuns.repository;

import org.onlybuns.model.GroupChat;
import org.onlybuns.model.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMemberRepository  extends JpaRepository<GroupMember, Long>  {

}
