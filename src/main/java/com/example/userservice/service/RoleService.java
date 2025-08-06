package com.example.userservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.userservice.entity.Permission;
import com.example.userservice.entity.Role;
import com.example.userservice.repository.PermissionRepository;
import com.example.userservice.repository.RoleRepository;

@Service
@Transactional
public class RoleService {
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
    
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }
    
    public Optional<Role> getRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }
    
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }
    
    public Role updateRole(Long id, Role roleDetails) {
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Role not found"));
        
        role.setRoleName(roleDetails.getRoleName());
        role.setDescription(roleDetails.getDescription());
        
        return roleRepository.save(role);
    }
    
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new RuntimeException("Role not found");
        }
        roleRepository.deleteById(id);
    }
    
    public Permission addPermissionToRole(Long roleId, Permission permission) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new RuntimeException("Role not found"));
        
        permission.setRole(role);
        return permissionRepository.save(permission);
    }
    
    public List<Permission> getPermissionsByRole(Long roleId) {
        return permissionRepository.findByRole_RoleId(roleId);
    }
}