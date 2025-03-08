package com.korinek.indoorlocalizatorapp.model.api;

import java.util.List;

public class BuildingApiModel {

    private final String name;
    private final List<RoomApiModel> rooms;

    public BuildingApiModel(String name, List<RoomApiModel> rooms) {
        this.name = name;
        this.rooms = rooms;
    }

    public String getName() {
        return name;
    }

    public List<RoomApiModel> getRooms() {
        return rooms;
    }

}
