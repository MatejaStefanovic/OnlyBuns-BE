package org.onlybuns.service;

import org.onlybuns.DTOs.PostCreationDTO;
import org.onlybuns.model.Image;
import org.onlybuns.model.Post;
import org.onlybuns.model.User;
import org.onlybuns.repository.PostRepository;
import org.onlybuns.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FileStorageSerivce2 fileStorageService;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository, FileStorageSerivce2 fileStorageService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    public Post createPost(PostCreationDTO postCreationDTO) {
        Post post = new Post();
        post.setDescription(postCreationDTO.getDescription());
        post.setLocation(postCreationDTO.getLocation());
        post.setCreationDateTime(LocalDateTime.now());
        User user = userRepository.findByEmail(postCreationDTO.getEmail());
        post.setUser(user);
        if (postCreationDTO.getImage() != null && !postCreationDTO.getImage().isEmpty()) {
            Image image = fileStorageService.storeFile(postCreationDTO.getImage());
            post.setImage(image);
        }

        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }


}
