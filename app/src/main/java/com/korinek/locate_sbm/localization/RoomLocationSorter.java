package com.korinek.locate_sbm.localization;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.korinek.locate_sbm.localization.methods.CosineSimilarityWKNNMethod;
import com.korinek.locate_sbm.localization.methods.KNearestNeighboursMethod;
import com.korinek.locate_sbm.localization.methods.RandomSortMethod;
import com.korinek.locate_sbm.localization.methods.WeightedKNearestNeighboursMethod;
import com.korinek.locate_sbm.model.LocalizedRoom;
import com.korinek.locate_sbm.model.WifiFingerprint;

import java.util.ArrayList;
import java.util.List;

public class RoomLocationSorter {

    public static List<LocalizedRoom> sortRoomsByLocation(WifiFingerprint actualFingerprint, List<LocalizedRoom> rooms, Context context) {
        List<LocalizedRoom> locationSortedRooms = new ArrayList<>(rooms);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String localizationMethod = sharedPreferences.getString("settings_localization_method", "random");

        LocationSortingMethod method = getMethod(localizationMethod);
        method.sort(actualFingerprint, locationSortedRooms);

        return locationSortedRooms;
    }

    private static LocationSortingMethod getMethod(String method) {
        switch (method) {
            case "knn_method":
                return new KNearestNeighboursMethod();
            case "wknn_method":
                return new WeightedKNearestNeighboursMethod();
            case "cosine_wknn_method":
                return new CosineSimilarityWKNNMethod();
            default:
                return new RandomSortMethod();
        }
    }
}
