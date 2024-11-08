package org.onlybuns.service;

import org.onlybuns.exceptions.UserRegistration.*;
import org.onlybuns.model.User;
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

    public void registerUser(User user) {
        if(!hasAllFields(user)){
            throw new MissingRegistrationFieldsException("Fields must not be blank or empty");
        }
        if(!isValidEmail(user.getEmail())){
            throw new InvalidEmailFormatException("Email is not in a valid format");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UsernameAlreadyExistsException("Username is already taken");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }
        userRepository.save(user);
    }

    public void activateUser(User user){
        userRepository.save(user);
    }

    public boolean hasAllFields(User user){
        return !user.getEmail().isBlank() &&
                !user.getUsername().isBlank() &&
                !user.getFirstName().isBlank() &&
                !user.getLastName().isBlank() &&
                !user.getPassword().isBlank() &&
                !user.getLocation().getCountry().isBlank() &&
                !user.getLocation().getCity().isBlank() &&
                !user.getLocation().getStreet().isBlank();
    }
}
