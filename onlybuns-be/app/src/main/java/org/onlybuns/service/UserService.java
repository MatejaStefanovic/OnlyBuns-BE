package org.onlybuns.service;
import org.onlybuns.enums.UserRole;
import org.onlybuns.exceptions.Security.InvalidTokenException;
import org.onlybuns.repository.UserRepository;
import org.onlybuns.security.AuthenticationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.onlybuns.exceptions.Security.*;
import org.onlybuns.model.User;
import org.onlybuns.repository.UserRepository;
import org.onlybuns.security.AuthenticationService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class UserService {
    private final Logger LOG = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final EmailService emailService;
    private int a = 0;
    

    public UserService(UserRepository userRepository,AuthenticationService authenticationService, EmailService emailService) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
        this.emailService = emailService;
    }
    public long count(){
        return userRepository.count();
    }
    public long countByNumberOfPostsGreaterThan(int number){
           return userRepository.countByNumberOfPostsGreaterThan(0);
    }
    public List<User> findAll() {
        return userRepository.findAllByRole(UserRole.NORMAL);
    }

    public User findByUsername(String username) { return userRepository.findByUsername(username); }

    @Transactional
    @RateLimiter(name = "followLimiter", fallbackMethod = "followFallback")
    public User follow(String usernameFollower, String usernameFollowing) {
        LOG.info("Follow method invoked for follower: {} and following: {}", usernameFollower, usernameFollowing);
        User user1 = findByUsername(usernameFollower);
        User user2 = userRepository.findByUsername(usernameFollowing);
       /* try {
            Thread.sleep(2000); // Pauza od 100ms
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error("Thread interrupted", e);
        }*/
        a++;
        int number = user2.getNumberOfFollowers() + 1;
        user2.setNumberOfFollowers(number);
        user2.getFollowers().add(user1.getUsername());
        user1.setNumberOfFollowing(user1.getNumberOfFollowing() + 1);
        user1.getFollowing().add(user2.getUsername());
        System.out.println("Rate limit  counter: " + a);
        userRepository.save(user1);
        return userRepository.save(user2);
    }

    public User followFallback(String usernameFollower, String usernameFollowing, RequestNotPermitted rnp) {
        LOG.warn("Too much follwings per minute for user: {}", usernameFollower);
        a=0;
        System.out.println("Rate limit triggered for: " + usernameFollower);
        throw new IllegalStateException("You have reached your following limit per minute.", rnp);

    }

    public void updateUser(String email, User updatedUser) {
        LOG.info("Updating user with email: {}", email);
        
        authenticationService.updateUser(email, updatedUser);
       
        LOG.info("User updated successfully");
    }

    @Transactional
    public User unfollow(String usernameFollower, String usernameFollowing){
        User user1 = findByUsername(usernameFollower);
        User user2 = userRepository.findByUsername(usernameFollowing);

       /*try {
            Thread.sleep(2000); // Pauza od 100ms
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error("Thread interrupted", e);
        }*/

        int number = user2.getNumberOfFollowers()-1;
        user2.setNumberOfFollowers(number);
        user2.getFollowers().remove(user1.getUsername());
        user1.setNumberOfFollowing(user1.getNumberOfFollowing()-1);
        user1.getFollowing().remove(user2.getUsername());
        userRepository.save(user1);
        return userRepository.save(user2);
    }

    @Scheduled(cron = "0 0 0 L * ?")
   // @Scheduled(cron = "0 */10 * * * ?")
    @Transactional
    public void deleteInactiveAccounts() {
        System.out.println("Pokretanje  brisanja neaktivnih naloga.");
        List<User> inactiveUsers = userRepository.findInactiveUsers();

        if (!inactiveUsers.isEmpty()) {
            inactiveUsers.forEach(user -> System.out.println("Brisanje neaktivnog korisnika: "+ user.getUsername()));
            userRepository.deleteAll(inactiveUsers);
            System.out.println("Završeno brisanje neaktivnih naloga "+ inactiveUsers.size());
        } else {
            System.out.println("Nema neaktivnih naloga.");
        }
    }
}
