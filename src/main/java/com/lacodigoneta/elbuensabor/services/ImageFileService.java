package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.entities.Image;
import com.lacodigoneta.elbuensabor.repositories.ImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ImageFileService extends BaseServiceImpl<Image, ImageRepository> implements ImageService {

    @Value("${upload.dir}")
    private String uploadDir;

    public ImageFileService(ImageRepository repository) {
        super(repository);
    }

    @Override
    public Image save(Object image) {
        MultipartFile imageFile = (MultipartFile) image;
        String fileName = imageFile.getOriginalFilename();
        String fileExtension = "";
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0) {
            fileExtension = fileName.substring(dotIndex).toLowerCase();
        }
        if (!List.of(".jpg", ".png").contains(fileExtension)) {
            throw new RuntimeException("File not allowed");
        }
        fileName = UUID.randomUUID() + fileExtension;

        String projectDir = System.getProperty("user.dir");
        String location = projectDir + uploadDir + fileName;

        String calculatedHash = calculateHash(imageFile);
        Image existingImage = findExisting(calculatedHash);

        if (!Objects.isNull(existingImage)) {
            return existingImage;
        }
        Image newImage = Image.builder()
                .location(fileName)
                .hash(calculatedHash)
                .build();

        Path path = Paths.get(location);
        if (!Files.exists(path)) {
            try {

                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException("Directory don't created");
            }
        }

        try {
            Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioException) {
            throw new RuntimeException("Could not store the file. Error: " + ioException.getMessage());
        }
        return repository.save(newImage);

    }

    @Override
    public Image findExisting(String value) {
        return repository.findByHash(value);
    }

    private String calculateHash(MultipartFile file) {

        try {
            byte[] bytes = file.getBytes();
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(bytes);
            return bytesToHexString(digest);
        } catch (IOException ioException) {
            throw new RuntimeException("Error when getting the bytes of the image");
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            throw new RuntimeException("Unable to find the algorithm MD5");
        }
    }

    public String calculateHash(String fileName) {

        try {
            byte[] bytes = fileName.getBytes();
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(bytes);
            return bytesToHexString(digest);
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            throw new RuntimeException("Unable to find the algorithm MD5");
        }

    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }

    @Override
    public Image changeStates(Image source, Image destination) {
        return null;
    }

    @Override
    public void beforeSaveValidations(Image entity) {

    }
}
