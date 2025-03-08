package com.korinek.indoorlocalizatorapp.model.api;

import java.util.Map;

public class RoomApiModel {

    private final String name;
    private Map<String, Object> attributes;

    public RoomApiModel(String name) {
        this.name = name;
    }
    public RoomApiModel(String name, Map<String, Object> attributes) {
        this.name = name;
        this.attributes = attributes;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
