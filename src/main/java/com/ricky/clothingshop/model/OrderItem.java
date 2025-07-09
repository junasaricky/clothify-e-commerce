package com.ricky.clothingshop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*; 
import lombok.*; 

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Order order;


    @ManyToOne
    private Product product;

    private int quantity;
    private double price;
}
