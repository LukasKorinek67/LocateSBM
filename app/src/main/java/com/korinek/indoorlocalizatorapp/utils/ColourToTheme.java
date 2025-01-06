package com.korinek.indoorlocalizatorapp.utils;

import com.korinek.indoorlocalizatorapp.R;

import java.util.HashMap;

public class ColourToTheme {

    private static final HashMap<Integer, Integer> colourThemeMap = initMap();

    private static HashMap<Integer, Integer> initMap() {
        HashMap<Integer, Integer> colourThemeMap = new HashMap<>();
        colourThemeMap.put(R.color.black, R.style.Theme_IndoorLocalizatorApp);
        colourThemeMap.put(R.color.blue, R.style.Theme_IndoorLocalizatorAppBlue);
        colourThemeMap.put(R.color.green, R.style.Theme_IndoorLocalizatorAppGreen);
        colourThemeMap.put(R.color.orange, R.style.Theme_IndoorLocalizatorAppOrange);
        colourThemeMap.put(R.color.pink, R.style.Theme_IndoorLocalizatorAppPink);
        colourThemeMap.put(R.color.purple, R.style.Theme_IndoorLocalizatorAppPurple);
        colourThemeMap.put(R.color.red, R.style.Theme_IndoorLocalizatorAppRed);
        colourThemeMap.put(R.color.yellow, R.style.Theme_IndoorLocalizatorAppYellow);
        return colourThemeMap;
    }

    public static int getThemeForColor(int color) {
        return colourThemeMap.getOrDefault(color, R.style.Theme_IndoorLocalizatorApp);
    }
}
