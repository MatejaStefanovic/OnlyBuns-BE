package org.onlybuns.DTOs;

public class AuthResponseDTO {
	private String token;  // JWT Token

    private UserDTO user;

    public AuthResponseDTO(String token, UserDTO user) {
		this.token = token;
		this.user = user;
	}
    
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

}
