package org.onlybuns.service;


import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class ImageCompressionService {


    @Value("${upload.path}")
    private String imageDirPath;
    @Scheduled(cron = "0 0 0 * * ?") // Pokreće se svakog dana u ponoć
    public void compressOldImages() {
        File imageDir = new File(imageDirPath);
        if (imageDir.exists() && imageDir.isDirectory()) {
            File[] files = imageDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    try {
                        if (isOlderThanOneMonth(file) && !isCompressed(file)) {
                            compressImage(file);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private boolean isOlderThanOneMonth(File file) {
        long fileTime = file.lastModified();
        long oneMonthAgo = Instant.now().minus(30, ChronoUnit.DAYS).toEpochMilli();
        return fileTime < oneMonthAgo;
    }

    private boolean isCompressed(File file) {
        // Proverava da li je fajl već kompresovana verzija (ima "-compressed" u imenu)
        if (file.getName().contains("-compressed")) {
            return true; // Fajl je već kompresovana verzija
        }

        // Ako fajl nije kompresovan, proverava da li postoji kompresovana verzija
        String compressedFileName = file.getName().replace(".", "-compressed.");
        File compressedFile = new File(file.getParent(), compressedFileName);
        return compressedFile.exists(); // Vraća true ako kompresovana verzija postoji
    }

    private void compressImage(File file) throws IOException {
        String compressedFilePath = file.getParent() + File.separator +
                file.getName().replace(".", "-compressed.");
        Thumbnails.of(file)
                .size(1024, 1024) // Prilagodite veličinu po potrebi
                .outputQuality(0.7) // Kvalitet kompresije
                .toFile(new File(compressedFilePath));

        System.out.println("Kompresovana slika: " + compressedFilePath);
    }

}
