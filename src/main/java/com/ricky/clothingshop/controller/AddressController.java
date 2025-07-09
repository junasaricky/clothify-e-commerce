package com.ricky.clothingshop.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ricky.clothingshop.model.Address;
import com.ricky.clothingshop.security.JwtUtil;
import com.ricky.clothingshop.service.AddressService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/address")
public class AddressController {

    private final AddressService service;
    private final JwtUtil jwtUtil;

    // Get all addresses of logged-in user
    @GetMapping
    public ResponseEntity<?> getAddresses(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(token);

            if (username == null || username.isBlank()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }

            List<Address> addresses = service.getUserAddresses(username);
            return ResponseEntity.ok(addresses);
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Failed to fetch addaaress: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Address> getAddressById(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        String username = jwtUtil.extractUsername(authHeader.replace("Bearer ", ""));
        return ResponseEntity.ok(service.getAddressById(username, id));
    }

    // Add new address
    @PostMapping
    public ResponseEntity<Address> addAddress(@RequestBody Address address, @RequestHeader("Authorization") String authHeader) {
        String username = jwtUtil.extractUsername(authHeader.replace("Bearer ", ""));
        return ResponseEntity.ok(service.addAddress(username, address));
    }

    // Update address
    @PutMapping("/{id}")
    public ResponseEntity<Address> updateAddress(@PathVariable Long id, @RequestBody Address updatedAddress) {
        return ResponseEntity.ok(service.updateAddress(id, updatedAddress));
    }

    // Delete address
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        service.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }

}
