package com.ricky.clothingshop.repository;

import com.ricky.clothingshop.model.Cart;
import com.ricky.clothingshop.model.User;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}

