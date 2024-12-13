package org.onlybuns.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.onlybuns.DTOs.LoginRequestDTO;
import org.onlybuns.DTOs.AuthResponseDTO;
import org.onlybuns.DTOs.UserDTO;
import org.onlybuns.DTOs.LocationDTO;
import org.onlybuns.DTOs.ResponseDTO;
import org.onlybuns.exceptions.Security.InvalidTokenException;
import org.onlybuns.exceptions.UserRegistration.*;
import org.onlybuns.model.User;
import org.onlybuns.service.UserLoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:3000")
public class UserLoginController {

    private final UserLoginService userLoginService;

    UserLoginController(UserLoginService userLoginService){
        this.userLoginService = userLoginService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Register a new user by providing their details")
    @ApiResponse(responseCode = "200", description = "User registered successfully")
    public ResponseEntity<ResponseDTO> registerUser(@Valid @RequestBody User user) {
        try {
            userLoginService.registerUser(user);
            
            ResponseDTO response = new ResponseDTO("User registered successfully");
            		
            return new ResponseEntity<ResponseDTO>(response, HttpStatus.CREATED);
        } catch (UsernameAlreadyExistsException | EmailAlreadyExistsException e) {
            return new ResponseEntity<ResponseDTO>(new ResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login to the system", description = "Authenticate user using username and password")
    @ApiResponse(responseCode = "200", description = "User logged in successfully")
    @ApiResponse(responseCode = "400", description = "Unauthorized user")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public ResponseEntity<Object> loginUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            String JWT = userLoginService.loginUser(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
            User user = userLoginService.getUserByEmail(loginRequestDTO.getEmail());
            UserDTO userDTO = new UserDTO(
            	    user.getUsername(),       
            	    user.getFirstName(),          
            	    user.getLastName(),       
            	    user.getEmail(),              
            	    user.isActivated(),          
            	    new LocationDTO(user.getLocation()),      
            	    user.getRole().name()

            );
            AuthResponseDTO authResponseDTO = new AuthResponseDTO(JWT,  userDTO);
            return new ResponseEntity<>(authResponseDTO, HttpStatus.OK);
            
        } catch (InvalidCredentialsException  e) {
            return new ResponseEntity<>(new ResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (UnauthorizedUserException e){
            return new ResponseEntity<>(new ResponseDTO(e.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateUser(@RequestParam("token") String token) {
        System.out.println("Received token: " + token);  // Log token received
        try {
            userLoginService.activateUser(token);          		
            return ResponseEntity.ok("Account activated successfully.");
        } catch (InvalidTokenException e) {
            e.printStackTrace();  // Log exception for more details
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token.");
        }
    }
}
