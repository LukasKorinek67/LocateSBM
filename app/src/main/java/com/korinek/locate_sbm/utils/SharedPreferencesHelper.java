package com.korinek.locate_sbm.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.korinek.locate_sbm.R;
import com.korinek.locate_sbm.model.Building;

public class SharedPreferencesHelper {

    private static final String PREFS_NAME = "app_preferences";
    private static final String KEY_IS_BUILDING_SELECTED = "is_building_selected";
    private static final String KEY_SELECTED_BUILDING_ID = "selected_building_id";
    private static final String KEY_SELECTED_BUILDING_NAME = "selected_building_name";
    private static final String KEY_SELECTED_BUILDING_COLOR = "selected_building_color";
    private static final String KEY_SELECTED_THEME = "selected_theme";

    private final SharedPreferences sharedPreferences;

    public SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public int getTheme() {
        return sharedPreferences.getInt(KEY_SELECTED_THEME, R.style.Theme_LocateSBM);
    }

    public boolean isBuildingSelected() {
        return sharedPreferences.getBoolean(KEY_IS_BUILDING_SELECTED, false);
    }

    public void saveBuilding(Building building) {
        sharedPreferences.edit()
                .putInt(KEY_SELECTED_BUILDING_ID, building.getId())
                .putBoolean(KEY_IS_BUILDING_SELECTED, true)
                .putString(KEY_SELECTED_BUILDING_NAME, building.getName())
                .putInt(KEY_SELECTED_BUILDING_COLOR, building.getColor())
                .apply();
    }

    public void saveTheme(int color) {
        int theme = ColorHelper.getThemeForColor(color);
        sharedPreferences.edit().putInt(KEY_SELECTED_THEME, theme).apply();
    }

    public Building getBuilding() {
        if (sharedPreferences.getBoolean(KEY_IS_BUILDING_SELECTED, false)) {
            return new Building(
                    sharedPreferences.getInt(KEY_SELECTED_BUILDING_ID, 0),
                    sharedPreferences.getString(KEY_SELECTED_BUILDING_NAME, ""),
                    sharedPreferences.getInt(KEY_SELECTED_BUILDING_COLOR, 0)
            );
        }
        return null;
    }

    public void removeBuilding() {
        sharedPreferences.edit()
                .remove(KEY_SELECTED_BUILDING_ID)
                .remove(KEY_SELECTED_BUILDING_NAME)
                .remove(KEY_SELECTED_BUILDING_COLOR)
                .putBoolean(KEY_IS_BUILDING_SELECTED, false)
                .putInt(KEY_SELECTED_THEME, R.style.Theme_LocateSBM)
                .apply();
    }

    public void resetApp() {
        sharedPreferences.edit().clear().apply();
    }
}
