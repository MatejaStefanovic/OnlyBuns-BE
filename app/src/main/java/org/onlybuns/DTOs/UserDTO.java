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

    public UserDTO(String username, String firstName, String lastName, String email, boolean isActivated, LocationDTO location, String role) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.isActivated = isActivated;
        this.location = location;
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
}
