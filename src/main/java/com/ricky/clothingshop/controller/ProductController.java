package com.ricky.clothingshop.controller;

import com.ricky.clothingshop.model.Product;
import com.ricky.clothingshop.repository.ProductRepository;
import com.ricky.clothingshop.service.ProductService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;
    private final ProductRepository productRepo;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(service.getDistinctByName());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        Product product = service.getProductById(id);
        return product != null ? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(service.searchProducts(keyword));
    }

    @GetMapping("/variants")
    public ResponseEntity<List<Product>> getVariantsByName(@RequestParam String name) {
        List<Product> variants = productRepo.findByName(name); 
        return ResponseEntity.ok(variants);
    }

}
