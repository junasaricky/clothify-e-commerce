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
            System.out.println("📩 Received webhook: " + payload);

            JsonNode json = objectMapper.readTree(payload);
            String type = json.path("data").path("type").asText();
            System.out.println("🔍 Webhook type: " + type);

            if (!"payment".equals(type)) {
                System.out.println("⚠️ Not a payment event, ignoring.");
                return ResponseEntity.ok().build();
            }

            JsonNode attributes = json.path("data").path("attributes");
            String status = attributes.path("status").asText();
            String description = attributes.path("description").asText();

            System.out.println("💰 Payment status: " + status);
            System.out.println("📝 Description: " + description);

            if (!status.equals("paid")) {
                System.out.println("🕒 Payment not yet paid, ignoring.");
                return ResponseEntity.ok().build();
            }

            if (description != null && description.startsWith("Order ID:")) {
                Long orderId = Long.parseLong(description.replace("Order ID:", "").trim());
                System.out.println("📦 Order ID from description: " + orderId);

                Order order = orderRepo.findById(orderId).orElse(null);

                if (order != null) {
                    System.out.println("📬 Order found: " + order.getId());
                    if (order.getPaymentStatus() == PaymentStatus.UNPAID) {
                        order.setPaymentStatus(PaymentStatus.PAID);
                        order.setStatus(OrderStatus.PROCESSING);
                        orderRepo.save(order);
                        System.out.println("✅ Order #" + orderId + " marked as PAID.");
                    } else {
                        System.out.println("🔁 Order already marked as paid.");
                    }
                } else {
                    System.out.println("❌ Order not found.");
                }
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("💥 Webhook processing failed:");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}

