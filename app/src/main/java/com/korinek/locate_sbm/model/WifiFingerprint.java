package com.korinek.locate_sbm.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Map;

@Entity(tableName = "wifi_fingerprints",
        foreignKeys = @ForeignKey(entity = Room.class,
                parentColumns = "id",
                childColumns = "roomId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("roomId")})
public class WifiFingerprint {
    @PrimaryKey(autoGenerate = true)
    private int fingerprintId;

    private int roomId;

    private Map<String, Integer> accessPoints;

    public void setFingerprintId(int fingerprintId) {
        this.fingerprintId = fingerprintId;
    }

    public int getFingerprintId() {
        return fingerprintId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public Map<String, Integer> getAccessPoints() {
        return accessPoints;
    }

    public void setAccessPoints(Map<String, Integer> accessPoints) {
        this.accessPoints = accessPoints;
    }
}
