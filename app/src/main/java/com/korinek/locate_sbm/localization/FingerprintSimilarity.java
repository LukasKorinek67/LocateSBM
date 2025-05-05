package com.korinek.locate_sbm.localization;

import com.korinek.locate_sbm.model.LocalizedRoom;

public class FingerprintSimilarity {

    private LocalizedRoom room;
    private double similarity;

    public FingerprintSimilarity(LocalizedRoom room, double similarity) {
        this.room = room;
        this.similarity = similarity;
    }

    public LocalizedRoom getRoom() {
        return room;
    }

    public void setRoom(LocalizedRoom room) {
        this.room = room;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }
}
