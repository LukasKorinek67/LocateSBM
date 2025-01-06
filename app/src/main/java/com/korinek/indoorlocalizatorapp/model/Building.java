package com.korinek.indoorlocalizatorapp.model;


public class Building {
    private String name;
    private int colour;

    public Building(String name, int colour) {
        this.name = name;
        this.colour = colour;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColour() {
        return colour;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }
}
