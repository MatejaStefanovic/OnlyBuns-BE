package org.onlybuns.service;

import jakarta.servlet.http.HttpServletRequest;
import org.onlybuns.exceptions.Security.*;
import org.onlybuns.exceptions.UserRegistration.*;
import org.onlybuns.model.User;
import org.onlybuns.repository.UserRepository;
import org.onlybuns.security.AuthenticationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class UserLoginService {

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final EmailService emailService;


    // Constructor injection, Spring automatically wires the dependency
    public UserLoginService(UserRepository userRepository, EmailService emailService, AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.authenticationService = authenticationService;
    }

    public String loginUser(String email, String password) {
        // Call AuthenticationService to verify credentials and generate a JWT token
        String jwtToken = authenticationService.loginUser(email, password);

        if (jwtToken == null) {
            throw new InvalidTokenException("Invalid login credentials");
        }
        return jwtToken;

    }
    public void registerUser(User user) {
        // Call AuthenticationService to register a user without activating
        authenticationService.registerUser(user);

        String activationUrl = generateActivationUrl(user);
        sendActivationEmail(user.getEmail(), activationUrl);
    }

    public void sendActivationEmail(String email, String activationUrl) {
        String subject = "Account activation";
        String message = "Click on the link below to activate your account:\n" + activationUrl;

        emailService.sendEmail(email, subject, message);
    }

    // Generate the activation URL with JWT
    private String generateActivationUrl(User user) {
        // Makes a token from email address, implemented in TokenProvider
        String token = authenticationService.generateActivationToken(user.getEmail());
        // Hits endpoint for registration with appropriate token
        return "http://localhost:8080/api/user/activate?token=" + token;
    }

    // Activate user when clicking the link in the activation email
    public void activateUser(String token) {
        // Inverse operation than the one from the method above, gets email from token
        String email = authenticationService.getEmailFromJWT(token);
        User user = userRepository.findByEmail(email);

        if (user != null && !user.isActivated()) {
            user.setActivated(true); // Activate the user account
            userRepository.save(user);
        } else {
            throw new InvalidTokenException("Invalid or expired activation token.");
        }
    }
    
    public User getUserByEmail(String email) {
    	User user = userRepository.findByEmail(email);
    	if (user != null && user.isActivated()) {
    		return user;
    	} else
    		throw new UnauthorizedUserException("User is not verified");
    }
    public User getCurrentUser() {
        // Izvlači token iz Authorization zaglavlja
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authorizationHeader = request.getHeader("Authorization");

            if (authorizationHeader != null) {
                String token;
                if (authorizationHeader.startsWith("Bearer ")) {
                    // Uklanja "Bearer " prefiks
                    token = authorizationHeader.substring(7);
                } else {
                    // Pretpostavlja da je ceo sadržaj zaglavlja token
                    token = authorizationHeader;
                }

                String email = authenticationService.getEmailFromJWT(token);
                return userRepository.findByEmail(email);
            }
        }
        return null; // Vraća null ako nema validnog tokena
    }
}
