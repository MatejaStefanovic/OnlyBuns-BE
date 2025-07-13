package org.onlybuns.service;

import jakarta.transaction.Transactional;
import org.onlybuns.DTOs.PostCreationDTO;
import org.onlybuns.component.MessageRabbitSender;
import org.onlybuns.model.*;
import org.onlybuns.repository.CommentRepository;
import org.onlybuns.repository.LikeRepository;
import org.onlybuns.repository.PostRepository;
import org.onlybuns.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FileStorageSerivce fileStorageService;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final MessageRabbitSender messageRabbitSender;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository, FileStorageSerivce fileStorageService, LikeRepository likeRepository, CommentRepository commentRepository, MessageRabbitSender messageRabbitSender) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.messageRabbitSender = messageRabbitSender;
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

    public Post updateSuitable(Long postId, boolean suitable) throws IOException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found for ID: " + postId));

        post.setSuitableForAds(suitable);
        messageRabbitSender.sendMessage(post.getDescription(),post.getCreationDateTime().toString(), post.getUser().getUsername());
        return postRepository.save(post);
    }

    public Post createPost(PostCreationDTO postCreationDTO) throws IOException {
        Post post = new Post();
        post.setDescription(postCreationDTO.getDescription());
        post.setLocation(postCreationDTO.getLocation());
        post.setCreationDateTime(LocalDateTime.now());
        User user = userRepository.findByEmail(postCreationDTO.getEmail());
        post.setUser(user);
        user.setNumberOfPosts(user.getNumberOfPosts()+1);
        userRepository.save(user);
        if (postCreationDTO.getImage() != null && !postCreationDTO.getImage().isEmpty()) {
            Image image = fileStorageService.storeFile(postCreationDTO.getImage());
            fileStorageService.getImageBase64ForImage(image);
            post.setImage(image);
        }

        return postRepository.save(post);
    }

    public int getPostStatisticsYearly() {
        List<Object[]> dailyPostCounts =    postRepository.getDailyPostCounts(LocalDateTime.now().minusYears(1), LocalDateTime.now());
        Map<String, Long> yearlyCounts = new HashMap<>();

        for (Object[] row : dailyPostCounts) {
            Date d = (Date) row[0];
            LocalDate date = ((java.sql.Date) d).toLocalDate();
            long postCount = (long) row[1];
            String year = String.valueOf(date.getYear());
            yearlyCounts.put(year, yearlyCounts.getOrDefault(year, 0L) + postCount);
        }

        long sum = 0;
        for (long count : yearlyCounts.values()) {
            sum += count;
        }
        int totalMonths = yearlyCounts.size();
        System.out.println("YEARLY: " + yearlyCounts);
        return (int) Math.round((double) sum / totalMonths);
    }



    public int getPostStatisticsMonthly() {
        List<Object[]> dailyPostCounts =    postRepository.getDailyPostCounts(LocalDateTime.now().minusMonths(3), LocalDateTime.now());
        Map<String, Long> monthlyCounts = new HashMap<>();

        for (Object[] row : dailyPostCounts) {
            Date d = (Date) row[0];
            LocalDate date = ((java.sql.Date) d).toLocalDate();
            long postCount = (long) row[1];
            int year = date.getYear();
            int month = date.getMonthValue();
            String yearMonth = year + "-" + String.format("%02d", month); //"GODINA-MJESEC"
            monthlyCounts.put(yearMonth, monthlyCounts.getOrDefault(yearMonth, 0L) + postCount);
        }
        //dodavanje sedmica u kojim nema postova
        LocalDate startDate = LocalDate.now().minusMonths(3);
        LocalDate endDate = LocalDate.now();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            int year = current.getYear();
            int month = current.getMonthValue();
            String yearMonth = year + "-" + String.format("%02d", month);
            monthlyCounts.putIfAbsent(yearMonth, 0L);
            current = current.plusMonths(1);
        }

        System.out.println("DDDMONTH: " + monthlyCounts);
        long sum = 0;
        for (long count : monthlyCounts.values()) {
            sum += count;
        }
        int totalMonths = monthlyCounts.size();
        System.out.println("DDDMONTH: " + monthlyCounts);
        return (int) Math.round((double) sum / totalMonths);
    }

    public int getPostStatisticsWeekly() {
        List<Object[]> dailyPostCounts =    postRepository.getDailyPostCounts(LocalDateTime.now().minusMonths(3), LocalDateTime.now());
        Map<String, Long> weeklyCounts = new HashMap<>();

        for (Object[] row : dailyPostCounts) {
            Date d = (Date) row[0];
            LocalDate date = ((java.sql.Date) d).toLocalDate();
           // LocalDate date = dateTime.toLocalDate();
            long postCount = (long) row[1];
            int year = date.getYear();
            int week = date.get(WeekFields.of(Locale.getDefault()).weekOfYear());
            String yearWeek = year + "-" + String.format("%02d", week); //"GODINA-SEDMICA"
            weeklyCounts.put(yearWeek, weeklyCounts.getOrDefault(yearWeek, 0L) + postCount);
        }
 //dodavanje sedmica u kojim nema postova
        LocalDate startDate = LocalDate.now().minusMonths(3);
        LocalDate endDate = LocalDate.now();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            int year = current.getYear();
            int week = current.get(WeekFields.of(Locale.getDefault()).weekOfYear());
            String yearWeek = year + "-" + String.format("%02d", week);
            weeklyCounts.putIfAbsent(yearWeek, 0L);
            current = current.plusWeeks(1);
        }

        System.out.println("R: " + weeklyCounts);
        long sum = 0;
        for (long count : weeklyCounts.values()) {
            sum += count;
        }
        int totalWeeks = weeklyCounts.size();
        System.out.println("R: " + weeklyCounts);
        return (int) Math.round((double) sum / totalWeeks);
    }

    /*public int getPostStatisticsWeekly(){
        List<Object[]> weeklyPostCounts = postRepository.findWeeklyPostCounts(LocalDateTime.now().minusMonths(3), LocalDateTime.now());
        int  sum=0;
        int count=weeklyPostCounts.size();
        for (Object[] row : weeklyPostCounts) {
            String date = (String) row[0];
            long postCount = (int) row[1];
            sum += postCount;
        }
        return (int)sum/count;
    }*/

    public List<Post> getAllPosts() throws IOException {
        return postRepository.findAll();
    }

    public List<Post> getAllPostsFollowed(String loggedUsername) {

        List<Post> allPosts = postRepository.findAll();
        List<Post> filteredPosts = new ArrayList<>();


        for (Post post : allPosts) {
            boolean isFollower = post.getUser().getFollowers().stream()
                    .anyMatch(follower -> follower.equals(loggedUsername));
            if (isFollower) {
                filteredPosts.add(post);
            }
        }
        return filteredPosts;
    }


    public List<Post> getPostsFromUser(String email) throws IOException {
        User user = userRepository.findByEmail(email); // Retrieve the user by email
        return postRepository.findAll()
                .stream()
                .filter(post -> post.getUser().getId() == user.getId())
                .collect(Collectors.toList());
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

    @Transactional
    public List<Post> getTopFivePostsLastWeek() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        Pageable pageable = PageRequest.of(0, 5); // Page 0, size 5
        return postRepository.findTopFivePostsLastWeek(sevenDaysAgo, pageable);
    }

    @Transactional
    public List<Post> getTopTenPostsAllTime() {
        Pageable pageable = PageRequest.of(0, 10); // Page 0, size 10
        return postRepository.findTopTenPostsAllTime(pageable);
    }
    
    @Transactional
    public List<Object[]> getTopTenUsersThatLikedMost() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        Pageable pageable = PageRequest.of(0, 10); // Page 0, size 10
        return likeRepository.findUsersWithMostLikesLastWeek(sevenDaysAgo, pageable);
    }

}
