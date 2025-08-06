package com.example.userservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.userservice.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    List<Permission> findByRole_RoleId(Long roleId);
    List<Permission> findByPermissionType(String permissionType);
}