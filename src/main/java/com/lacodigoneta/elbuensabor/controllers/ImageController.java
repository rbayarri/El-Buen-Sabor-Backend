package com.lacodigoneta.elbuensabor.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/images")
@AllArgsConstructor
public class ImageController {

//    private ImageServiceFactory imageServiceFactory;
//
//    private ImageMapper mapper;
//
//    @PostMapping("")
//    public ResponseEntity<ImageDto> saveImage(@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam(value = "url", required = false) String url) throws IOException {
//        ImageService imageService = imageServiceFactory.getObject(Objects.nonNull(file));
//        Image saved = imageService.save(Objects.nonNull(file) ? file : url);
//        return ResponseEntity.ok(mapper.toImageDto(saved));
//    }
}
