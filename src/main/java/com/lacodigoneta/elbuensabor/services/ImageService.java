package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.entities.Image;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public interface ImageService {

    Image save(Object image) throws IOException;

    Image findExisting(String value);

    Image findById(UUID id);

    default boolean isImageFile(File file) {
        try (InputStream stream = new FileInputStream(file)) {
            ImageIO.read(stream);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
