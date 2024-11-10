package org.onlybuns.DTOs;

import org.onlybuns.model.Location;
import org.onlybuns.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public class PostCreationDTO {

    private String description;
    private MultipartFile image;
    private Location location;

    public PostCreationDTO(String description, MultipartFile image, Location location) {
        this.description = description;
        this.image = image;
        this.location = location;

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

  /*  public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }*/

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
