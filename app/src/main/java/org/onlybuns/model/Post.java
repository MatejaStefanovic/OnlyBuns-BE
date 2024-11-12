package org.onlybuns.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private  String description;
    private LocalDateTime creationDateTime;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    private Image image;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likesList;

    private int likes;


    public Post() {
        likesList = new ArrayList<Like>();
        comments = new ArrayList<Comment>();
    }

    public Post(int id, String description, LocalDateTime creationDateTime, Image image, Location location) {
        this.id = id;
        this.description = description;
        this.creationDateTime = creationDateTime;
        this.location = location;
        this.image = image;
        this.comments = new ArrayList<Comment>();
        this.likesList = new ArrayList<Like>();
        this.likes = 0;
    }

    public Post(int id, String description, LocalDateTime creationDateTime, Image image, Location location, List<Comment> comments,List<Like> likesList, int likes) {
        this.id = id;
        this.description = description;
        this.creationDateTime = creationDateTime;
        this.location = location;
        this.image = image;
        this.comments = new ArrayList<Comment>(comments);
        this.likesList = new ArrayList<Like>(likesList);
        this.likes = likes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Like> getLikesList() {
        return likesList;
    }

    public void setLikesList(List<Like> likesList) {
        this.likesList = likesList;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }


   public Image getImage() {
        return image;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public void setImage(Image image) {
        this.image = image;
    }
    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

}


