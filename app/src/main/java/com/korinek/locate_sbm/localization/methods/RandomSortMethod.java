package com.korinek.locate_sbm.localization.methods;

import com.korinek.locate_sbm.localization.LocationSortingMethod;
import com.korinek.locate_sbm.model.LocalizedRoom;
import com.korinek.locate_sbm.model.WifiFingerprint;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomSortMethod extends LocationSortingMethod {

    @Override
    public void sort(WifiFingerprint actualFingerprint, List<LocalizedRoom> locationSortedRooms) {
        Collections.shuffle(locationSortedRooms, new Random());
        // set  equal probability to all rooms
        double equalProbability = 100.0 / locationSortedRooms.size();
        locationSortedRooms.forEach(room -> room.setLocationProbability(equalProbability));
    }
}
