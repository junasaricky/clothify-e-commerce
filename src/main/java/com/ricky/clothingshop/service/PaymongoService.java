package com.ricky.clothingshop.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.ricky.clothingshop.model.PaymentType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PaymongoService {

    @Value("${paymongo.secret.key}")
    private String secretKey;

    public String createCheckoutSession(int amount, Long orderId, PaymentType type, int quantity) {
        String paymentMethodType = type == PaymentType.GCASH ? "gcash" : "card";

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
                "line_items": [
                    {
                    "currency": "PHP",
                    "amount": %d,
                    "name": "Order #%d",
                    "quantity": %d
                    }
                ],
                "payment_method_types": ["%s"],
                "description": "Order ID: %d",
                "reference_number": "%d",
                "success_url": "https://clothify-e-commerce.onrender.com/thank-you",
                "cancel_url": "https://clothify-e-commerce.onrender.com/shop"
                }
            }
        }
        """.formatted(amount, orderId, quantity, paymentMethodType, orderId, orderId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(secretKey, "");

        HttpEntity<String> request = new HttpEntity<>(payload, headers);

        ResponseEntity<String> response = new RestTemplate()
            .postForEntity("https://api.paymongo.com/v1/checkout_sessions", request, String.class);

        if (response.getBody() == null) {
            throw new RuntimeException("No response from PayMongo");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("data").path("attributes").path("checkout_url").asText();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse PayMongo response");
        }
    }
}