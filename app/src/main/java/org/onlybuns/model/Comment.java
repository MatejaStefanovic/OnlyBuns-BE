package org.onlybuns.model;


import jakarta.persistence.*;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private  String description;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true) // Optional relationship to the user who commented
    private User user;

    public Comment() {}

    public Comment(int id, String description, Post post, User user) {
        this.id = id;
        this.description = description;
        this.post = post;
        this.user = user;
    }

    public int getId() {
        return id;
    }
    public String getDescription() {
        return description;
    }
    public Post getPost() {
        return post;
    }
    public User getUser() {
        return user;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setPost(Post post) {
        this.post = post;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
