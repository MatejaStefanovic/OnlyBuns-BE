package org.onlybuns.service;

import org.onlybuns.DTOs.LocationDTO;
import org.onlybuns.model.Location;
import org.onlybuns.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private OpenCageService openCageService;

    // Glavna metoda koja će se keširati
    @Cacheable(value = "location", key = "#lat.toPlainString() + '_' + #lng.toPlainString()")
    public Location getLocationInfo(BigDecimal lat, BigDecimal lng) {

        // Ako vrednost NIJE u Redis kešu, tek onda se izvršava ovaj blok:
        System.out.println("Pozivam OpenCage API jer nije bilo u Redis kešu...");

        LocationDTO locationData = openCageService.getLocationFromCoordinates(lat, lng);

        Location newLocation = new Location();
        // Važno: Ključ za bazu kreiraš i ovde, isti kao i za keš, da bi se lokacije ispravno čuvale
        newLocation.setCoordinateKey(String.format("%.4f_%.4f", lat, lng));
        newLocation.setCity(locationData.getCity());
        newLocation.setCountry(locationData.getCountry());
        newLocation.setStreet(locationData.getStreet());
        newLocation.setLatitude(lat);
        newLocation.setLongitude(lng);

        // Ovu liniju OSTAVLJAŠ, jer je ona odgovorna za *čuvanje* lokacije u bazu
        // To se dešava SAMO ako lokacija nije pronađena u Redis kešu
        locationRepository.save(newLocation);
        System.out.println("Lokacija preuzeta sa OpenCage API-ja, sačuvana u bazi i stavljena u Redis keš.");

        return newLocation;
    }

}
