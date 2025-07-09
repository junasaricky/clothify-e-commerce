package com.ricky.clothingshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ricky.clothingshop.dto.UpdatePasswordRequest;
import com.ricky.clothingshop.dto.UpdateProfileImageRequest;
import com.ricky.clothingshop.dto.UpdateProfileRequest;
import com.ricky.clothingshop.model.User;
import com.ricky.clothingshop.repository.UserRepository;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public List<User> getUsersByRole(String role) {
        return userRepo.findByRole(role);
    }

    public Optional<User> findById(Long id) {
        return userRepo.findById(id);
    }

    public void deleteById(Long id) {
        User user = userRepo.findById(id).orElseThrow();

        if ("ROLE_ADMIN".equals(user.getRole())) {
            throw new IllegalArgumentException("Cannot delete admin accounts.");
        }

        userRepo.delete(user);
    }

    public User updateProfile(String username, UpdateProfileRequest dto) {
        User user = userRepo.findByUsername(username).orElseThrow();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        return userRepo.save(user);
    }

    public User updatePassword(String username, UpdatePasswordRequest dto) {
        User user = userRepo.findByUsername(username).orElseThrow();

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        if (dto.getCurrentPassword().equals(dto.getNewPassword())) {
            throw new IllegalArgumentException("New password must be different from the current password.");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        return userRepo.save(user);
    }

    public User updateProfileImage(String username, UpdateProfileImageRequest dto) {
        User user = userRepo.findByUsername(username).orElseThrow();
        user.setProfileImage(dto.getProfileImage());
        return userRepo.save(user);
    }
    
    public User findByUsername(String username) {
        return userRepo.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}
