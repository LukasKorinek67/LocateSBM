package com.korinek.indoorlocalizatorapp.model;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "buildings")
public class Building {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int color;

    public Building(int id, String name, int color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    @Ignore
    public Building(String name, int color) {
        this.name = name;
        this.color = color;
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Building building = (Building) o;
        return id == building.id && color == building.color && Objects.equals(name, building.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
