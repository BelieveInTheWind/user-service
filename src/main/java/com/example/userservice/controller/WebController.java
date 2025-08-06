package com.example.userservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/register")
    public String register() {
        return "register";
    }
    
    @GetMapping("/reset-password")
    public String resetPassword() {
        return "reset-password";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return "dashboard";
    }
}