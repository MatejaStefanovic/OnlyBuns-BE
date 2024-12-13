package org.onlybuns.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.onlybuns.exceptions.UserRegistration.*;
import org.onlybuns.exceptions.DoesNotExist.UsernameAlreadyExistsException;
import org.onlybuns.model.User;
import org.onlybuns.repository.UserRepository;
import org.onlybuns.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;


@Service
public class AuthenticationService {

    private static final Logger logger = LogManager.getLogger(AuthenticationService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenProvider tokenProvider;  // Your Token Provider to generate JWT tokens

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;  // For password hashing and comparison

    // Example method for user login
    public String loginUser(String email, String password) {
        logger.info("Attempting login for email: {}", email);

        User user = userRepository.findByEmail(email);
        if (user == null) {
            logger.warn("Login failed. Email {} not found.", email);
            throw new InvalidCredentialsException("Email not found");
        }

        // Check if the password matches (using BCrypt for password hashing)
        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("Login failed. Incorrect password for user: {}", email);
            throw new InvalidCredentialsException("Incorrect password");
        }
        if(!user.isActivated()){
            throw new UnauthorizedUserException("User is not verified");
        }
        logger.info("User {} logged in successfully.", email);

        // Generate JWT token and return it
         return tokenProvider.generateToken(user.getEmail());



    }


    public void registerUser(User user){
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UsernameAlreadyExistsException("Username is already taken");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }

        String hashedPassword = encodePassword(user.getPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);

    }

    public String encodePassword(String password){
        return passwordEncoder.encode(password);
    }

    public String generateActivationToken(String email) {
        return tokenProvider.generateToken(email);
    }

    public String getEmailFromJWT(String token) {
        return tokenProvider.getSubjectFromJWT(token);
    }

}

