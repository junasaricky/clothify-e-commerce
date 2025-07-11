package com.ricky.clothingshop.model;

import java.time.LocalDateTime;

import jakarta.persistence.*; 
import lombok.*; 

@Entity
@Table(name = "\"user\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id @GeneratedValue
    private Long id;

    private String username;
    private String password;
    private String role;

    private String email;        
    private String fullName;      
    private String phoneNumber;
    
    private String passwordResetToken;
    private LocalDateTime passwordResetTokenExpiry;

    @Column(name = "profile_image", columnDefinition = "LONGTEXT")
    private String profileImage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
