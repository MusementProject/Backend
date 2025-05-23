package com.musement.backend.controllers;

import com.musement.backend.models.Image;
import com.musement.backend.services.CloudinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final CloudinaryService cloudinaryService;

    public MediaController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Image> uploadImage(
            @RequestParam("file") MultipartFile file) throws IOException {

        String originalName = file.getOriginalFilename();
        String format = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf('.') + 1)
                : "jpg";

        Image saved = cloudinaryService.uploadAndSave(file.getBytes(), format);
        return ResponseEntity.ok(saved);
    }
}