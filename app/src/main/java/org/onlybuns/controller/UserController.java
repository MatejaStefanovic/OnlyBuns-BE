package org.onlybuns.controller;

import org.onlybuns.DTOs.UserDTO;
import org.onlybuns.exceptions.DoesNotExist.UsernameAlreadyExistsException;
import org.onlybuns.model.User;
import org.onlybuns.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/findUser")
    public ResponseEntity<UserDTO> findByUsername(@RequestParam("username") String username) {
        try {
            return new ResponseEntity<>(new UserDTO(userService.findByUsername(username)), HttpStatus.OK);
        } catch (UsernameAlreadyExistsException e) {
            e.printStackTrace();  // Log exception for more details
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/follow")
    public ResponseEntity<UserDTO> follow(@RequestParam("usernameFollower") String usernameFollower, @RequestParam("usernameFollowing") String usernameFollowing) {
        try {
            return new ResponseEntity<>(new UserDTO(userService.follow(usernameFollower,usernameFollowing)), HttpStatus.OK);
        } catch (IllegalStateException e) {
            e.printStackTrace();  // Log exception for more details
            return new ResponseEntity<>(null, HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    @PutMapping("/unfollow")
    public ResponseEntity<UserDTO> unfollow(@RequestParam("usernameFollower") String usernameFollower, @RequestParam("usernameFollowing") String usernameFollowing) {
        try {
            return new ResponseEntity<>(new UserDTO(userService.unfollow(usernameFollower,usernameFollowing)), HttpStatus.OK);
        } catch (IllegalStateException e) {
            e.printStackTrace();  // Log exception for more details
            return new ResponseEntity<>(null, HttpStatus.TOO_MANY_REQUESTS);
        }
    }

}
