package org.onlybuns.service;
import org.onlybuns.enums.UserRole;
import org.onlybuns.exceptions.Security.InvalidTokenException;
import org.onlybuns.repository.UserRepository;
import org.onlybuns.security.AuthenticationService;
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

import java.util.List;

@Service
public class UserService {
    private final Logger LOG = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    public UserService(UserRepository userRepository,AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }

    public List<User> findAll() {
        return userRepository.findAllByRole(UserRole.NORMAL);
    }

    public User findByUsername(String username) { return userRepository.findByUsername(username); }

    /*public User follow(String usernameFollower, String usernameFollowing){
        User user1 = findByUsername(usernameFollower);
        User user2 = findByUsername(usernameFollowing);
        int number = user2.getNumberOfFollowers()+1;
        user2.setNumberOfFollowers(number);
        user2.getFollowers().add(user1);
        user1.setNumberOfFollowing(user1.getNumberOfFollowing()+1);
        userRepository.save(user1);
        return userRepository.save(user2);
    }
*/
    @Transactional
    @RateLimiter(name = "followLimiter", fallbackMethod = "followFallback")
    public User follow(String usernameFollower, String usernameFollowing) {
        LOG.info("Follow method invoked for follower: {} and following: {}", usernameFollower, usernameFollowing);
        User user1 = findByUsername(usernameFollower);
        User user2 = userRepository.findByUsername(usernameFollowing);
        try {
            Thread.sleep(2000); // Pauza od 100ms
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error("Thread interrupted", e);
        }
        int number = user2.getNumberOfFollowers() + 1;
        user2.setNumberOfFollowers(number);
        user2.getFollowers().add(user1);
        user1.setNumberOfFollowing(user1.getNumberOfFollowing() + 1);

        userRepository.save(user1);
        return userRepository.save(user2);
    }

    public User followFallback(String usernameFollower, String usernameFollowing, RequestNotPermitted rnp) {
        LOG.warn("Too much follwings per minute for user: {}", usernameFollower);
        System.out.println("Rate limit triggered for: " + usernameFollower);
        throw new IllegalStateException("You have reached your following limit per minute.", rnp);

    }

    @Transactional
    public User unfollow(String usernameFollower, String usernameFollowing){
        User user1 = findByUsername(usernameFollower);
        User user2 = userRepository.findByUsername(usernameFollowing);

       try {
            Thread.sleep(2000); // Pauza od 100ms
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error("Thread interrupted", e);
        }

        int number = user2.getNumberOfFollowers()-1;
        user2.setNumberOfFollowers(number);
        user2.getFollowers().remove(user1);
        user1.setNumberOfFollowing(user1.getNumberOfFollowing()-1);
        userRepository.save(user1);
        return userRepository.save(user2);
    }
}
