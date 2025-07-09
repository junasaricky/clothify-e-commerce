package com.ricky.clothingshop.dto;

import com.ricky.clothingshop.model.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderStatusRequest {
    private OrderStatus status;
    private String remarks; 
}
