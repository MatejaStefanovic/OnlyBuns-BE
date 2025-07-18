package org.onlybuns.model;


import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "groupMembers")
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_username", nullable = false)
    private String memberUsername;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    public GroupMember() {
        this.joinedAt = LocalDateTime.now(); // Default to current time
    }

    public GroupMember( String memberUsername) {
        this.memberUsername = memberUsername;
        this.joinedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }


    public String getMemberUsername() {
        return memberUsername;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setMemberUsername(String memberUsername) {
        this.memberUsername = memberUsername;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}
