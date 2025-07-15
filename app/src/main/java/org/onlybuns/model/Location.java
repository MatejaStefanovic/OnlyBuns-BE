package org.onlybuns.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "location")
public class Location  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    private String coordinateKey;
    private String country;
    private String city;
    private String street;
    private BigDecimal latitude;
    private BigDecimal longitude;

    @CreationTimestamp
    private LocalDateTime cachedAt;



    public Location(){}

    public Location( String country, String street, String city) {
        this.country = country;
        this.street = street;
        this.city = city;
    }

    public Location( String country, String street, String city, String coordinateKey ) {
        this.country = country;
        this.street = street;
        this.city = city;
        this.coordinateKey = coordinateKey;
    }

    public long getId() {
        return id;
    }


    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public String getCoordinateKey() {
        return coordinateKey;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setCoordinateKey(String coordinateKey) {
        this.coordinateKey = coordinateKey;
    }

    public static String createKey(BigDecimal lat, BigDecimal lng) {
        return String.format("%.4f_%.4f", lat, lng);
    }
}
