package com.lacodigoneta.elbuensabor.mappers;

import com.lacodigoneta.elbuensabor.dto.ImageDto;
import com.lacodigoneta.elbuensabor.entities.Image;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ImageMapper {

    private final ModelMapper mapper;

    public ImageDto toImageDto(Image image) {
        ImageDto imageDto = mapper.map(image, ImageDto.class);
        if (Objects.nonNull(image.getHash())) {
            URI uri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/uploads/images/" + imageDto.getLocation())
                    .build()
                    .toUri();
            imageDto.setLocation(uri.toString());
        }
        return imageDto;
    }

    public Image toEntity(ImageDto imageDto) {
        if (Objects.isNull(imageDto)) {
            return null;
        }
        return mapper.map(imageDto, Image.class);
    }
}
