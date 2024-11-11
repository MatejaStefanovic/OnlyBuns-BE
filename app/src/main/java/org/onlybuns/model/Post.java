package org.onlybuns.model;

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
    /*@ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;*/

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    public Post() {

        comments = new ArrayList<Comment>();
    }

    public Post(int id, String description, LocalDateTime creationDateTime, Image image, Location location) {
        this.id = id;
        this.description = description;
        this.creationDateTime = creationDateTime;
        this.location = location;
        this.image = image;
        comments = new ArrayList<Comment>();
    }

    //Dodaj ti ako hoces konstruktor koji ima i Komentare u sebi, meni treba ovaj gore bez komentara

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
}


