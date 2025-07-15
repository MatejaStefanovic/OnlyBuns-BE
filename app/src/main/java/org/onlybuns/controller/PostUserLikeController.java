package org.onlybuns.controller;

import org.onlybuns.model.PostLikeUser;
import org.onlybuns.repository.PostLikeUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post-like-users")
@CrossOrigin(origins = "http://localhost:3000")
public class PostUserLikeController {


    @Autowired
    private PostLikeUserRepository postLikeUserRepository;

    @GetMapping("/post/{postId}/count")
    public long getLikesByPostId(@PathVariable Long postId) {
        long count = postLikeUserRepository.countLikesByPostId(postId);
        return count;
    }

    @GetMapping("/all")
    public List<PostLikeUser> getAllPostLikes() {
        List<PostLikeUser> lisa = postLikeUserRepository.findAll();
         return lisa;
    }




}
