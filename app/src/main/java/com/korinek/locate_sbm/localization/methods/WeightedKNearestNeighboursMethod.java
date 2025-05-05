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

public class WeightedKNearestNeighboursMethod extends LocationSortingMethod {

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

        // Vážený součet (čím menší vzdálenost, tím vyšší váha)
        Map<LocalizedRoom, Double> roomScores = new HashMap<>();
        //double maxDistance = nearest.get(nearest.size() - 1).getDistance();

        for (FingerprintDistance fd : nearest) {
            //double weight = 1.0 - (fd.getDistance() / (maxDistance + 0.0001)); // normalizovaná váha
            double weight = 1.0 / (fd.getDistance() + 0.0001);
            roomScores.put(fd.getRoom(), roomScores.getOrDefault(fd.getRoom(), 0.0) + weight);
        }

        // normalization
        double totalScore = roomScores.values().stream().mapToDouble(Double::doubleValue).sum();
        for (LocalizedRoom room : locationSortedRooms) {
            double probability = roomScores.getOrDefault(room, 0.0) / totalScore * 100.0;
            room.setLocationProbability(probability);
        }

        // sorting according to probability
        locationSortedRooms.sort((a, b) -> Double.compare(b.getLocationProbability(), a.getLocationProbability()));
    }
}
