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

import java.time.Instant;
import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final EmailService emailService;

    public UserService(UserRepository userRepository,AuthenticationService authenticationService, EmailService emailService) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
        this.emailService = emailService;
    }

    public List<User> findAll() {
        return userRepository.findAllByRole(UserRole.NORMAL);
    }




}
