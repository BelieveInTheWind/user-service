package com.example.userservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.userservice.entity.Permission;
import com.example.userservice.entity.Role;
import com.example.userservice.repository.PermissionRepository;
import com.example.userservice.repository.RoleRepository;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.repository.UserRoleRepository;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserRoleRepository userRoleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
        initializePermissions();
    }
    
    private void initializeRoles() {
        if (roleRepository.count() == 0) {
            Role adminRole = new Role("ADMIN", "Admin");
            Role userRole = new Role("USER", "Regular user");
            
            roleRepository.save(adminRole);
            roleRepository.save(userRole);
            
            System.out.println("Default roles initialized");
        }
    }
    
    private void initializePermissions() {
        if (permissionRepository.count() == 0) {
            Role adminRole = roleRepository.findByRoleName("ADMIN").orElse(null);
            Role userRole = roleRepository.findByRoleName("USER").orElse(null);
            
            if (adminRole != null) {
                permissionRepository.save(new Permission("CREATE_USER", adminRole));
                permissionRepository.save(new Permission("UPDATE_USER", adminRole));
                permissionRepository.save(new Permission("DELETE_USER", adminRole));
                permissionRepository.save(new Permission("VIEW_ALL_USERS", adminRole));
                permissionRepository.save(new Permission("MANAGE_ROLES", adminRole));
            }
            
            if (userRole != null) {
                permissionRepository.save(new Permission("VIEW_OWN_PROFILE", userRole));
                permissionRepository.save(new Permission("UPDATE_OWN_PROFILE", userRole));
            }
            
            System.out.println("Default permissions initialized");
        }
    }
    
}
