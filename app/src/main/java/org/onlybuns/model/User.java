package org.onlybuns.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.onlybuns.enums.UserRole;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(min = 3, max = 16)
    @Column(unique = true)
    private String username;

    @NotNull
    @Size(min = 8, max = 128)
    private String password;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private Integer numberOfPosts = 0;

    @NotNull
    private Integer numberOfFollowing = 0;

    @Email
    @Column(unique = true)
    private String email;

    @JsonProperty("isActivated")
    private boolean isActivated;

    @NotNull
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "location_id")
    private Location location;

    @Enumerated(EnumType.STRING)  // Store the enum as a string in the database
    private UserRole role;
    
    public User() {
    }

    public User(UserRole role, Location location, boolean isActivated, String email, String lastName, String firstName, String password, String username, Integer numberOfFollowing, Integer numberOfPosts) {
        this.role = role;
        this.location = location;
        this.isActivated = isActivated;
        this.email = email;
        this.lastName = lastName;
        this.firstName = firstName;
        this.password = password;
        this.username = username;
        this.numberOfFollowing = numberOfFollowing;
        this.numberOfPosts = numberOfPosts;
    }

    public Integer getNumberOfPosts() {return numberOfPosts; }

    public void setNumberOfPosts(Integer number){ numberOfPosts = number;}

    public Integer getNumberOfFollowing() {return numberOfFollowing;}

    public void setNumberOfFollowing(Integer number) {numberOfFollowing = number;}

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
