package com.korinek.indoorlocalizatorapp.model;

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
    private int icon;

    public Room(int id, String name, int buildingId, int icon) {
        this.id = id;
        this.name = name;
        this.buildingId = buildingId;
        this.icon = icon;
    }

    @Ignore
    public Room(String name, int buildingId, int icon) {
        this.name = name;
        this.buildingId = buildingId;
        this.icon = icon;
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

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return id == room.id && buildingId == room.buildingId && icon == room.icon && Objects.equals(name, room.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, buildingId, icon);
    }
}
