package com.ricky.clothingshop.service;

import com.ricky.clothingshop.model.Order;
import com.ricky.clothingshop.model.OrderItem;
import com.ricky.clothingshop.model.OrderStatus;
import com.ricky.clothingshop.model.PaymentStatus;
import com.ricky.clothingshop.model.PaymentType;
import com.ricky.clothingshop.model.User;
import com.ricky.clothingshop.repository.OrderItemRepository;
import com.ricky.clothingshop.repository.OrderRepository;
import com.ricky.clothingshop.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.ricky.clothingshop.model.Address;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final UserRepository userRepo;

    public OrderService(OrderRepository orderRepo, OrderItemRepository orderItemRepo, UserRepository userRepo) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.userRepo = userRepo;
    }

    public Order placeOrder(String username, List<OrderItem> items, PaymentType paymentType, Address deliveryAddress) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(new Date());
        order.setItems(items);
        order.setPaymentType(paymentType); 
        order.setAddress(deliveryAddress); 
        order.setStatusUpdatedAt(new Date());
        order.setTotal(
                items.stream().mapToDouble(OrderItem::getPrice).sum()
        );

        if (paymentType == PaymentType.CASH_ON_DELIVERY) {
            order.setPaymentStatus(PaymentStatus.UNPAID);
            order.setStatus(OrderStatus.PENDING);
        } else {
            order.setPaymentStatus(PaymentStatus.UNPAID); 
            order.setStatus(OrderStatus.PENDING);   
        }

        Order savedOrder = orderRepo.save(order);

        for (OrderItem item : items) {
            item.setOrder(savedOrder);
            orderItemRepo.save(item);
        }

        savedOrder.setItems(items);
        return savedOrder;
    }

    @Transactional
    public List<Order> getOrdersByUser(String username) {
        return orderRepo.findByUserUsername(username);
    }

    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    public Order updateOrderStatus(Long orderId, OrderStatus newStatus, String remarks) {
        Order order = orderRepo.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(newStatus);
        order.setStatusUpdatedAt(new Date());
        order.setRemarks(remarks);
        
        // Automatically mark COD as PAID if status is DELIVERED
        if (order.getPaymentType() == PaymentType.CASH_ON_DELIVERY
            && newStatus == OrderStatus.DELIVERED) {
            order.setPaymentStatus(PaymentStatus.PAID);
        }

        return orderRepo.save(order);
    }

    public Order getOrderById(Long orderId) {
        return orderRepo.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
    }

}
