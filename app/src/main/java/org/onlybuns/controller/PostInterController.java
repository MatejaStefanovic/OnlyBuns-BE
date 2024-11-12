package org.onlybuns.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import org.hibernate.annotations.Parameter;
import org.onlybuns.DTOs.PostCreationDTO;
import org.onlybuns.exceptions.UserRegistration.UnauthorizedUserException;
import org.onlybuns.model.Location;
import org.onlybuns.model.Post;
import org.onlybuns.model.User;
import org.onlybuns.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:3000")
public class PostInterController {

    private final PostService postService;

    @Autowired
    public PostInterController(PostService postService) {
        this.postService = postService;
    }


   /* @Transactional*/
    @PostMapping("/{postId}/like")
    @Operation(summary = "Add a like to a post")
    public ResponseEntity<Post> addLike(@PathVariable int postId, @RequestParam String username,  @RequestParam int flag) {
        try {
            Post updatedPost = postService.addLike(postId, username, flag);
            return new ResponseEntity<>(updatedPost, HttpStatus.OK);
        } catch (UnauthorizedUserException e) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/{postId}/comment")
    @Operation(summary = "Add a comment to a post")
    public ResponseEntity<Post> addComment(@PathVariable int postId, @RequestParam String username, @RequestParam String description) {
        try {
            Post updatedPost = postService.addComment(postId,username, description);
            return new ResponseEntity<>(updatedPost, HttpStatus.OK);
        } catch (UnauthorizedUserException e) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{postId}/delete")
    @Operation(summary = "Delete post")
    public ResponseEntity<Map<String, String>>  deletePost(@PathVariable int postId) {

            postService.deletePost(postId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Deleted");
            return ResponseEntity.ok(response);
    }
    @GetMapping("/all")
    @Operation(summary = "Return list of users")
    public ResponseEntity<List<Post>> getAll() throws IOException {
            return new ResponseEntity<List<Post>>( postService.getAllPosts(), HttpStatus.OK);
    }

}
