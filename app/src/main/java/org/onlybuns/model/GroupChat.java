package org.onlybuns.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "groupchats")
public class GroupChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String groupName;
    private String admin;

/*
    @ElementCollection
    @CollectionTable(name = "users_groupchat", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "member_username")
    private Set<String> members = new HashSet<>();
*/

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "group_id") // Creates a foreign key in `groupMembers`
    private Set<GroupMember> members = new HashSet<>();

    public GroupChat(){
        this.members = new HashSet<GroupMember>();
    }
    public GroupChat(int id, String groupName, String admin, Set<GroupMember> members) {
        this.id = id;
        this.groupName = groupName;
        this.admin = admin;
        this.members = members;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public Set<GroupMember> getMembers() {
        return members;
    }



    public void setMembers(Set<GroupMember> members) {
        this.members = members;
    }
}