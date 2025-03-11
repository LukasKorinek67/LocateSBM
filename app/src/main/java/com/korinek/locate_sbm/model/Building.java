package com.korinek.locate_sbm.model;


import androidx.room.Embedded;
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
    @Embedded
    private TecoApiIntegration integration;

    public Building(int id, String name, int color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    @Ignore
    public Building(String name, int color) {
        this.name = name;
        this.color = color;
        this.integration = new TecoApiIntegration();
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

    public TecoApiIntegration getIntegration() {
        return integration;
    }

    public void setIntegration(TecoApiIntegration integration) {
        this.integration = integration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Building building = (Building) o;
        return id == building.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, integration);
    }
}
