package org.onlybuns.controller;


import org.onlybuns.DTOs.PostCreationDTO;
import org.onlybuns.component.MessageSender;
import org.onlybuns.model.Location;
import org.onlybuns.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/advertisiment/posts")
@CrossOrigin(origins = "http://localhost:3000")
public class PostAdvertisimentController {


    private final PostService postService;


    @Autowired
    public PostAdvertisimentController(PostService postService, MessageSender messageSender) {
        this.postService = postService;

    }

    @PutMapping(value = "/markForAds/{postId}")
    public ResponseEntity<Map<String, String>> markPostForAds(@PathVariable Long postId) throws IOException {

        postService.updateSuitable(postId, true);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Marked");
        return ResponseEntity.ok(response);
    }


}
