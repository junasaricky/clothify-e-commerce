package com.ricky.clothingshop.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ricky.clothingshop.model.User;
import com.ricky.clothingshop.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepo;

    public AuthService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public User save(User user) {
        return userRepo.save(user);
    } 

    public Optional<User> findByResetToken(String token) {
        return userRepo.findByPasswordResetToken(token);
    }
}
