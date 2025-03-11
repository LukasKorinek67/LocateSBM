package com.korinek.locate_sbm.utils.api;

import com.korinek.locate_sbm.model.Room;
import com.korinek.locate_sbm.model.api.RoomApiModel;

import java.util.List;

public interface ApiCalls {

    interface RoomsFromApiCallback {
        void onSuccess(List<Room> rooms);
        void onFailure(String errorMessage);
    }

    interface RoomDataCallback {
        void onSuccess(RoomApiModel room);
        void onFailure(String errorMessage);
    }

    interface SetRoomAttributeCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    void getRoomsFromApi(RoomsFromApiCallback callback);
    void getRoomData(String name, RoomDataCallback callback);
    void setRoomAttribute(String roomName, String attribute, Object newValue, SetRoomAttributeCallback callback);
}
