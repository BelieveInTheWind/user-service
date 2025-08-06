package com.example.userservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.userservice.entity.Role;
import com.example.userservice.entity.User;
import com.example.userservice.entity.UserRole;
import com.example.userservice.repository.RoleRepository;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.repository.UserRoleRepository;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRoleRepository userRoleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByUserEmail(email);
    }
    
    // Check if the authenticated user owns the resource
    public boolean isOwner(String email, Long userId) {
        Optional<User> user = userRepository.findByUserEmail(email);
        return user.isPresent() && user.get().getUserId().equals(userId);
    }
    
    public User createUser(User user) {
        if (userRepository.existsByUserEmail(user.getUserEmail())) {
            throw new RuntimeException("User with email already exists");
        }
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        User savedUser = userRepository.save(user);
        
        // Assign default role (USER)
        Role defaultRole = roleRepository.findByRoleName("USER")
            .orElseThrow(() -> new RuntimeException("Default role not found"));
        
        UserRole userRole = new UserRole(savedUser, defaultRole);
        userRoleRepository.save(userRole);
        
        return savedUser;
    }
    
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setUserEmail(userDetails.getUserEmail());
        
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }
    
    public void assignRoleToUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new RuntimeException("Role not found"));
        
        UserRole userRole = new UserRole(user, role);
        userRoleRepository.save(userRole);
    }
    
    public void removeRoleFromUser(Long userId, Long roleId) {
        userRoleRepository.deleteByUser_UserIdAndRole_RoleId(userId, roleId);
    }
}