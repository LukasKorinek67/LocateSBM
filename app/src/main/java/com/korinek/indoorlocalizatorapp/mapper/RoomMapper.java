package com.korinek.indoorlocalizatorapp.mapper;

import com.korinek.indoorlocalizatorapp.R;
import com.korinek.indoorlocalizatorapp.model.Room;
import com.korinek.indoorlocalizatorapp.model.RoomApiModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RoomMapper {

    private static final int DEFAULT_ICON = R.drawable.ic_room_floor_plan;

    public static Room toRoom(RoomApiModel apiModel, int buildingId) {
        return new Room(apiModel.getName(), buildingId, DEFAULT_ICON);
    }

    public static List<Room> toRoomList(List<RoomApiModel> apiModels, int buildingId) {
        return apiModels.stream()
                .map(apiModel -> toRoom(apiModel, buildingId))
                .collect(Collectors.toList());
    }

    public static RoomApiModel toRoomApiModel(Room room) {
        Map<String, Object> attributes = new HashMap<>();
        return new RoomApiModel(room.getName(), attributes);
    }

    public static List<RoomApiModel> toRoomApiModelList(List<Room> rooms) {
        return rooms.stream()
                .map(RoomMapper::toRoomApiModel)
                .collect(Collectors.toList());
    }
}
