package com.korinek.indoorlocalizatorapp.utils;

import com.korinek.indoorlocalizatorapp.R;

import java.util.HashMap;

public class ColorToTheme {

    private static final HashMap<Integer, Integer> colorThemeMap = initMap();

    private static HashMap<Integer, Integer> initMap() {
        HashMap<Integer, Integer> colorThemeMap = new HashMap<>();
        colorThemeMap.put(R.color.black, R.style.Theme_IndoorLocalizatorApp);
        colorThemeMap.put(R.color.blue, R.style.Theme_IndoorLocalizatorAppBlue);
        colorThemeMap.put(R.color.green, R.style.Theme_IndoorLocalizatorAppGreen);
        colorThemeMap.put(R.color.orange, R.style.Theme_IndoorLocalizatorAppOrange);
        colorThemeMap.put(R.color.pink, R.style.Theme_IndoorLocalizatorAppPink);
        colorThemeMap.put(R.color.purple, R.style.Theme_IndoorLocalizatorAppPurple);
        colorThemeMap.put(R.color.red, R.style.Theme_IndoorLocalizatorAppRed);
        colorThemeMap.put(R.color.yellow, R.style.Theme_IndoorLocalizatorAppYellow);
        return colorThemeMap;
    }

    public static int getThemeForColor(int color) {
        return colorThemeMap.getOrDefault(color, R.style.Theme_IndoorLocalizatorApp);
    }
}
