package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.entities.Image;
import com.lacodigoneta.elbuensabor.repositories.ImageRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ImageUrlService extends BaseServiceImpl<Image, ImageRepository> implements ImageService {

    public ImageUrlService(ImageRepository repository) {
        super(repository);
    }

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
        return repository.findByLocation(value);
    }

    @Override
    public Image changeStates(Image source, Image destination) {
        return null;
    }

    @Override
    public void beforeSaveValidations(Image entity) {

    }
}
