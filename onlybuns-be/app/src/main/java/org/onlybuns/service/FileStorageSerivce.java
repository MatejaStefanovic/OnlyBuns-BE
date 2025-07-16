package org.onlybuns.service;

import jakarta.transaction.Transactional;
import org.onlybuns.config.FileStorageProperties;
import org.onlybuns.model.Image;
import org.onlybuns.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageSerivce {

    private final Path fileStorageLocation;


    @Autowired
    private ImageRepository imageRepository;



    @Autowired
    public FileStorageSerivce(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }


    /*// --- Nova metoda za dohvaćanje slike iz baze i keširanje ---
    @Cacheable(value = "imageID", key = "#id")
    public Image getImageById(Long id) { // Koristi Long ako je ID tipa Long u bazi
        System.out.println("Dohvaćam sliku iz baze podataka: " + id);
        // Simuliraj kašnjenje da vidiš efekat keširanja
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found with id " + id));
    }*/

    @Transactional
    @Cacheable(value = "imageBase64", key = "#image.id")
    public String getImage (Image image) throws IOException {
        return image.setImageBase64(fileStorageLocation.toString());
    }

    @Transactional
    @CachePut(value = "image", key = "#result.id")
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


/*
    public void deleteFile(String fileName) {
        try {
            Path filePath = fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("Could not delete file: " + fileName, ex);
        }
    }*/
// --- Metoda za brisanje fajla i izbacivanje iz keša ---
@CacheEvict(value = "image", key = "#id") // Briše iz keša kada se metoda izvrši
@Transactional
public void deleteFile(String fileName, Long id) { // Dodat ID za brisanje iz keša
    try {
        Path filePath = fileStorageLocation.resolve(fileName).normalize();
        Files.deleteIfExists(filePath);
        imageRepository.deleteById(id); // Obriši i iz baze
        System.out.println("Obrisao fajl i izbacio sliku sa ID: " + id + " iz keša.");
    } catch (IOException ex) {
        throw new RuntimeException("Could not delete file: " + fileName, ex);
    }
}

}
