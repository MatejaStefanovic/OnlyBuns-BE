package org.onlybuns.service;

import jakarta.transaction.Transactional;
import org.onlybuns.config.FileStorageProperties;
import org.onlybuns.model.Image;
import org.onlybuns.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

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
/*
    @Transactional
    public String getImageBase64ForImage(Image image) throws IOException {
        return image.setImageBase64(fileStorageLocation.toString());
    }
    @Transactional
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

    public Image editFile(MultipartFile newFile, Image oldImage) {
        // 1. Obriši stari fajl (ako postoji)
        if (oldImage != null && oldImage.getRelativePath() != null) {
            deleteFile(oldImage.getRelativePath());
            imageRepository.delete(oldImage); // opcionalno: ako koristiš cascading, možeš izostaviti
        }

        // 2. Sačuvaj novi fajl
        return storeFile(newFile);
    }

    public void deleteFile(String fileName) {
        try {
            Path filePath = fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("Could not delete file: " + fileName, ex);
        }
    }
*/


    /**
     * Dohvata Base64 string slike iz datoteke na disku.
     * Ova metoda nije kešabilna jer služi za popunjavanje transient polja Image objekta
     * ili za direktno vraćanje sadržaja datoteke. Keširanje se vrši na nivou Image entiteta.
     * @param image Objekat slike čije Base64 podatke treba popuniti.
     * @return Base64 string slike.
     * @throws IOException ako dođe do greške pri čitanju datoteke.
     */
    @Transactional
    public String getImageBase64ForImage(Image image) throws IOException {
        return image.setImageBase64(fileStorageLocation.toString());
    }

    /**
     * Čuva datoteku na disku i metapodatke slike u bazi podataka.
     * Invalidira sve unose u kešu "images" kako bi se osigurala konzistentnost.
     * @param file MultipartFile za čuvanje.
     * @return Sačuvani objekat slike.
     * @throws RuntimeException ako dođe do greške pri čuvanju datoteke.
     */
    @Transactional
    @CacheEvict(value = "images", allEntries = true) // Invalidira ceo keš "images"
    public Image storeFile(MultipartFile file) {
        String fileName = null;
        try {
            // Normalizuj ime datoteke
            fileName = file.getOriginalFilename();
            if (fileName == null || fileName.contains("..")) {
                throw new IOException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Kopiraj datoteku na ciljanu lokaciju
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);

            // Sačuvaj relativnu putanju u bazi podataka
            Image image = new Image();
            image.setRelativePath(fileName);
            return imageRepository.save(image);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    /**
     * Briše datoteku sa diska.
     * Ova metoda ne invalidira keš direktno, jer se poziva unutar drugih metoda
     * koje već imaju `@CacheEvict` (npr. `deleteImageById`).
     * @param fileName Ime datoteke za brisanje.
     * @throws RuntimeException ako dođe do greške pri brisanju datoteke.
     */
    public void deleteFile(String fileName) {
        try {
            Path filePath = fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("Could not delete file: " + fileName, ex);
        }
    }

    /**
     * Dohvata objekat slike po ID-u i kešira ga.
     * Keš se zove "images", a ključ keša je ID slike.
     * @param id ID slike.
     * @return Optional objekat slike.
     */
    @Cacheable(value = "images", key = "#id")
    public Optional<Image> getImageById(Long id) {
        System.out.println("Fetching Image from DB for ID: " + id); // Za testiranje
        return imageRepository.findById(id);
    }

    /**
     * Briše sliku po ID-u (i datoteku sa diska i metapodatke iz baze)
     * i invalidira odgovarajući unos u kešu "images".
     * @param id ID slike za brisanje.
     * @throws RuntimeException ako slika nije pronađena ili dođe do greške pri brisanju.
     */
    @Transactional
    @CacheEvict(value = "images", key = "#id") // Invalidira specifični unos pri brisanju
    public void deleteImageById(Long id) {
        Optional<Image> imageOptional = imageRepository.findById(id);
        if (imageOptional.isPresent()) {
            Image image = imageOptional.get();
            if (image.getRelativePath() != null) {
                deleteFile(image.getRelativePath()); // Briše datoteku sa diska
            }
            imageRepository.delete(image); // Briše metapodatke slike iz baze
        } else {
            throw new RuntimeException("Image not found for ID: " + id);
        }
    }
}
