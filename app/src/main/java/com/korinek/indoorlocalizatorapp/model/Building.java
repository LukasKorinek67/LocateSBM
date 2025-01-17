package com.korinek.indoorlocalizatorapp.model;


import java.util.Objects;

public class Building {
    private String name;
    private int color;

    public Building(String name, int color) {
        this.name = name;
        this.color = color;
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
        return color == building.color && Objects.equals(name, building.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color);
    }
}
