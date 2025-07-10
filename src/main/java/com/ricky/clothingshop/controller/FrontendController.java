package com.ricky.clothingshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping({
        "/", 
        "/login",
        "/register",
        "/dashboard",
        "/shop/**",
        "/admin/**",
        "/customer/**",
        "/cart",
        "/checkout"
    })
    public String forward() {
        return "forward:/index.html";
    }
}

