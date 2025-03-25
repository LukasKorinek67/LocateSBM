package com.korinek.locate_sbm.model;


public class LocalizedRoom {

    private Room room;

    // probability of user being placed in this room
    private double locationProbability;

    public LocalizedRoom(Room room, double probability) {
        this.room = room;
        this.locationProbability = probability;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public double getLocationProbability() {
        return locationProbability;
    }

    public void setLocationProbability(double locationProbability) {
        this.locationProbability = locationProbability;
    }
}
