package org.onlybuns.service;

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

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class UserActivityService {


    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Autowired
    public UserActivityService(PostRepository postRepository, UserRepository userRepository, LikeRepository likeRepository, CommentRepository commentRepository, EmailService emailService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }


     //@Scheduled(cron = "0 * * * * ?")
    public void checkIfUserIsNotActiveMoreThat7Day() {



        List<User> users = userRepository.findAll();

        for (User user : users){// Pretpostavka: metod za dobijanje JWT-a korisnika

            Date oneMinuteAgo = Date.from(Instant.now().minusSeconds(60)); // Pre 1 minut

            if (user.getLastActivity().before(oneMinuteAgo)) {
                // Token je istekao pre više od 1 minuta
                emailService.sendEmail(user.getEmail(), "Statistics from last seven days", "Mau mau");
                user.setLastCheckedStatistics(LocalDateTime.now()); // Postavljanje vremena
                userRepository.save(user); // Čuvanje korisnika u bazi
            }

        }
    }

    public String generateBody(User user) {
        // Vreme pre jednog minuta
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        String body = "";
        // Brojač postova starijih od jednog minuta
        int count = 0;
        LocalDateTime lastcheckedActivty = user.getLastCheckedStatistics();
        for (Post post : postRepository.findAll()) {
            // Proveri da li je datum kreiranja posta pre jednog minuta
            if (post.getCreationDateTime().isBefore(oneMinuteAgo) &&  post.getCreationDateTime().isAfter(user.getLastCheckedStatistics())  && post.getUser().getId()!= user.getId()) {
                count++;
            }
        }

        // Kreiranje tela poruke sa rezultatom
        body = "User " + user.getUsername() + " has " + count + " new posts.";
        return body;
    }





}
