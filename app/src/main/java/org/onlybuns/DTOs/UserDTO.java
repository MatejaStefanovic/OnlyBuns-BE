package org.onlybuns.DTOs;

import org.onlybuns.model.User;

public class UserDTO {
	
	private String username;
    private String firstName;
    private String lastName;
    private String email;
    private boolean isActivated;
    private LocationDTO location;
    private String role;
    private int numberOfPosts = 0;
    private int numberOfFollowing = 0;

    public UserDTO(String username, String firstName, String lastName, String email, boolean isActivated, LocationDTO location, String role) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.isActivated = isActivated;
        this.location = location;
        this.role = role;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

	public void setUsername(String username) {
	    this.username = username;
	}
	
	public String getFirstName() {
	    return firstName;
	}
	
	public void setFirstName(String firstName) {
	    this.firstName = firstName;
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
	
	public boolean isActivated() {
	    return isActivated;
	}
	
	public void setActivated(boolean activated) {
	    isActivated = activated;
	}

     public int getNumberOfPosts() {
        return numberOfPosts;
    }
    public void setNumberOfPosts(int numberOfPosts) {
        this.numberOfPosts = numberOfPosts;
    }
    public int getNumberOfFollowing() {
        return numberOfFollowing;
    }
    public void setNumberOfFollowing(int numberOfFollowing) {
        this.numberOfFollowing = numberOfFollowing;
    }
 
}

   