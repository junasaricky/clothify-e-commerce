package com.ricky.clothingshop.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ricky.clothingshop.model.Order;
import com.ricky.clothingshop.model.PaymentStatus;
import com.ricky.clothingshop.model.OrderStatus;
import com.ricky.clothingshop.repository.OrderRepository;
import com.ricky.clothingshop.service.EmailService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final OrderRepository orderRepo;
    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    @PostMapping("/paymongo")
    public ResponseEntity<Void> handlePaymongoWebhook(@RequestBody String payload) {
        try {
            System.out.println("📩 Received webhook: " + payload);

            JsonNode root = objectMapper.readTree(payload);
            String eventType = root.path("data").path("attributes").path("type").asText();

            System.out.println("🔍 Webhook eventType: " + eventType);

            if (!"payment.paid".equals(eventType)) {
                System.out.println("⚠️ Not a payment.paid event, ignoring.");
                return ResponseEntity.ok().build();
            }

            JsonNode paymentAttributes = root.path("data").path("attributes").path("data").path("attributes");
            String status = paymentAttributes.path("status").asText();
            String description = paymentAttributes.path("description").asText();

            System.out.println("💰 Payment status: " + status);
            System.out.println("📝 Description: " + description);

            if (!"paid".equals(status)) {
                return ResponseEntity.ok().build(); // Not yet paid
            }

            if (description != null && description.startsWith("Order ID:")) {
                Long orderId = Long.parseLong(description.replace("Order ID:", "").trim());
                Order order = orderRepo.findById(orderId).orElse(null);

                if (order != null && order.getPaymentStatus() == PaymentStatus.UNPAID) {
                    order.setPaymentStatus(PaymentStatus.PAID);
                    order.setStatus(OrderStatus.PROCESSING);
                    orderRepo.save(order);

                    emailService.sendPaymentConfirmation(order);
                    System.out.println("✅ Order #" + orderId + " marked as PAID.");
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

