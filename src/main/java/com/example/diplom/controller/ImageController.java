package com.example.diplom.controller;


import com.example.diplom.models.Image;
import com.example.diplom.repository.ImageRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@AllArgsConstructor
public class ImageController {
    private ImageRepository imageRepository;

    @GetMapping("/images/{id}")
    @Transactional
    public ResponseEntity<byte[]> getImageById(@PathVariable Long id) {
        Image image = imageRepository.findById(id).orElseThrow(()->new RuntimeException("Картинки нет"));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getContentType()))
                .body(image.getBytes());
    }
}
