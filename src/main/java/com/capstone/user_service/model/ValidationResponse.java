package com.capstone.user_service.model;

public class ValidationResponse {
    String username;

    public ValidationResponse() {
    }
    public ValidationResponse(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
