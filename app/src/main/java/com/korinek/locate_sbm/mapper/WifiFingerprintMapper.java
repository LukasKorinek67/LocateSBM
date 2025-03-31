package com.korinek.locate_sbm.mapper;

import android.net.wifi.ScanResult;

import com.korinek.locate_sbm.model.WifiFingerprint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WifiFingerprintMapper {

    public static WifiFingerprint scanResultsToWifiFingerprint(List<ScanResult> scanResults) {
        WifiFingerprint wifiFingerprint = new WifiFingerprint();

        Map<String, Integer> accessPoints = new HashMap<>();

        for (ScanResult result : scanResults) {
            // BSSID is MAC adress of AP, level is RSSI
            accessPoints.put(result.BSSID, result.level);
        }
        wifiFingerprint.setAccessPoints(accessPoints);

        return wifiFingerprint;
    }

    public static WifiFingerprint scanResultsToWifiFingerprint(List<ScanResult> scanResults, int roomId) {
        WifiFingerprint wifiFingerprint = new WifiFingerprint();
        wifiFingerprint.setRoomId(roomId);

        Map<String, Integer> accessPoints = new HashMap<>();
        for (ScanResult result : scanResults) {
            // BSSID is MAC adress of AP, level is RSSI
            accessPoints.put(result.BSSID, result.level);
        }
        wifiFingerprint.setAccessPoints(accessPoints);

        return wifiFingerprint;
    }
}
