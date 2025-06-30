package org.onlybuns.controller;


import org.onlybuns.component.MessageRabbitSender;
import org.onlybuns.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/advertisiment/posts")
@CrossOrigin(origins = "http://localhost:3000")
public class PostAdvertisimentController {


    private final PostService postService;


    @Autowired
    public PostAdvertisimentController(PostService postService, MessageRabbitSender messageRabbitSender) {
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
