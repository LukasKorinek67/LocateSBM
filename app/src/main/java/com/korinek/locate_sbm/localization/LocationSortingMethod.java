package com.korinek.locate_sbm.localization;

import com.korinek.locate_sbm.model.LocalizedRoom;
import com.korinek.locate_sbm.model.WifiFingerprint;

import java.util.List;
import java.util.Map;

public abstract class LocationSortingMethod {

    protected static final int K = 3; // number of nearest neighbours

    public abstract void sort(WifiFingerprint actualFingerprint, List<LocalizedRoom> locationSortedRooms);

    protected double calculateEuclideanDistance2(WifiFingerprint f1, WifiFingerprint f2) {
        Map<String, Integer> ap1 = f1.getAccessPoints();
        Map<String, Integer> ap2 = f2.getAccessPoints();

        double sum = 0.0;
        for (Map.Entry<String, Integer> entry : ap1.entrySet()) {
            String mac = entry.getKey();
            int rssi1 = entry.getValue();
            int rssi2 = ap2.getOrDefault(mac, -100); // pokud chybí, simulujeme slabý signál
            sum += Math.pow(rssi1 - rssi2, 2);
        }
        return Math.sqrt(sum);
    }

    // tohle bylo použitý původně
    protected double calculateEuclideanDistance(WifiFingerprint f1, WifiFingerprint f2) {
        Map<String, Integer> ap1 = f1.getAccessPoints();
        Map<String, Integer> ap2 = f2.getAccessPoints();

        double sum = 0.0;
        int matchedCount = 0;

        for (Map.Entry<String, Integer> entry : ap1.entrySet()) {
            String ssid = entry.getKey();
            Integer rssi1 = entry.getValue();
            Integer rssi2 = ap2.get(ssid);

            if (rssi2 != null) {
                sum += Math.pow(rssi1 - rssi2, 2);
                matchedCount++;
            }
        }

        // Pokud nejsou žádné společné AP, vracíme velkou hodnotu jako "maximální vzdálenost"
        if (matchedCount == 0) {
            return Double.MAX_VALUE;
        }

        return Math.sqrt(sum);
    }

    protected double calculateCosineSimilarity2(WifiFingerprint f1, WifiFingerprint f2) {
        Map<String, Integer> ap1 = f1.getAccessPoints();
        Map<String, Integer> ap2 = f2.getAccessPoints();

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (Map.Entry<String, Integer> entry : ap1.entrySet()) {
            String mac = entry.getKey();
            int rssi1 = entry.getValue();
            int rssi2 = ap2.getOrDefault(mac, 0); // pokud chybí, simulujeme slabý signál

            dotProduct += rssi1 * rssi2;
            norm1 += rssi1 * rssi1;
            norm2 += rssi2 * rssi2;
        }

        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    protected double calculateCosineSimilarity(WifiFingerprint f1, WifiFingerprint f2) {
        Map<String, Integer> ap1 = f1.getAccessPoints();
        Map<String, Integer> ap2 = f2.getAccessPoints();

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (Map.Entry<String, Integer> entry : ap2.entrySet()) {
            String mac = entry.getKey();
            int rssi1 = entry.getValue();
            int rssi2 = ap1.getOrDefault(mac, -100); // pokud chybí, simulujeme slabý signál

            dotProduct += rssi1 * rssi2;
            norm1 += rssi1 * rssi1;
            norm2 += rssi2 * rssi2;
        }

        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
