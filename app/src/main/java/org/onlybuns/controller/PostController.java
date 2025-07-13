package org.onlybuns.controller;


import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import org.hibernate.annotations.Parameter;
import org.onlybuns.DTOs.LoginRequestDTO;
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
import io.micrometer.core.instrument.Timer;

@RestController
@RequestMapping("/api/post")
@CrossOrigin(origins = "http://localhost:3000")
public class PostController {

    private final PostService postService;
    private final Timer createPostTimer;

    @Autowired
    public PostController(PostService postService, MeterRegistry meterRegistry) {

        this.postService = postService;
        this.createPostTimer = Timer.builder("http_requests_create_post")
                .description("Time taken to create a new post")
                .register(meterRegistry);

    }


    @PutMapping(value = "/update/{postId}", consumes = {"multipart/form-data"})
    public ResponseEntity<Map<String, String>> updatePost(@PathVariable Long postId,
                                                          @RequestPart("description") String description,
                                                          @RequestPart("image") MultipartFile image,
                                                          @RequestPart("city") String city,
                                                          @RequestPart("country") String country,
                                                          @RequestPart("street") String street,
                                                          @RequestPart("email") String email) throws IOException {
        PostCreationDTO postDTO = new PostCreationDTO(description, image, new Location(country, street, city), email);
        postService.updatePost(postId, postDTO);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Updated");
        return ResponseEntity.ok(response);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Map<String, String>> createPost(@RequestPart("description") String description,
                                                          @RequestPart("image") MultipartFile image,
                                                          @RequestPart("city") String city,
                                                          @RequestPart("country") String country,
                                                          @RequestPart("street") String street,
                                                          @RequestPart("email") String email) throws IOException {
        Map<String, String> response = new HashMap<>();

        createPostTimer.record(() -> {
            try {
                postService.createPost(new PostCreationDTO(description, image, new Location(country, street, city), email, false));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        response.put("message", "Created");
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() throws IOException {
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/userPosts")
    public ResponseEntity<List<Post>> getPostsFromUser(@RequestParam String email) throws IOException {
        // Retrieve posts using the user's email

        List<Post> posts = postService.getPostsFromUser(email);
        return ResponseEntity.ok(posts);
    }
    @GetMapping("/trending/lastWeek")
    public ResponseEntity<List<Post>> getTop5PostsLast7Days() {
        List<Post> posts = postService.getTopFivePostsLastWeek();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/weekly")
    public ResponseEntity<Integer> getWeeklyStatistic() throws IOException {

        int stat = postService.getPostStatisticsWeekly();
        return ResponseEntity.ok(stat);
    }

    @GetMapping("/monthly")
    public ResponseEntity<Integer> getMonthlyStatistic() throws IOException {

        int stat = postService.getPostStatisticsMonthly();
        return ResponseEntity.ok(stat);
    }

    @GetMapping("/yearly")
    public ResponseEntity<Integer> getYearlyStatistic() throws IOException {

        int stat = postService.getPostStatisticsYearly();
        return ResponseEntity.ok(stat);
    }

    @GetMapping("/trending/allTime")
    public ResponseEntity<List<Post>>  getTop10PostsAllTime() {
        List<Post> posts = postService.getTopTenPostsAllTime();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/trending/users/lastWeek")
    public ResponseEntity<List<Object[]>> getTop10UsersLastWeek() {
        List<Object[]> users = postService.getTopTenUsersThatLikedMost();
        return ResponseEntity.ok(users);
    }
}
