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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    private Image image;

    @ElementCollection
    @CollectionTable(name = "users_groupchat", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "member_username")
    private Set<String> members = new HashSet<>();

    public GroupChat(int id, String groupName, String admin, Set<String> members, Image image) {
        this.id = id;
        this.groupName = groupName;
        this.admin = admin;
        this.members = members;
        this.image = image;
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

    public Set<String> getMembers() {
        return members;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setMembers(Set<String> members) {
        this.members = members;
    }
}