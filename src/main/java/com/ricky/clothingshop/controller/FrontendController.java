package com.ricky.clothingshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping(value = { "/", "/{path:^(?!api|images|static|error).*}" })
    public String redirect() {
        return "forward:/index.html";
    }
}
