package com.ricky.clothingshop.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class FrontendController {

    @GetMapping(value = {
        "/", 
        "/admin/**", 
        "/customer/**", 
        "/shop/**", 
        "/login", 
        "/register", 
        "/cart", 
        "/checkout", 
        "/dashboard"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
