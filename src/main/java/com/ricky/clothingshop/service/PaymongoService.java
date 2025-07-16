package com.ricky.clothingshop.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.ricky.clothingshop.model.PaymentType;
import com.ricky.clothingshop.model.OrderItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@Service
public class PaymongoService {

    @Value("${paymongo.secret.key}")
    private String secretKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String createCheckoutSession(List<OrderItem> orderItems, Long orderId, PaymentType type) {
        StringBuilder lineItemsBuilder = new StringBuilder();
        for (int i = 0; i < orderItems.size(); i++) {
            OrderItem item = orderItems.get(i);
            String name = item.getProduct().getName().replace("\"", ""); // sanitize name
            int amount = (int) (item.getPrice() / item.getQuantity() * 100); // price per unit in centavos
            int quantity = item.getQuantity();

            lineItemsBuilder.append("""
                {
                    "currency": "PHP",
                    "amount": %d,
                    "name": "%s",
                    "quantity": %d
                }
            """.formatted(amount, name, quantity));

            if (i < orderItems.size() - 1) {
                lineItemsBuilder.append(",\n");
            }
        }

        String paymentMethodType = switch (type) {
            case GCASH -> "gcash";
            case CARD -> "card";
            case GRAB_PAY -> "grab_pay";
            default -> throw new IllegalArgumentException("Unsupported payment method: " + type);
        };

        String payload = """
        {
            "data": {
                "attributes": {
                    "billing": {
                        "name": "Customer",
                        "email": "sample@email.com"
                    },
                    "send_email_receipt": false,
                    "show_description": true,
                    "show_line_items": true,
                    "line_items": [%s],
                    "payment_method_types": ["%s"],
                    "description": "Order ID: %d",
                    "reference_number": "%d",
                    "success_url": "https://clothify-e-commerce.onrender.com/thank-you",
                    "cancel_url": "https://clothify-e-commerce.onrender.com/shop"
                }
            }
        }
        """.formatted(lineItemsBuilder, paymentMethodType, orderId, orderId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(secretKey, "");

        HttpEntity<String> request = new HttpEntity<>(payload, headers);

        ResponseEntity<String> response = new RestTemplate()
                .postForEntity("https://api.paymongo.com/v1/checkout_sessions", request, String.class);

        if (response.getBody() == null) {
            throw new RuntimeException("No response from PayMongo");
        }

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("data").path("attributes").path("checkout_url").asText();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse PayMongo response");
        }
    }
}