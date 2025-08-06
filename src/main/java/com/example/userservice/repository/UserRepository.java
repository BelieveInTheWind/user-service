package com.example.userservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.userservice.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserEmail(String userEmail);
    
    @Query("SELECT u FROM User u JOIN FETCH u.profile WHERE u.userId = :userId")
    Optional<User> findByIdWithProfile(Long userId);
    
    @Query("SELECT u FROM User u JOIN FETCH u.userRoles ur JOIN FETCH ur.role WHERE u.userId = :userId")
    Optional<User> findByIdWithRoles(@Param("userId") Long userId);
    
    @Query("SELECT u FROM User u JOIN FETCH u.userRoles ur JOIN FETCH ur.role WHERE u.userEmail = :email")
    Optional<User> findByUserEmailWithRoles(@Param("email") String email);
    
    boolean existsByUserEmail(String userEmail);
}