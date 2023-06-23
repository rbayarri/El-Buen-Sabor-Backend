package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.entities.Image;
import com.lacodigoneta.elbuensabor.repositories.ImageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ImageUrlService implements ImageService {

    private final ImageRepository repository;

    @Override
    public Image save(Object image) {

        String url = image.toString();

        Image existing = findExisting(url);
        if(Objects.nonNull(existing)){
            return existing;
        }
        Image newImage = Image.builder()
                .location(url)
                .build();

        return repository.save(newImage);
    }

    @Override
    public Image findExisting(String value) {
        return repository.findByLocationEquals(value);
    }

    @Override
    public Image findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Entity not found"));

    }
}
