package com.ricky.clothingshop.repository;

import com.ricky.clothingshop.model.Cart;
import com.ricky.clothingshop.model.CartItem;
import com.ricky.clothingshop.model.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}
