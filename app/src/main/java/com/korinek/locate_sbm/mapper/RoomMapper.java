package com.korinek.locate_sbm.mapper;

import com.korinek.locate_sbm.model.LocalizedRoom;
import com.korinek.locate_sbm.model.Room;
import com.korinek.locate_sbm.model.RoomWithWifiFingerprints;
import com.korinek.locate_sbm.model.api.RoomApiModel;
import com.korinek.locate_sbm.utils.RoomIconsHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RoomMapper {

    public static Room toRoom(RoomApiModel apiModel, int buildingId) {
        return new Room(apiModel.getName(), buildingId, RoomIconsHelper.DEFAULT_ICON_NAME);
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

    public static LocalizedRoom toLocalizedRoom(RoomWithWifiFingerprints room) {
        double defaultProbability = 0.0;
        return new LocalizedRoom(room, defaultProbability);
    }

    public static List<LocalizedRoom> toLocalizedRoomList(List<RoomWithWifiFingerprints> rooms) {
        return rooms.stream()
                .map(RoomMapper::toLocalizedRoom)
                .collect(Collectors.toList());
    }
}
