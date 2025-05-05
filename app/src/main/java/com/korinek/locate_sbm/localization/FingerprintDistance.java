package com.korinek.locate_sbm.localization;

import com.korinek.locate_sbm.model.LocalizedRoom;

public class FingerprintDistance {

    private LocalizedRoom room;
    private double distance;

    public FingerprintDistance(LocalizedRoom room, double distance) {
        this.room = room;
        this.distance = distance;
    }

    public LocalizedRoom getRoom() {
        return room;
    }

    public void setRoom(LocalizedRoom room) {
        this.room = room;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
