package org.onlybuns.repository;

import org.onlybuns.model.GroupChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupChatRepository extends JpaRepository<GroupChat, Long> {

    @Query("SELECT g FROM GroupChat g JOIN g.members m WHERE m = :username")
    List<GroupChat> findGroupsByMember(@Param("username") String username);
    List<GroupChat> findAllByAdmin( String username);

    @Query("SELECT g FROM GroupChat g JOIN FETCH g.members WHERE g.id = :groupId")
    Optional<GroupChat> findByIdWithMembers(@Param("groupId") Long groupId);

}
