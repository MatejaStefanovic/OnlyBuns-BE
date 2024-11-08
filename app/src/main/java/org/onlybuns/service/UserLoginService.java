package org.onlybuns.service;

import org.onlybuns.exceptions.Security.*;
import org.onlybuns.model.User;
import org.onlybuns.repository.UserRepository;
import org.onlybuns.security.AuthenticationService;
import org.springframework.stereotype.Service;

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

    public void loginUser(String username, String password) {
        // Call AuthenticationService to verify credentials and generate a JWT token
        String jwtToken = authenticationService.loginUser(username, password);

        if (jwtToken == null) {
            throw new InvalidTokenException("Invalid login credentials");
        }

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
}
