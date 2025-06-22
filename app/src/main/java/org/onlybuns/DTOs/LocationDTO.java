package org.onlybuns.DTOs;

import org.onlybuns.model.Location;

import java.math.BigDecimal;

public class LocationDTO {
	private String country;
    private String city;
    private String street;
    



    public LocationDTO(Location location) {
    	this.country = location.getCountry();
    	this.city = location.getCity();
    	this.street = location.getStreet();
    }

    public LocationDTO(String country, String street, String city) {
        this.country = country;
        this.street = street;
        this.city = city;
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
