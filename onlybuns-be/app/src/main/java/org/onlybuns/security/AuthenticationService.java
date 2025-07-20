package org.onlybuns.security;

import com.google.common.hash.BloomFilter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.onlybuns.exceptions.UserRegistration.*;
import org.onlybuns.exceptions.DoesNotExist.UsernameAlreadyExistsException;
import org.onlybuns.model.User;
import org.onlybuns.model.Location;
import org.onlybuns.repository.UserRepository;
import org.onlybuns.service.BloomFilterService;
import org.onlybuns.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;


@Service
public class AuthenticationService {

    private static final Logger logger = LogManager.getLogger(AuthenticationService.class);

    private final BloomFilter<String> usernameBloomFilter;

    @Autowired
    public AuthenticationService(BloomFilterService bloomFilterService) {
        this.usernameBloomFilter = bloomFilterService.getUsernameBloomFilter();
    }

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

    @Transactional
    public void registerUser(User user){
        if (usernameBloomFilter.mightContain(user.getUsername())) {
            // U slucaju da se desio false positive!
            if (userRepository.existsByUsername(user.getUsername())) {
                throw new UsernameAlreadyExistsException("Username is already taken");
            }
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }

        String hashedPassword = encodePassword(user.getPassword());
        user.setPassword(hashedPassword);

        try {
            userRepository.save(user);
            usernameBloomFilter.put(user.getUsername());
            
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            //LOG.error("Thread interrupted", e);
        }
    }

    @Transactional
    public void updateUser(String email, User updatedUser) {
        User existingUser = userRepository.findByEmail(email);
        if (existingUser == null) {
            System.out.println("ERROR: User not found!");
            return;
        }
        
        System.out.println("👤 Existing user found: " + existingUser.getEmail());
        System.out.println("📍 Existing user location: " + existingUser.getLocation());
    

        // Check if username is being changed and if new username already exists
        if (!existingUser.getUsername().equals(updatedUser.getUsername()) && 
            usernameBloomFilter.mightContain(updatedUser.getUsername())) {
            if (userRepository.existsByUsername(updatedUser.getUsername())) {
                throw new UsernameAlreadyExistsException("Username is already taken");
            }
        }
        
        // Update fields
        String oldUsername = existingUser.getUsername();
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());

    
        if (updatedUser.getLocation() != null) {
            if (existingUser.getLocation() != null) {
                // Update existing location
                existingUser.getLocation().setCity(updatedUser.getLocation().getCity());
                existingUser.getLocation().setCountry(updatedUser.getLocation().getCountry());
                existingUser.getLocation().setStreet(updatedUser.getLocation().getStreet());
                // Add other location fields as needed
            } else {
                // Create new location if user didn't have one
                existingUser.setLocation(updatedUser.getLocation());
            }
        }
        // Only update password if a new one is provided
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            String hashedPassword = encodePassword(updatedUser.getPassword());
            existingUser.setPassword(hashedPassword);
        }
        
        // Update bloom filter if username changed
        if (!oldUsername.equals(updatedUser.getUsername())) {
            usernameBloomFilter.put(updatedUser.getUsername());
        }
        
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

