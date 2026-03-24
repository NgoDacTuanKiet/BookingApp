package com.bookingapp.model;

import java.util.List;

public class Room {
    int id;
    int hotelId;
    String roomType;
    double price;
    int capacity;
    int quantity;
    String description;

    List<RoomImage> images;

    public Room() {
    }

    public Room(int id, int hotelId, String roomType, double price, int capacity, int quantity, String description, List<RoomImage> images) {
        this.id = id;
        this.hotelId = hotelId;
        this.roomType = roomType;
        this.price = price;
        this.capacity = capacity;
        this.quantity = quantity;
        this.description = description;
        this.images = images;
    }

    public int getId() {
        return id;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<RoomImage> getImages() {
        return images;
    }

    public void setImages(List<RoomImage> images) {
        this.images = images;
    }
}
