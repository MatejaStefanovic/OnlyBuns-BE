package org.onlybuns.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.onlybuns.exceptions.UserRegistration.EmailAlreadyExistsException;
import org.onlybuns.exceptions.UserRegistration.UnauthorizedUserException;
import org.onlybuns.exceptions.UserRegistration.UsernameAlreadyExistsException;
import org.onlybuns.model.User;
import org.onlybuns.service.UserLoginService;
import org.onlybuns.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminViewController {

    private final UserService userService;

    AdminViewController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/users")
    @Operation(summary = "Return list of users")
    public ResponseEntity<List<User>> getAll() {
        try {
            return new ResponseEntity( userService.findAll(), HttpStatus.OK);
        }  catch (UnauthorizedUserException e){
        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
    }
    }
}
