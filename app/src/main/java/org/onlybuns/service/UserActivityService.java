package org.onlybuns.service;

import org.onlybuns.model.Like;
import org.onlybuns.model.Post;
import org.onlybuns.model.User;
import org.onlybuns.repository.CommentRepository;
import org.onlybuns.repository.LikeRepository;
import org.onlybuns.repository.PostRepository;
import org.onlybuns.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserActivityService {


    private final PostService postService;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final LikeRepository likeRepository;

    @Autowired
    public UserActivityService(PostService postService, UserRepository userRepository, LikeRepository likeRepository, CommentRepository commentRepository, EmailService emailService) {
        this.postService = postService;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.likeRepository = likeRepository;
    }


    @Scheduled(cron = "0 * * * * ?")
    public void checkIfUserIsNotActiveMoreThat7Day() throws IOException {



        List<User> users = userRepository.findAll();

        for (User user : users){// Pretpostavka: metod za dobijanje JWT-a korisnika

            Date oneMinuteAgo = Date.from(Instant.now().minusSeconds(60)); // Pre 1 minut


                if (user.getLastActivity().before(oneMinuteAgo)) {
                    // Token je istekao pre više od 1 minuta

                    String body = generateBody(user);
                    if (!Objects.equals(body, "")) {
                        emailService.sendEmail(user.getEmail(), "Statistics from last seven days", body);
                        user.setLastCheckedStatistics(Date.from(Instant.now())); // Postavljanje vremena
                        userRepository.save(user); // Čuvanje korisnika u bazi
                    }

                }

          /* else{

                boolean uslo1 = user.getLastActivity().before(oneMinuteAgo);
                boolean uslov2 = user.getLastCheckedStatistics().before(user.getLastActivity());
                if (user.getLastActivity().before(oneMinuteAgo) && user.getLastCheckedStatistics().before(user.getLastActivity())) {
                    // Token je istekao pre više od 1 minuta

                    String body = generateBody(user);
                    if (!Objects.equals(body, "")) {
                        emailService.sendEmail(user.getEmail(), "Statistics from last seven days", body);
                        user.setLastCheckedStatistics(Date.from(Instant.now())); // Postavljanje vremena
                        userRepository.save(user); // Čuvanje korisnika u bazi
                    }

                }
            }*/


        }
    }

    public String generateBody(User user) throws IOException {
        // Vreme pre jednog minuta
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        String body = "";
        // Brojač postova starijih od jednog minuta
        int count = 0;
        Date lastcheckedActivty = user.getLastCheckedStatistics();
        for (Post post : postService.getAllPosts()) {
            // Proveri da li je datum kreiranja posta pre jednog minuta
            if(user.getLastCheckedStatistics() == null){
                if (post.getCreationDateTime().isAfter(user.getLastActivity()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()) && post.getUser().getId()!= user.getId()) {
                    count++;
                }
            }
            else{
                if (post.getCreationDateTime().isAfter(user.getLastActivity()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()) && post.getUser().getId()!= user.getId() && post.getCreationDateTime().isAfter(user.getLastCheckedStatistics()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime())) {
                    count++;
                }
            }

        }

        int likes = getNewLikes(user);

        // Kreiranje tela poruke sa rezultatom
        if(count!=0 || likes!=0)
        {
            body = "User " + user.getUsername() + " has " + count + " new posts and " + likes + " new likes";
        }

        return body;
    }


    public int getNewLikes(User user) throws IOException {

        int likeNumbers = 0;

        List<Like> likes = likeRepository.findAll();

        if(user.getLastCheckedStatistics() == null){
            List<Like> newLikes = likes.stream()
                    .filter(like -> like.getCreationDateTime().isAfter(user.getLastActivity().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()))
                    .collect(Collectors.toList());

            if(!newLikes.isEmpty()){
                likeNumbers =  newLikes.size();
            }



            return likeNumbers;
        }
        else{
            List<Like> newLikes = likes.stream()
                    .filter(like -> like.getCreationDateTime().isAfter(user.getLastCheckedStatistics().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()))
                    .collect(Collectors.toList());

            if(!newLikes.isEmpty()){
                likeNumbers =  newLikes.size();
            }


            return likeNumbers;
        }



    }






}
