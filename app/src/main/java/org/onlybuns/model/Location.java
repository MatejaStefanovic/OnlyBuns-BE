package org.onlybuns.model;

import jakarta.persistence.*;

@Entity
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String country;
    private String city;
    private String street;

    public Location(){}

    public Location( String country, String street, String city) {
        this.country = country;
        this.street = street;
        this.city = city;
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

    public void setStreet(String street) {
        this.street = street;
    }
}
