package org.onlybuns.controller;

import org.onlybuns.DTOs.UserDTO;
import org.onlybuns.exceptions.DoesNotExist.UsernameAlreadyExistsException;
import org.onlybuns.model.User;
import org.onlybuns.service.CommentService;
import org.onlybuns.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;
    private final CommentService commentService;


    UserController(UserService userService, CommentService commentService) {
        this.userService = userService;
        this.commentService = commentService;
    }

    /*@GetMapping("/findUser")
    public ResponseEntity<UserDTO> findByUsername(@RequestParam("username") String username) {
        try {
            return new ResponseEntity<>(new UserDTO(userService.findByUsername(username)), HttpStatus.OK);
        } catch (UsernameAlreadyExistsException e) {
            e.printStackTrace();  // Log exception for more details
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }*/
    @GetMapping("/findUser")
    public ResponseEntity<UserDTO> findByUsername(@RequestParam("username") String username) {
        try {
            User user = userService.findByUsername(username);
            return ResponseEntity.ok(userService.toUserDTO(user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    /*@PutMapping("/follow")
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
    }*/

    @PutMapping("/follow")
    public ResponseEntity<UserDTO> follow(@RequestParam("usernameFollower") String usernameFollower, @RequestParam("usernameFollowing") String usernameFollowing) {
        try {
            UserDTO followedUser = userService.toUserDTO(userService.follow(usernameFollower, usernameFollowing));
            return ResponseEntity.ok(followedUser);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null);
        }
    }

    @PutMapping("/unfollow")
    public ResponseEntity<UserDTO> unfollow(@RequestParam("usernameFollower") String usernameFollower, @RequestParam("usernameFollowing") String usernameFollowing) {
        try {
            UserDTO unfollowedUser = userService.toUserDTO(userService.unfollow(usernameFollower, usernameFollowing));
            return ResponseEntity.ok(unfollowedUser);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null);
        }
    }


    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getUserActivityPercentages() {
        long totalUsers = userService.count();
        long usersWithPosts = userService.countByNumberOfPostsGreaterThan(0);
        long usersWithOnlyComments = commentService.countUsersWithOnlyComments();
        long inactiveUsers = totalUsers - usersWithPosts - usersWithOnlyComments;

        Map<String, Object> response = new HashMap<>();
        response.put("totalUsers", totalUsers);
        response.put("usersWithPosts", (usersWithPosts * 100.0) / totalUsers);
        response.put("usersWithOnlyComments", (usersWithOnlyComments * 100.0) / totalUsers);
        response.put("inactiveUsers", (inactiveUsers * 100.0) / totalUsers);

        return ResponseEntity.ok(response);

    }

}
