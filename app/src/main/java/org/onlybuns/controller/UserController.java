package org.onlybuns.controller;

import org.onlybuns.DTOs.UserDTO;
import org.onlybuns.exceptions.DoesNotExist.UsernameAlreadyExistsException;
import org.onlybuns.model.User;
import org.onlybuns.service.CommentService;
import org.onlybuns.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @GetMapping("/following")
    public ResponseEntity<List<String>> getAllFollowingsForUser(@RequestParam("username") String username){
        User user = userService.findByUsername(username);
        List<User> users = userService.findAll();
        List<String> followings = new ArrayList<>();
        users.remove(user);
        for (User u : users){
            boolean isFollower = u.getFollowers().stream()
                    .anyMatch(follower -> follower.equals(user.getUsername()));
            if (isFollower) {
                followings.add(u.getUsername());
            }
           /* if( u.getFollowers().contains(user.getUsername())){
                followings.add(u.getUsername());
            }*/
        }
        return new ResponseEntity<>(followings, HttpStatus.OK);
    }


    @GetMapping("/followers")
    public ResponseEntity<List<String>> getAllFollowersForUser(@RequestParam("username") String username){
        User user = userService.findByUsername(username);
        List<User> users = userService.findAll();
        List<String> followings = new ArrayList<>();
        users.remove(user);
        for (User u : users){
            boolean isFollowing = u.getFollowing().stream()
                    .anyMatch(following -> following.equals(user.getUsername()));
            if (isFollowing) {
                followings.add(u.getUsername());
            }
           /* if( u.getFollowers().contains(user.getUsername())){
                followings.add(u.getUsername());
            }*/
        }
        return new ResponseEntity<>(followings, HttpStatus.OK);
    }


    @PutMapping("/update/{email}")
    public ResponseEntity<User> updateUser(@PathVariable String email, @RequestBody User user) {
        userService.updateUser(email, user);
        return ResponseEntity.ok().build();
    }
}
