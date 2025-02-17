package com.korinek.indoorlocalizatorapp.utils.api;

import com.korinek.indoorlocalizatorapp.model.Room;
import com.korinek.indoorlocalizatorapp.model.RoomApiModel;

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

    void getRoomsFromApi(RoomsFromApiCallback callback);
    void getRoomData(String name, RoomDataCallback callback);
}
