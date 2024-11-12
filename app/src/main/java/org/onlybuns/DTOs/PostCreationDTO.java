package org.onlybuns.DTOs;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import org.onlybuns.model.Comment;
import org.onlybuns.model.Like;
import org.onlybuns.model.Location;
import org.onlybuns.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostCreationDTO {

    private String description;
    private MultipartFile image;
    private Location location;
    private String email;
    private List<Comment> comments;
    private List<Like> likesList;
    private int likes;

    public PostCreationDTO(String description, MultipartFile image, Location location, String email) {
        this.description = description;
        this.image = image;
        this.location = location;
        this.email = email;
    }

    public PostCreationDTO(String description, MultipartFile image, Location location, String email, int likes, List<Comment> c, List<Like> l) {
        this.description = description;
        this.image = image;
        this.location = location;
        this.email = email;
        this.comments = c;
        this.likes = likes;
        this.likesList = l;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
