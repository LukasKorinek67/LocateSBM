package com.korinek.indoorlocalizatorapp.utils.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.korinek.indoorlocalizatorapp.model.BuildingApiModel;
import com.korinek.indoorlocalizatorapp.model.RoomApiModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildingDeserializer implements JsonDeserializer<BuildingApiModel> {
    @Override
    public BuildingApiModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        if (jsonObject.entrySet().isEmpty()) {
            throw new JsonParseException("Empty JSON object");
        }

        Map.Entry<String, JsonElement> firstEntry = jsonObject.entrySet().iterator().next();
        String buildingName = firstEntry.getKey();

        // List of rooms
        JsonArray roomsArray = firstEntry.getValue().getAsJsonArray();
        List<RoomApiModel> rooms = new ArrayList<>();

        for (JsonElement roomElement : roomsArray) {
            RoomApiModel room = getRoom(roomElement);
            if (room != null) {
                rooms.add(room);
            }
        }

        return new BuildingApiModel(buildingName, rooms);
    }

    private RoomApiModel getRoom(JsonElement roomElement) {
        RoomApiModel room;
        JsonObject roomObject = roomElement.getAsJsonObject();
        if (roomObject.has("name") && !roomObject.get("name").getAsString().isEmpty()) {
            String name = roomObject.get("name").getAsString();
            room = new RoomApiModel(name);
        } else {
            return null;
        }

        Map<String, Object> attributes = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : roomObject.entrySet()) {
            String key = entry.getKey();

            if (!key.equals("name")) {
                JsonElement value = entry.getValue();

                if (value.isJsonPrimitive()) {
                    JsonPrimitive primitive = value.getAsJsonPrimitive();
                    if (primitive.isNumber()) {
                        String numberStr = primitive.getAsString();
                        // if it has . or , it is Double, otherwise Integer
                        if (numberStr.contains(".") || numberStr.contains(",")) {
                            attributes.put(key, primitive.getAsDouble());
                        } else {
                            attributes.put(key, primitive.getAsInt());
                        }
                    } else if (primitive.isBoolean()) {
                        attributes.put(key, primitive.getAsBoolean());
                    } else {
                        attributes.put(key, primitive.getAsString());
                    }
                } else {
                    attributes.put(key, value.toString());
                }
            }
        }
        room.setAttributes(attributes);
        return room;
    }
}
