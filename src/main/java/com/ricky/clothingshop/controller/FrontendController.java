package com.ricky.clothingshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping({
        "/", 
        "/login",
        "/register",
        "/forgot-password",
        "/reset-password",
        "/shop",
        "/shop/**",
        "/cart",
        "/checkout",
        "/thank-you",
        "/payment/**",
        "/my-orders",
        "/address/**",
        "/account",
        "/account/settings",
        "/admin/**"
    })
    public String forward() {
        return "forward:/index.html";
    }
}

