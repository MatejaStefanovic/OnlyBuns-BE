package org.onlybuns.controller;


import io.swagger.v3.oas.annotations.media.Content;
import org.hibernate.annotations.Parameter;
import org.onlybuns.DTOs.PostCreationDTO;
import org.onlybuns.model.Location;
import org.onlybuns.model.Post;
import org.onlybuns.model.User;
import org.onlybuns.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/post")
@CrossOrigin(origins = "http://localhost:3000")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }



    @PostMapping(consumes = {"multipart/form-data"})
    public Post createPost(@RequestPart("description") String description,
                           @RequestPart("image") MultipartFile image,
                           @RequestPart("city") String city,
                           @RequestPart("country") String country,
                           @RequestPart("street") String street,
                           @RequestPart("email") String email
                           ) {

            return postService.createPost(new PostCreationDTO(description,image, new Location(country,street,city),email));
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }
}
