package com.ricky.clothingshop.controller;

import com.ricky.clothingshop.model.Product;
import com.ricky.clothingshop.service.ProductService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/products")
public class AdminController {

    private final ProductService service;

    // Add a product
    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        return ResponseEntity.ok(service.saveProduct(product));
    }

    // Update a product
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product newProduct) {
        Product existing = service.getProductById(id);
        if (existing == null) return ResponseEntity.notFound().build();

        existing.setName(newProduct.getName());
        existing.setDescription(newProduct.getDescription());
        existing.setColor(newProduct.getColor());
        existing.setSize(newProduct.getSize());
        existing.setPrice(newProduct.getPrice());
        existing.setStock(newProduct.getStock());
        existing.setImageUrl(newProduct.getImageUrl());
        existing.setHasDiscount(newProduct.isHasDiscount());
        existing.setDiscountPercentage(newProduct.getDiscountPercentage());


        return ResponseEntity.ok(service.saveProduct(existing));
    }

    // Delete a product
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Product existing = service.getProductById(id);
        if (existing == null) return ResponseEntity.notFound().build();
    
        service.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // List all products (for admin)
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(service.getAll());
    }

    // Get product by ID for editing
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Product product = service.getProductById(id);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
        return ResponseEntity.ok(product);
    }

}
