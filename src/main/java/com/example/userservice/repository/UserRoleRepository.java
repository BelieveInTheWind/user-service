package com.example.userservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.userservice.entity.UserRole;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUser_UserId(Long userId);
    List<UserRole> findByRole_RoleId(Long roleId);
    void deleteByUser_UserIdAndRole_RoleId(Long userId, Long roleId);
}