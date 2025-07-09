package com.ricky.clothingshop.controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.io.IOException;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;


@RestController
@RequestMapping("/api/admin")
public class AdminUploadController {

    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        // Reject if file is not image
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body("Invalid file type. Only images are allowed.");
        }

        // Check if file exceeds 5MB
        if (file.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.badRequest().body("File too large");
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // Save to src/main/resources/static/images
        Path imagesDir = Paths.get("src/main/resources/static/images");
        // ensure dir exists
        Files.createDirectories(imagesDir); 

        Path filePath = imagesDir.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return ResponseEntity.ok("/images/" + filename); 
    }

}
