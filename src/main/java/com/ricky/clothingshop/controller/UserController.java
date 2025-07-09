package com.ricky.clothingshop.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ricky.clothingshop.dto.UpdatePasswordRequest;
import com.ricky.clothingshop.dto.UpdateProfileImageRequest;
import com.ricky.clothingshop.dto.UpdateProfileRequest;
import com.ricky.clothingshop.model.User;
import com.ricky.clothingshop.service.UserService;
import com.ricky.clothingshop.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;
    private final JwtUtil jwtUtil;

    @PatchMapping("/update-profile")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<User> updateProfile(@RequestHeader("Authorization") String authHeader,
                                              @RequestBody UpdateProfileRequest dto) {
        String username = jwtUtil.extractUsername(authHeader.replace("Bearer ", ""));
        User updated = service.updateProfile(username, dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/update-password")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<?> updatePassword(@RequestHeader("Authorization") String authHeader,
                                                @RequestBody UpdatePasswordRequest dto) {
        try {
            String username = jwtUtil.extractUsername(authHeader.replace("Bearer ", ""));
            service.updatePassword(username, dto);
            return ResponseEntity.ok(Map.of("message", "Password updated successfully."));

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .badRequest()
                .body(Map.of("message", e.getMessage())); 
        }
    }

    @PatchMapping("/update-profile-image")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<User> updateProfileImage(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateProfileImageRequest dto) {
        
        String username = jwtUtil.extractUsername(authHeader.replace("Bearer ", ""));
        User updated = service.updateProfileImage(username, dto);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String username = jwtUtil.extractUsername(authHeader.replace("Bearer ", ""));
        User user = service.findByUsername(username);
        return ResponseEntity.ok(user);
}
}
