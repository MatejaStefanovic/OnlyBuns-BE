package org.onlybuns.service;

import org.onlybuns.DTOs.LocationDTO;
import org.onlybuns.model.Location;
import org.onlybuns.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class LocationService {

    @Autowired
    private LocationRepository cacheRepository;

    @Autowired
    private OpenCageService openCageService;

    public Location getLocationInfo(BigDecimal lat, BigDecimal lng) {

        // 1. Napravi ključ za keš
        String key = String.format("%.4f_%.4f", lat, lng);

        // 2. Pogledaj u keš
        Optional<Location> cached = cacheRepository.findByCoordinateKey(key);

        if (cached.isPresent()) {
            System.out.println("Našao lokaciju u kešu!");
            Location cache = cached.get();
            return cache;
        }

        // 3. Nema u kešu - pozovi API
        System.out.println("Pozivam OpenCage API...");
        LocationDTO locationData = openCageService.getLocationFromCoordinates(lat, lng);

        // 4. Sačuvaj u keš
        Location newCache = new Location();
        newCache.setCoordinateKey(key);
        newCache.setCity(locationData.getCity());
        newCache.setCountry(locationData.getCountry());
        newCache.setStreet(locationData.getStreet());

        cacheRepository.save(newCache);
        System.out.println("Sačuvao u keš!");

        return newCache;
    }




}
