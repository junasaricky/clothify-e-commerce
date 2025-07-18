package com.ricky.clothingshop.service;

import com.ricky.clothingshop.model.Cart;
import com.ricky.clothingshop.model.CartItem;
import com.ricky.clothingshop.model.Product;
import com.ricky.clothingshop.model.User;
import com.ricky.clothingshop.repository.CartItemRepository;
import com.ricky.clothingshop.repository.CartRepository;
import com.ricky.clothingshop.repository.ProductRepository;
import com.ricky.clothingshop.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final UserRepository userRepo;
    private final ProductRepository productRepo;
    private final CartRepository cartRepo;
    private final CartItemRepository cartItemRepo;

    public CartService(UserRepository userRepo, ProductRepository productRepo, CartRepository cartRepo, CartItemRepository cartItemRepo) {
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
    }

    public void addToCart(String username, Long productId, int quantity) {
        User user = userRepo.findByUsername(username).orElseThrow();
        Product product = productRepo.findById(productId).orElseThrow();
        
        Cart cart = cartRepo.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepo.save(newCart);
        });

        CartItem item = cartItemRepo.findByCartAndProduct(cart, product)
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setQuantity(0);
                    return newItem;
                });

        item.setQuantity(item.getQuantity() + quantity);
        cartItemRepo.save(item);
    }

    public List<CartItem> getCartItems(String username) {
        User user = userRepo.findByUsername(username).orElseThrow();

        Cart cart = cartRepo.findByUser(user).orElse(null);

        if (cart == null) {
            return Collections.emptyList(); // return empty cart instead of error
        }

        return cart.getItems();
    }

    public void clearCart(String username) {
        User user = userRepo.findByUsername(username).orElseThrow();
        Cart cart = cartRepo.findByUser(user).orElseThrow();
        // Delete cart items from repo
        cartItemRepo.deleteAll(cart.getItems());
        // Clear items in memory to avoid residual state
        cart.getItems().clear();
        // Save updated cart
        cartRepo.save(cart);
    }
    
    @Transactional
    public void removeCartItemsByProductIds(String username, List<Long> productIds) {
        User user = userRepo.findByUsername(username).orElseThrow();
        Cart cart = cartRepo.findByUser(user).orElseThrow();

        List<CartItem> itemsToDelete = cart.getItems().stream()
            .filter(item -> productIds.contains(item.getProduct().getId()))
            .collect(Collectors.toList());

        cartItemRepo.deleteAll(itemsToDelete);

        // Optional: clear in-memory list of deleted items
        cart.getItems().removeIf(item -> productIds.contains(item.getProduct().getId()));

        cartRepo.save(cart);
    }

    @Transactional
    public void removeCartItemById(String username, Long itemId) {
        User user = userRepo.findByUsername(username).orElseThrow();
        Cart cart = cartRepo.findByUser(user).orElseThrow();

        CartItem item = cart.getItems().stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Cart item not found"));

        cart.getItems().remove(item);
        cartItemRepo.delete(item);
        cartRepo.save(cart);
    }

    public void updateCartItemQuantity(Long itemId, int quantity) {
        Optional<CartItem> optionalItem = cartItemRepo.findById(itemId);
        if (optionalItem.isPresent()) {
            CartItem item = optionalItem.get();
            item.setQuantity(quantity);
            cartItemRepo.save(item);
        } else {
            throw new RuntimeException("Cart item not found with ID: " + itemId);
        }
    }
}
