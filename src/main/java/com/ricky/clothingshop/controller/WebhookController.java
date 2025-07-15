package com.ricky.clothingshop.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ricky.clothingshop.model.Order;
import com.ricky.clothingshop.model.PaymentStatus;
import com.ricky.clothingshop.model.OrderStatus;
import com.ricky.clothingshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final OrderRepository orderRepo;
    private final ObjectMapper objectMapper;

    @PostMapping("/paymongo")
    public ResponseEntity<Void> handlePaymongoWebhook(@RequestBody String payload) {
        try {
            JsonNode json = objectMapper.readTree(payload);
            String type = json.path("data").path("type").asText();

            if (!"payment".equals(type)) {
                return ResponseEntity.ok().build(); // Ignore non-payment events
            }

            JsonNode attributes = json.path("data").path("attributes");
            String status = attributes.path("status").asText();
            String description = attributes.path("description").asText(); // Use this to get order ID

            if (!status.equals("paid")) {
                return ResponseEntity.ok().build(); // Ignore unpaid
            }

            // Sample: description = "orderId:123"
            if (description != null && description.startsWith("orderId:")) {
                Long orderId = Long.parseLong(description.replace("orderId:", ""));
                Order order = orderRepo.findById(orderId).orElse(null);

                if (order != null && order.getPaymentStatus() == PaymentStatus.UNPAID) {
                    order.setPaymentStatus(PaymentStatus.PAID);
                    order.setStatus(OrderStatus.PROCESSING);
                    orderRepo.save(order);
                    System.out.println("✅ Order #" + orderId + " marked as PAID.");
                }
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}

