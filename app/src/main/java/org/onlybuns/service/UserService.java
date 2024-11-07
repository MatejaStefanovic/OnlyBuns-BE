package org.onlybuns.service;

import org.onlybuns.repository.UserRepository;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserService {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private final UserRepository userRepository;

    // Constructor injection, Spring automatically wires the dependency
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public static boolean isValidEmail(String email) {
        // Compile the regex pattern
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        // Match the input email to the pattern
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
