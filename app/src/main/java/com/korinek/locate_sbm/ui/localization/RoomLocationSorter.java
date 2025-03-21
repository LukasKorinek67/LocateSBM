package com.korinek.locate_sbm.ui.localization;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.korinek.locate_sbm.model.Room;
import com.korinek.locate_sbm.model.WifiFingerprint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RoomLocationSorter {

    public static List<Room> sortRoomsByLocation(WifiFingerprint actualFingerprint, List<Room> rooms, Context context) {
        List<Room> locationSortedRooms = new ArrayList<>(rooms);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String localizationMethod = sharedPreferences.getString("settings_localization_method", "random");

        if(localizationMethod.equals("random")) {
            // random shuffle
            Collections.shuffle(locationSortedRooms, new Random());
        } else {
            // TODO - sort by location (different methods), for now just random shuffle
            Collections.shuffle(locationSortedRooms, new Random());
        }

        return locationSortedRooms;
    }
}
