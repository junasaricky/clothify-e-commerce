package com.ricky.clothingshop.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;       
    private String fullName;     
    private String phoneNumber; 
}
