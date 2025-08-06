package com.example.userservice.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.userservice.entity.Profile;
import com.example.userservice.entity.User;
import com.example.userservice.repository.ProfileRepository;
import com.example.userservice.repository.UserRepository;

@Service
@Transactional
public class ProfileService {
    
    @Autowired
    private ProfileRepository profileRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Optional<Profile> getProfileByUserId(Long userId) {
        return profileRepository.findById(userId);
    }
    
    public Profile createOrUpdateProfile(Long userId, Profile profileDetails) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Profile profile = profileRepository.findById(userId)
            .orElse(new Profile());
        
        profile.setUser(user);
        profile.setUserEmail(profileDetails.getUserEmail());
        profile.setFullName(profileDetails.getFullName());
        profile.setUserName(profileDetails.getUserName());
        profile.setPhoneNumber(profileDetails.getPhoneNumber());
        profile.setPicUrl(profileDetails.getPicUrl());
        profile.setAddressLine1(profileDetails.getAddressLine1());
        profile.setAddressLine2(profileDetails.getAddressLine2());
        profile.setPostcode(profileDetails.getPostcode());
        profile.setCity(profileDetails.getCity());
        
        return profileRepository.save(profile);
    }
    
    public void deleteProfile(Long userId) {
        if (profileRepository.existsById(userId)) {
            profileRepository.deleteById(userId);
        }
    }
}