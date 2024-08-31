package com.capstone.user_service.controller;

import com.capstone.user_service.model.*;
import com.capstone.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    //for gateway testing
    @GetMapping("/test")
    public String test(){
        return "user service up and running";
    }

    // CREATE
    @PostMapping("/signup")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // VALIDATE JWT
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestBody JWT jwt) {
        String username = userService.validateToken(jwt.getToken());
        if(username!=null)
            return ResponseEntity.ok(new ValidationResponse(username));
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token is invalid");
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody AuthenticationRequest request) {
        LoginResponse response = userService.loginUser(request.getUsername(), request.getPassword());
        if (response != null&&response.getToken()!=null) {
            return ResponseEntity.ok(response);
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

    }

    // READ
    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);

    }

    // UPDATE
    @PutMapping("/{username}")
    public ResponseEntity<User> updateUser(@PathVariable String username, @RequestBody User user) {
        User updatedUser = userService.updateUser(username, user);
        return ResponseEntity.ok(updatedUser);
    }

    // CHANGE PASSWORD
    @PutMapping("/{username}/password")
    public ResponseEntity<Void> changePassword(@PathVariable String username, @RequestBody String newPassword) {
        userService.changePassword(username, newPassword);
        return ResponseEntity.noContent().build();
    }

    // DELETE
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }
}
