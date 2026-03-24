package com.bookingapp.model;

public class Hotel {
    int id;
    String name;
    String address;
    String city;
    String description;

    public Hotel() {
    }

    public Hotel(int id, String name, String address, String city, String description) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
