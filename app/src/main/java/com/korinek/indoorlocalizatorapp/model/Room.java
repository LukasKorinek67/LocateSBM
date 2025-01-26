package com.korinek.indoorlocalizatorapp.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;


@Entity(
        tableName = "rooms",
        foreignKeys = @ForeignKey(
                entity = Building.class,
                parentColumns = "id",
                childColumns = "buildingId",
                onDelete = ForeignKey.CASCADE
        )
)
public class Room {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int buildingId;

    public Room(int id, String name, int buildingId) {
        this.id = id;
        this.name = name;
        this.buildingId = buildingId;
    }

    @Ignore
    public Room(String name, int buildingId) {
        this.name = name;
        this.buildingId = buildingId;
    }

    @Ignore
    public Room(String name) {
        this.name = name;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return id == room.id && buildingId == room.buildingId && Objects.equals(name, room.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, buildingId);
    }
}
