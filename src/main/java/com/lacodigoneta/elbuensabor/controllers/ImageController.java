package com.lacodigoneta.elbuensabor.controllers;

import com.lacodigoneta.elbuensabor.services.ImageServiceFactory;
import com.lacodigoneta.elbuensabor.dto.ImageDto;
import com.lacodigoneta.elbuensabor.entities.Image;
import com.lacodigoneta.elbuensabor.mappers.ImageMapper;
import com.lacodigoneta.elbuensabor.services.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/images")
@AllArgsConstructor
public class ImageController {

    private ImageServiceFactory imageServiceFactory;

    private ImageMapper mapper;

    @PostMapping("")
    public ResponseEntity<ImageDto> saveImage(@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam(value = "url", required = false) String url) {
        ImageService imageService = imageServiceFactory.getObject(Objects.nonNull(file));
        Image saved = imageService.save(Objects.nonNull(file) ? file : url);
        return ResponseEntity.ok(mapper.toImageDto(saved));
    }
}
