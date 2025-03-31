package com.korinek.locate_sbm.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;


@Entity(
        tableName = "rooms",
        foreignKeys = @ForeignKey(
                entity = Building.class,
                parentColumns = "id",
                childColumns = "buildingId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(
                        value = {"name", "buildingId"},
                        unique = true
                )}
)
public class Room {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int buildingId;
    private String icon;
    public long lastCalibrationTimestamp;

    public Room(int id, String name, int buildingId, String icon, long lastCalibrationTimestamp) {
        this.id = id;
        this.name = name;
        this.buildingId = buildingId;
        this.icon = icon;
        this.lastCalibrationTimestamp = lastCalibrationTimestamp;
    }

    @Ignore
    public Room(String name, int buildingId, String icon) {
        this.name = name;
        this.buildingId = buildingId;
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getLastCalibrationTimestamp() {
        return lastCalibrationTimestamp;
    }

    public void setLastCalibrationTimestamp(long lastCalibrationTimestamp) {
        this.lastCalibrationTimestamp = lastCalibrationTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return id == room.id && buildingId == room.buildingId && Objects.equals(icon, room.icon) && Objects.equals(name, room.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, buildingId, icon);
    }
}
