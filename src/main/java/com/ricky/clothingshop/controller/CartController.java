package com.ricky.clothingshop.controller;

import com.ricky.clothingshop.dto.CartRequest;
import com.ricky.clothingshop.model.CartItem;
import com.ricky.clothingshop.model.Order;
import com.ricky.clothingshop.model.OrderItem;
import com.ricky.clothingshop.model.PaymentType;
import com.ricky.clothingshop.model.Product;
import com.ricky.clothingshop.repository.AddressRepository;
import com.ricky.clothingshop.repository.CartItemRepository;
import com.ricky.clothingshop.repository.CartRepository;
import com.ricky.clothingshop.repository.ProductRepository;
import com.ricky.clothingshop.repository.UserRepository;
import com.ricky.clothingshop.model.Address;
import com.ricky.clothingshop.model.User;
import com.ricky.clothingshop.security.JwtUtil;
import com.ricky.clothingshop.service.CartService;
import com.ricky.clothingshop.service.OrderService;
import com.ricky.clothingshop.service.PaymongoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final OrderService orderService;
    private final AddressRepository addressRepo;
    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;
    private final ProductRepository productRepo;
    private final PaymongoService paymongoService;

    public CartController(
        CartService cartService,
        OrderService orderService,
        AddressRepository addressRepo,
        UserRepository userRepo,
        CartRepository cartRepo,
        CartItemRepository cartItemRepo,
        JwtUtil jwtUtil,
        ProductRepository productRepo,
        PaymongoService paymongoService
    ) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.addressRepo = addressRepo;
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
        this.productRepo = productRepo;
        this.paymongoService = paymongoService;
    }

    @GetMapping("/view")
    public ResponseEntity<List<CartItem>> viewCart(@RequestHeader("Authorization") String authHeader) {
        try {
            String username = jwtUtil.extractUsername(authHeader.replace("Bearer ", ""));
            List<CartItem> cartItems = cartService.getCartItems(username);
            return ResponseEntity.ok(cartItems);
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addToCart(@RequestBody CartRequest request, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(token);
        cartService.addToCart(username, request.getProductId(), request.getQuantity());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Added to cart");

        return ResponseEntity.ok(response);
    }

    // Preview cart items (before checkout)
    @GetMapping("/checkout-preview")
    public ResponseEntity<Map<String, Object>> previewCheckout(@RequestHeader("Authorization") String authHeader) {
        String username = jwtUtil.extractUsername(authHeader.replace("Bearer ", ""));
        User user = userRepo.findByUsername(username).orElseThrow();

        List<CartItem> cartItems = cartService.getCartItems(username);
        Address address = addressRepo.findFirstByUser(user).orElse(null); // safe even if address is missing

        Map<String, Object> response = new HashMap<>();
        response.put("items", cartItems);
        response.put("address", address);

        return ResponseEntity.ok(response);
    }

    // Place order
    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam PaymentType paymentType,
        @RequestParam Long addressId,
        @RequestBody List<Map<String, Object>> selectedItems
    ) {
        try {
            String username = jwtUtil.extractUsername(authHeader.replace("Bearer ", ""));

            if (cartService.getCartItems(username).isEmpty()) {
                throw new RuntimeException("Cart already empty. Order already placed?");
            }

            Address deliveryAddress = addressRepo.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

            List<OrderItem> orderItems = new ArrayList<>();
            
            for (Map<String, Object> item : selectedItems) {
                Long productId = Long.valueOf(item.get("productId").toString());
                int quantity = Integer.parseInt(item.get("quantity").toString());

                Product product = productRepo.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

                OrderItem orderItem = new OrderItem();
                orderItem.setProduct(product);
                orderItem.setQuantity(quantity);

                double productPrice = product.isHasDiscount()
                    ? product.getDiscountedPrice()
                    : product.getPrice();
                orderItem.setPrice(productPrice * quantity);

                orderItems.add(orderItem);
            }

            Order order = orderService.placeOrder(username, orderItems, paymentType, deliveryAddress);

            List<Long> orderedProductIds = orderItems.stream()
                .map(item -> item.getProduct().getId())
                .collect(Collectors.toList());
            cartService.removeCartItemsByProductIds(username, orderedProductIds);

            // Handle PayMongo Redirect
            String redirectUrl = null;
            if (paymentType == PaymentType.GCASH || 
                paymentType == PaymentType.CARD || 
                paymentType == PaymentType.GRAB_PAY) {

                redirectUrl = paymongoService.createCheckoutSession(orderItems, order.getId(), paymentType);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("order", order);
            response.put("redirect_url", redirectUrl);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/buy-now")
    public ResponseEntity<Map<String, Object>> buyNowCheckout(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam PaymentType paymentType,
        @RequestParam Long addressId,
        @RequestBody List<Map<String, Object>> selectedItems
    ) {
        try {
            String username = jwtUtil.extractUsername(authHeader.replace("Bearer ", ""));
            
            Address deliveryAddress = addressRepo.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

            List<OrderItem> orderItems = new ArrayList<>();

            for (Map<String, Object> item : selectedItems) {
                Long productId = Long.valueOf(item.get("productId").toString());
                int quantity = Integer.parseInt(item.get("quantity").toString());

                Product product = productRepo.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

                OrderItem orderItem = new OrderItem();
                orderItem.setProduct(product);
                orderItem.setQuantity(quantity);
                double productPrice = product.isHasDiscount()
                    ? product.getDiscountedPrice()
                    : product.getPrice();
                orderItem.setPrice(productPrice * quantity);

                orderItems.add(orderItem);
            }

            Order order = orderService.placeOrder(username, orderItems, paymentType, deliveryAddress);

            List<Long> orderedProductIds = orderItems.stream()
                .map(item -> item.getProduct().getId())
                .collect(Collectors.toList());
            cartService.removeCartItemsByProductIds(username, orderedProductIds);

            String redirectUrl = null;
            if (paymentType == PaymentType.GCASH || 
                paymentType == PaymentType.CARD || 
                paymentType == PaymentType.GRAB_PAY) {

                redirectUrl = paymongoService.createCheckoutSession(orderItems, order.getId(), paymentType);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("order", order);
            response.put("redirect_url", redirectUrl);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update-quantity/{itemId}")
        public ResponseEntity<Void> updateQuantity(@PathVariable Long itemId, @RequestParam int quantity) {
            cartService.updateCartItemQuantity(itemId, quantity);
            return ResponseEntity.ok().build();
        }
}
