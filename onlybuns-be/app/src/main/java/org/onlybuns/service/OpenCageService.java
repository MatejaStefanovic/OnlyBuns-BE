package org.onlybuns.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.onlybuns.DTOs.LocationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
public class OpenCageService {

    // Stavite vaš API ključ ovde (ili u application.properties)
    private final String API_KEY = "c9514f3f109d49aaaf3d7dc0a79ed9f3";

    @Autowired
    private RestTemplate restTemplate;

    // RestTemplate bean (dodati u main klasu)
    public OpenCageService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public LocationDTO getLocationFromCoordinates(BigDecimal lat, BigDecimal lng) {
        try {
            // Napravite URL
            String url = String.format(
                    "https://api.opencagedata.com/geocode/v1/json?q=%s+%s&key=%s",
                    lat.toString(), lng.toString(), API_KEY
            );

            System.out.println("Pozivam OpenCage API: " + url);

            // Pozovite API
            String response = restTemplate.getForObject(url, String.class);

            // Parsirajte JSON odgovor
            return parseOpenCageResponse(response);

        } catch (Exception e) {
            System.err.println("Greška pri pozivanju OpenCage API: " + e.getMessage());
            return new LocationDTO("Unknown", "Unknown", "Unknown");
        }
    }

    private LocationDTO parseOpenCageResponse(String jsonResponse) {
        try {
            // Jednostavno parsiranje JSON-a (možete koristiti Jackson ili Gson)
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            if (root.has("results") && root.get("results").size() > 0) {
                JsonNode components = root.get("results").get(0).get("components");

                String city = getJsonValue(components, "city", "town", "village");
                String country = getJsonValue(components, "country");
                String street = getJsonValue(components, "road");

                return new LocationDTO(country, street, city);
            }

        } catch (Exception e) {
            System.err.println("Greška pri parsiranju JSON-a: " + e.getMessage());
        }

        return new LocationDTO("Unknown", "Unknown", "Unknown");
    }

    private String getJsonValue(JsonNode node, String... keys) {
        for (String key : keys) {
            if (node.has(key) && !node.get(key).isNull()) {
                return node.get(key).asText();
            }
        }
        return "";
    }




}
