package com.ricky.clothingshop.model;

import jakarta.persistence.*; 
import lombok.*; 

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private String description;
    private String size;
    private String color;
    private double price;
    private int stock;
    private double discountPercentage;
    private boolean hasDiscount;

    public double getDiscountedPrice() {
        if (hasDiscount && discountPercentage > 0) {
            return price - (price * (discountPercentage / 100));
        }
        return price;
    }

    @Lob
    @Column(columnDefinition = "LONGTEXT") 
    private String imageData;
    
}