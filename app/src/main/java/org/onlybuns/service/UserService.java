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

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    public UserService(UserRepository userRepository,AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }

    public List<User> findAll() {
        return userRepository.findAllByRole(UserRole.NORMAL);
    }
}
