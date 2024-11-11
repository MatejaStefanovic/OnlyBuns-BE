package org.onlybuns.DTOs;

import org.onlybuns.model.Location;
import org.onlybuns.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public class PostCreationDTO {

    private String description;
    private MultipartFile image;
    private Location location;
    private String email;

    public PostCreationDTO(String description, MultipartFile image, Location location, String email) {
        this.description = description;
        this.image = image;
        this.location = location;
        this.email = email;

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
