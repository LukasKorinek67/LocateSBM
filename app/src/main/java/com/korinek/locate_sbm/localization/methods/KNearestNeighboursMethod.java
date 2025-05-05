package com.korinek.locate_sbm.localization.methods;

import com.korinek.locate_sbm.localization.FingerprintDistance;
import com.korinek.locate_sbm.localization.LocationSortingMethod;
import com.korinek.locate_sbm.model.LocalizedRoom;
import com.korinek.locate_sbm.model.WifiFingerprint;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KNearestNeighboursMethod extends LocationSortingMethod {

    @Override
    public void sort(WifiFingerprint actualFingerprint, List<LocalizedRoom> locationSortedRooms) {
        List<FingerprintDistance> allDistances = new ArrayList<>();

        for (LocalizedRoom room : locationSortedRooms) {
            for (WifiFingerprint fingerprint : room.getRoom().getWifiFingerprints()) {
                double distance = calculateEuclideanDistance(actualFingerprint, fingerprint);
                allDistances.add(new FingerprintDistance(room, distance));
            }
        }

        if(allDistances.isEmpty()) {
            return;
        }

        // sorting fingerprints according to distance
        allDistances.sort(Comparator.comparingDouble(a -> a.getDistance()));

        // get K nearest neighbours
        List<FingerprintDistance> nearest = allDistances.subList(0, Math.min(K, allDistances.size()));

        // count neighbours
        Map<LocalizedRoom, Integer> roomVotes = new HashMap<>();
        for (FingerprintDistance fd : nearest) {
            roomVotes.put(fd.getRoom(), roomVotes.getOrDefault(fd.getRoom(), 0) + 1);
        }

        // normalization
        for (LocalizedRoom room : locationSortedRooms) {
            int votes = roomVotes.getOrDefault(room, 0);
            double probability = (double) votes / nearest.size() * 100.0;
            room.setLocationProbability(probability);
        }

        // sorting according to probability
        locationSortedRooms.sort((a, b) -> Double.compare(b.getLocationProbability(), a.getLocationProbability()));
    }
}
