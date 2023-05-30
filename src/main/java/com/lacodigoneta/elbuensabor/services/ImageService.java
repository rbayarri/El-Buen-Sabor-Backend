package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.entities.Image;

import java.util.UUID;

public interface ImageService {

    Image save(Object image);

    Image findExisting(String value);

    Image findById(UUID id);

}
