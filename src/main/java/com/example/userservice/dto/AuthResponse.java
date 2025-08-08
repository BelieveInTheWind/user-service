package com.example.userservice.dto;

public class AuthResponse {
    private String token;
    private String role;
    private String message;
    
    // Constructors
    public AuthResponse() {}
    
    public AuthResponse(String token, String role, String message) {
        this.token = token;
        this.role = role;
        this.message = message;
    }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}