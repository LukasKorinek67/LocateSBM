package com.korinek.indoorlocalizatorapp.ui.localization;

import com.korinek.indoorlocalizatorapp.model.Room;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RoomLocationSorter {

    public static List<Room> sortRoomsByLocation(List<Room> rooms) {
        List<Room> locationSortedRooms = new ArrayList<>(rooms);
        // TODO - sort by location, for now just random shuffle
        Collections.shuffle(locationSortedRooms, new Random());
        return locationSortedRooms;
    }
}
