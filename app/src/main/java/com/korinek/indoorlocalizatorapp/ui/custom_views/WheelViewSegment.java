package com.korinek.indoorlocalizatorapp.ui.custom_views;

import com.korinek.indoorlocalizatorapp.R;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WheelViewSegment {

    private String text;
    private final Object value;
    private String valueWithUnit;
    private int icon;

    private static final Map<String, String> TEXT_MAP = new HashMap<>();
    private static final Map<String, Integer> ICON_MAP = new HashMap<>();
    private static final Map<String, String> UNITS_MAP = new HashMap<>();

    static {
        TEXT_MAP.put("temp", "Teplota");
        TEXT_MAP.put("rh", "Vlhkost");
        TEXT_MAP.put("co2", "CO2");
        TEXT_MAP.put("tempW", "Cílová teplota");
        TEXT_MAP.put("valve", "Ventil");
        TEXT_MAP.put("light1", "Světlo 1");
        TEXT_MAP.put("light2", "Světlo 2");
        TEXT_MAP.put("light3", "Světlo 3");
        TEXT_MAP.put("blinds", "Rolety");
        TEXT_MAP.put("ventilation", "Ventilace");

        ICON_MAP.put("temp", R.drawable.ic_temperature_celsius);
        ICON_MAP.put("rh", R.drawable.ic_humidity);
        ICON_MAP.put("co2", R.drawable.ic_co2);
        ICON_MAP.put("tempW", R.drawable.ic_temperature_set);
        ICON_MAP.put("valve", R.drawable.ic_valve);
        ICON_MAP.put("light1", R.drawable.ic_light);
        ICON_MAP.put("light2", R.drawable.ic_light);
        ICON_MAP.put("light3", R.drawable.ic_light);
        ICON_MAP.put("blinds", R.drawable.ic_blinds);
        ICON_MAP.put("ventilation", R.drawable.ic_ventilation);

        UNITS_MAP.put("Teplota", "°C");
        UNITS_MAP.put("Vlhkost", "%");
        UNITS_MAP.put("CO2", "");
        UNITS_MAP.put("Cílová teplota", "°C");
        UNITS_MAP.put("Ventil", "%");
        UNITS_MAP.put("Světlo 1", "%");
        UNITS_MAP.put("Světlo 2", "%");
        UNITS_MAP.put("Světlo 3", "%");
        UNITS_MAP.put("Rolety", "");
        UNITS_MAP.put("Ventilace", "%");
    }

    public WheelViewSegment(String key, Object value) {
        this.value = value;
        setText(key);
        setIcon(key);
        setValueWithUnit(value);
    }

    private void setText(String key) {
        this.text = TEXT_MAP.getOrDefault(key, key);
    }

    private void setIcon(String key) {
        this.icon = ICON_MAP.getOrDefault(key, R.drawable.ic_gesture_tap);
    }

    private void setValueWithUnit(Object value) {
        if (value instanceof Integer) {
            this.valueWithUnit = String.format(Locale.getDefault(),"%d", (Integer) value);
        } else if (value instanceof Double) {
            this.valueWithUnit = String.format(Locale.getDefault(), "%.2f", (Double) value);
        } else {
            this.valueWithUnit = value.toString();
        }
        this.valueWithUnit += UNITS_MAP.getOrDefault(this.text, "");
    }

    public String getText() {
        return text;
    }

    public Object getValue() {
        return value;
    }

    public String getValueWithUnit() {
        return valueWithUnit;
    }


    public int getIcon() {
        return icon;
    }
}
