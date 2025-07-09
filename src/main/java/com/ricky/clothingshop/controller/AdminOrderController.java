package com.ricky.clothingshop.controller;

import com.ricky.clothingshop.dto.UpdateOrderStatusRequest;
import com.ricky.clothingshop.model.Order;
import com.ricky.clothingshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final OrderService orderService;

    // GET all customer orders
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // GET order by ID for View details
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    // UPDATE status of order
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId,
                                                   @RequestBody UpdateOrderStatusRequest request) {
        Order updated = orderService.updateOrderStatus(orderId, request.getStatus(), request.getRemarks());
        return ResponseEntity.ok(updated);
    }
}
