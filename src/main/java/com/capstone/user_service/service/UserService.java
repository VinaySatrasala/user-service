package com.capstone.user_service.service;

import com.capstone.user_service.exceptions.DuplicateUserException;
import com.capstone.user_service.exceptions.UserNotFoundException;
import com.capstone.user_service.model.LoginResponse;
import com.capstone.user_service.model.User;
import com.capstone.user_service.repository.UserRepository;
import com.capstone.user_service.util.JwtUtil;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtUtil jwtUtil;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // CREATE
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateUserException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateUserException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // VALIDATE JWT
    public String validateToken(String token) {
        String username = jwtUtil.extractUsername(token);
        User user = userRepository.findByUsername(username).get();
        if (user != null && jwtUtil.validateToken(token, user)) {
            return username;
        }
        return null;
    }

    // LOGIN
    public LoginResponse loginUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        if (passwordEncoder.matches(password, user.getPassword())) {
            return new LoginResponse(jwtUtil.generateToken(user),user.getId());

        } else {
            throw new UserNotFoundException("Invalid password");
        }
    }

    // READ
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    // UPDATE
    public User updateUser(String username, User user) {
        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        System.out.println(user);
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setState(user.getState());
        existingUser.setCountry(user.getCountry());
        System.out.println(existingUser);
        return userRepository.save(existingUser);
    }

    // CHANGE PASSWORD
    public void changePassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    // DELETE
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        userRepository.delete(user);
    }
}
