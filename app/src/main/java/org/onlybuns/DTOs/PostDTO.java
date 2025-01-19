package org.onlybuns.DTOs;

import org.onlybuns.model.Location;
import org.onlybuns.model.Post;
import org.onlybuns.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PostDTO {

    private int id;
    private String description;
    private LocalDateTime creationDateTime;
    private String username; // Only include the username of the user
    private int likes;
    private Location locationName; // Location name or identifier
    private List<String> followers;

    public PostDTO(Post post) {
        this.id = post.getId();
        this.description = post.getDescription();
        this.creationDateTime = post.getCreationDateTime();
        this.username = post.getUser().getUsername(); // Map only the username
        this.likes = post.getLikes();
        this.locationName = post.getLocation();// Map location name
        this.followers = post.getUser() != null
                ? post.getUser().getFollowers().stream()
                .map(User::getUsername)
                .collect(Collectors.toList())
                : List.of();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public Location getLocationName() {
        return locationName;
    }

    public void setLocationName(Location locationName) {
        this.locationName = locationName;
    }
}
