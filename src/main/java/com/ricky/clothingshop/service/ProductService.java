package com.ricky.clothingshop.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ricky.clothingshop.model.Product;
import com.ricky.clothingshop.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepo;

    public ProductService(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    public List<Product> getAll() {
        return productRepo.findAll();
    }

    public List<Product> getDistinctByName() {
        List<Product> all = productRepo.findAll();

        // Use LinkedHashMap to preserve insertion order and remove duplicates by name
        Map<String, Product> uniqueMap = new LinkedHashMap<>();
        for (Product p : all) {
            uniqueMap.putIfAbsent(p.getName(), p); // first one per name
        }
        return new ArrayList<>(uniqueMap.values());
    }

    public Product saveProduct(Product product) {
        return productRepo.save(product);
    }

    public List<Product> searchProducts(String keyword) {
        return productRepo.findByNameContainingIgnoreCase(keyword);
    }

    public Product getProductById(Long id) {
        return productRepo.findById(id).orElse(null);
    }

    public void deleteProduct(Long id) {
        productRepo.deleteById(id);
    }
}
