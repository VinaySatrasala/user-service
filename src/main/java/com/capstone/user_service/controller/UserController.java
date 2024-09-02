package com.capstone.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.user_service.model.AuthenticationRequest;
import com.capstone.user_service.model.JWT;
import com.capstone.user_service.model.LoginResponse;
import com.capstone.user_service.model.User;
import com.capstone.user_service.model.ValidationResponse;
import com.capstone.user_service.service.UserService;

@RestController
@CrossOrigin({"http://localhost:4200"})
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
