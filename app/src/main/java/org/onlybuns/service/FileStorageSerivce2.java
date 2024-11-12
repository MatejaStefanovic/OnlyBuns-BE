package org.onlybuns.service;

import org.onlybuns.config.FileStorageProperties;
import org.onlybuns.model.Image;
import org.onlybuns.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Service
public class FileStorageSerivce2 {

    private final Path fileStorageLocation;


    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    public FileStorageSerivce2(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }
    public String getImageBase64ForImage(Image image) throws IOException {
        return image.setImageBase64(fileStorageLocation.toString());
    }
    public Image storeFile(MultipartFile file) {
        String fileName = null;
        try {
            // Normalize file name
            fileName = file.getOriginalFilename();

            // Copy file to the target location
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);
            // Save relative path in the database
            Image image = new Image();
            image.setRelativePath(fileName);
            return imageRepository.save(image);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }


}
