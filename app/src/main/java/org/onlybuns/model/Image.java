package org.onlybuns.model;

import jakarta.persistence.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String relativePath;


    @Lob
    @Column(columnDefinition = "TEXT")
    private String imageBase64;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }
    public Image(){

    }

    public String getImageBase64() {
        return imageBase64;
    }

   
   public String setImageBase64(String uploadPath) throws IOException {
       if (relativePath != null) {
           Path filePath = Paths.get(uploadPath, relativePath);
           if (Files.exists(filePath)) {
               byte[] imageBytes = Files.readAllBytes(filePath);
               imageBase64= Base64.getEncoder().encodeToString(imageBytes);
               return imageBase64;
           } else {
               throw new FileNotFoundException("File not found: " + filePath);
           }
       }
       return null;
   }




}
