package com.korinek.locate_sbm.model;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;

public class RoomWithWifiFingerprints {
    @Embedded
    public Room room;

    @Relation(
            parentColumn = "id",
            entityColumn = "roomId"
    )
    public List<WifiFingerprint> wifiFingerprints;

    public RoomWithWifiFingerprints(Room room, List<WifiFingerprint> wifiFingerprints) {
        this.room = room;
        this.wifiFingerprints = wifiFingerprints;
    }

    @Ignore
    public RoomWithWifiFingerprints(Room room) {
        this.room = room;
        this.wifiFingerprints = new ArrayList<>();
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public List<WifiFingerprint> getWifiFingerprints() {
        return wifiFingerprints;
    }

    public void setWifiFingerprints(List<WifiFingerprint> wifiFingerprints) {
        this.wifiFingerprints = wifiFingerprints;
    }

    public String getName() {
        return room.getName();
    }

    public String getIcon() {
        return room.getIcon();
    }

    public void setIcon(String icon) {
        this.room.setIcon(icon);
    }
}
