package com.ricky.clothingshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
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
