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
    public ResponseEntity<String> loginUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            userLoginService.loginUser(loginRequestDTO.getUsername(), loginRequestDTO.getPassword());
            return new ResponseEntity<>("User logged in successfully", HttpStatus.OK);
        } catch (UsernameAlreadyExistsException | EmailAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateUser(@RequestParam("token") String token) {
        try {
            userLoginService.activateUser(token);
            return ResponseEntity.ok("Account activated successfully.");
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token.");
        }
    }
}
