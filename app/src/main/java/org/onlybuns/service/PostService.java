package org.onlybuns.service;

import jakarta.transaction.Transactional;
import org.onlybuns.DTOs.PostCreationDTO;
import org.onlybuns.model.*;
import org.onlybuns.repository.CommentRepository;
import org.onlybuns.repository.LikeRepository;
import org.onlybuns.repository.PostRepository;
import org.onlybuns.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FileStorageSerivce fileStorageService;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository, FileStorageSerivce fileStorageService, LikeRepository likeRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
    }
    public Post updatePost(Long postId, PostCreationDTO postDTO) throws IOException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found for ID: " + postId));

        post.setDescription(postDTO.getDescription());
        post.setLocation(postDTO.getLocation());

        if (postDTO.getImage() != null && !postDTO.getImage().isEmpty()) {
            Image updatedImage = fileStorageService.storeFile(postDTO.getImage());
            fileStorageService.getImageBase64ForImage(updatedImage);
            post.setImage(updatedImage);
        }

        return postRepository.save(post);
    }

    public Post createPost(PostCreationDTO postCreationDTO) throws IOException {
        Post post = new Post();
        post.setDescription(postCreationDTO.getDescription());
        post.setLocation(postCreationDTO.getLocation());
        post.setCreationDateTime(LocalDateTime.now());
        User user = userRepository.findByEmail(postCreationDTO.getEmail());
        post.setUser(user);
        if (postCreationDTO.getImage() != null && !postCreationDTO.getImage().isEmpty()) {
            Image image = fileStorageService.storeFile(postCreationDTO.getImage());
            fileStorageService.getImageBase64ForImage(image);
            post.setImage(image);
        }

        return postRepository.save(post);
    }

    public List<Post> getAllPosts() throws IOException {
        return postRepository.findAll();
    }

    public List<Post> getPostsFromUser(String email) throws IOException {
        User user = userRepository.findByEmail(email);
        return postRepository.findAllByUser(user);
    }

   public void deletePost(long postId){
       Post post = postRepository.findById(postId)
               .orElseThrow(() -> new IllegalArgumentException("Post not found for ID: " + postId));

       postRepository.delete(post);
   }
    /*public Post addLike(long postId, String username, int flag) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found for ID: " + postId));

        if (post.getUser() == null) {
            throw new IllegalStateException("Post does not have a user assigned.");
        }

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found for username: " + username);
        }
        if (flag==1) {post.setLikes(post.getLikes() + 1);}
        if(flag==-1){post.setLikes(post.getLikes() - 1);}
        Like like = new Like(user, post);
        post.getLikesList().add(like);

        likeRepository.save(like);
        return postRepository.save(post);
    }*/

   /* public Post addComment(long postId, String username, String description) {


        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found for username: " + username);
        }
        if(checkIfUserCanComment(user)){
            Comment comment = new Comment();
            comment.setUser(user);
            comment.setDescription(description);
            comment.setPost(post);
            comment.setCreationDateTime(LocalDateTime.now());
            post.getComments().add(comment);
            commentRepository.save(comment);
            postRepository.save(post);
        }


        return post;
    }*/

    @Transactional
    public Post addLike(long postId, String username, int flag) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found for ID: " + postId));

        if (post.getUser() == null) {
            throw new IllegalStateException("Post does not have a user assigned.");
        }

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found for username: " + username);
        }

        // Provera da li korisnik već lajkuje post
        boolean alreadyLiked = post.getLikesList().stream()
                .anyMatch(like -> like.getUser().getId() == user.getId());

        if (alreadyLiked) {
            post.setLikes(post.getLikes() - 1);
            post.getLikesList().removeIf(like -> like.getUser().getId() == user.getId());
            likeRepository.deleteByUserAndPost(user.getId(), post.getId());

        }

        if (!alreadyLiked) {

            post.setLikes(post.getLikes() + 1);
            Like like = new Like(user, post, LocalDateTime.now());
            post.getLikesList().add(like);
            likeRepository.save(like);
        }

        // Ažuriranje broja lajkova





        return postRepository.save(post);
    }



    public Post addComment(long postId, String username, String description) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (!checkIfUserCanComment(user)) {
            throw new IllegalStateException("User has exceeded the comment limit for the last hour.");
        }

        // Kreiranje i dodavanje komentara
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setDescription(description);
        comment.setPost(post);
        comment.setCreationDateTime(LocalDateTime.now());

        post.getComments().add(comment);
        commentRepository.save(comment);

        return postRepository.save(post); // Vraćanje ažuriranog posta
    }


    public boolean checkIfUserCanComment(User user) {
        // Dohvatanje svih komentara iz repozitorijuma
        List<Comment> comments = commentRepository.findAll();

        // Trenutno vreme i vreme pre jednog sata
        Instant oneHourAgo = Instant.now().minusSeconds(3600);

        // Filtriranje komentara korisnika u poslednjih sat vremena
        List<Comment> recentComments = comments.stream()
                .filter(comment -> comment.getUser().getId() == user.getId()) // Komentari korisnika
                .filter(comment -> comment.getCreationDateTime()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .isAfter(oneHourAgo)) // Komentari u poslednjih sat vremena
                .collect(Collectors.toList());

        // Ako korisnik ima više od 60 komentara u poslednjih sat vremena, vraćamo false
        return recentComments.size() < 15;
    }




}
