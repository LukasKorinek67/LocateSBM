package com.korinek.locate_sbm.localization.methods;

import com.korinek.locate_sbm.localization.FingerprintSimilarity;
import com.korinek.locate_sbm.localization.LocationSortingMethod;
import com.korinek.locate_sbm.model.LocalizedRoom;
import com.korinek.locate_sbm.model.WifiFingerprint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CosineSimilarityWKNNMethod extends LocationSortingMethod {

    @Override
    public void sort(WifiFingerprint actualFingerprint, List<LocalizedRoom> locationSortedRooms) {
        List<FingerprintSimilarity> allSimilarities = new ArrayList<>();

        for (LocalizedRoom room : locationSortedRooms) {
            for (WifiFingerprint fingerprint : room.getRoom().getWifiFingerprints()) {
                double similarity = calculateCosineSimilarity(actualFingerprint, fingerprint);
                allSimilarities.add(new FingerprintSimilarity(room, similarity));
            }
        }

        if (allSimilarities.isEmpty()) {
            return;
        }

        // sorting fingerprints according to similarity (higher = better)
        allSimilarities.sort((a, b) -> Double.compare(b.getSimilarity(), a.getSimilarity()));

        // get K nearest neighbours
        List<FingerprintSimilarity> nearest = allSimilarities.subList(0, Math.min(K, allSimilarities.size()));

        // Součet podobností
        Map<LocalizedRoom, Double> roomScores = new HashMap<>();
        for (FingerprintSimilarity fs : nearest) {
            roomScores.put(fs.getRoom(), roomScores.getOrDefault(fs.getRoom(), 0.0) + fs.getSimilarity());
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
