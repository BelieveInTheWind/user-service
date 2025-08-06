package com.example.userservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.userservice.dto.RegisterRequest;
import com.example.userservice.entity.Role;
import com.example.userservice.entity.User;
import com.example.userservice.entity.UserRole;
import com.example.userservice.repository.RoleRepository;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.repository.UserRoleRepository;

@Service
@Transactional
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRoleRepository userRoleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User registerUser(RegisterRequest registerRequest) {
        // Check if user already exists
        if (userRepository.existsByUserEmail(registerRequest.getEmail())) {
            throw new RuntimeException("User with this email already exists");
        }
        
        // Create new user
        User user = new User();
        user.setUserEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        
        User savedUser = userRepository.save(user);
        
        // Assign default USER role
        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RuntimeException("Default USER role not found"));
        
        UserRole userRoleEntity = new UserRole(savedUser, userRole);
        userRoleRepository.save(userRoleEntity);
        
        return savedUser;
    }
    
    public String getUserRole(String email) {
        User user = userRepository.findByUserEmailWithRoles(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return user.getUserRoles().stream()
                .findFirst()
                .map(userRole -> userRole.getRole().getRoleName())
                .orElse("USER");
    }
}