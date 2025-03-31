package com.korinek.locate_sbm.model;


public class LocalizedRoom {

    private RoomWithWifiFingerprints room;

    // probability of user being placed in this room
    private double locationProbability;

    public LocalizedRoom(RoomWithWifiFingerprints room, double probability) {
        this.room = room;
        this.locationProbability = probability;
    }

    public RoomWithWifiFingerprints getRoom() {
        return room;
    }

    public void setRoom(RoomWithWifiFingerprints room) {
        this.room = room;
    }

    public double getLocationProbability() {
        return locationProbability;
    }

    public void setLocationProbability(double locationProbability) {
        this.locationProbability = locationProbability;
    }

    public String getName() {
        return room.getName();
    }

    public String getIcon() {
        return room.getIcon();
    }
}
