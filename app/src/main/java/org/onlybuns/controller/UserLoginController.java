package org.onlybuns.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.onlybuns.DTOs.LoginRequestDTO;
import org.onlybuns.exceptions.Security.InvalidTokenException;
import org.onlybuns.exceptions.UserRegistration.*;
import org.onlybuns.model.User;
import org.onlybuns.service.UserLoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserLoginController {

    private final UserLoginService userLoginService;

    UserLoginController(UserLoginService userLoginService){
        this.userLoginService = userLoginService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Register a new user by providing their details")
    @ApiResponse(responseCode = "200", description = "User registered successfully")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
        try {
            userLoginService.registerUser(user);
            return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
        } catch (UsernameAlreadyExistsException | EmailAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login to the system", description = "Authenticate user using username and password")
    @ApiResponse(responseCode = "200", description = "User logged in successfully")
    @ApiResponse(responseCode = "400", description = "Unauthorized user")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public ResponseEntity<String> loginUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            userLoginService.loginUser(loginRequestDTO.getUsername(), loginRequestDTO.getPassword());
            return new ResponseEntity<>("User logged in successfully", HttpStatus.OK);
        } catch (InvalidCredentialsException  e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (UnauthorizedUserException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
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
