package com.korinek.locate_sbm.utils;

import android.content.Context;

import com.korinek.locate_sbm.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class RoomAttributesHelper {

    public enum RoomAttribute {
        TEMPERATURE("temp", new String[]{"temperature", "temp_celsius", "tempCelsius"}, R.string.temperature, R.drawable.ic_temperature_celsius, "°C"),
        HUMIDITY("rh", new String[]{"humidity"}, R.string.humidity, R.drawable.ic_humidity, "%"),
        CO2("co2", new String[]{"carbon_dioxide", "carbonDioxide"}, R.string.co2, R.drawable.ic_co2, ""),
        SET_TEMPERATURE("tempW", new String[]{"target_temperature", "targetTemperature",  "temp_set", "tempSet"}, R.string.set_temperature, R.drawable.ic_temperature_set, "°C"),
        VALVE("valve", new String[]{"radiator_valve", "radiatorValve"}, R.string.valve, R.drawable.ic_valve, "%"),
        LIGHT1("light1", new String[]{"light_1"}, R.string.light1, R.drawable.ic_light, "%"),
        LIGHT2("light2", new String[]{"light_2"}, R.string.light2, R.drawable.ic_light, "%"),
        LIGHT3("light3", new String[]{"light_3"}, R.string.light3, R.drawable.ic_light, "%"),
        BLINDS("blinds", new String[]{"window_blinds", "windowBlinds"}, R.string.blinds, R.drawable.ic_blinds, ""),
        VENTILATION("ventilation", new String[]{"airflow"}, R.string.ventilation, R.drawable.ic_ventilation, "%");

        private final String canonicalKey;
        private final String[] keyAliases;
        private final int displayNameResId;
        private final int iconResId;
        private final String unit;

        RoomAttribute(String canonicalKey, String[] keyAliases, int displayNameResId, int iconResId, String unit) {
            this.canonicalKey = canonicalKey;
            this.keyAliases = keyAliases;
            this.displayNameResId = displayNameResId;
            this.iconResId = iconResId;
            this.unit = unit;
        }

        public String getCanonicalKey() {
            return canonicalKey;
        }

        public int getDisplayNameResId() {
            return displayNameResId;
        }

        public String getDisplayName(Context context) {
            return context.getString(displayNameResId);
        }

        public int getIconResId() {
            return iconResId;
        }

        public String getUnit() {
            return unit;
        }

        public static RoomAttribute fromKey(String key) {
            for (RoomAttribute attr : values()) {
                if (attr.canonicalKey.equalsIgnoreCase(key)) {
                    return attr;
                }
                for (String alias : attr.keyAliases) {
                    if (alias.equalsIgnoreCase(key)) {
                        return attr;
                    }
                }
            }
            return null;
        }
    }

    public static Map<String, Object> filterNegativeAttributes(Map<String, Object> attributes) {

        return attributes.entrySet().stream()
                .filter(entry -> (entry.getValue() instanceof Number) && ((Number) entry.getValue()).doubleValue() >= 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Map<String, Object> sortAttributes(Map<String, Object> attributes) {
        List<String> priorityOrder = Arrays.asList(
                "temp", "tempW", "rh", "blinds", "ventilation", "light1", "light2", "light3", "valve", "co2"
        );

        // sorting
        List<Map.Entry<String, Object>> sortedList = new ArrayList<>(attributes.entrySet());
        sortedList.sort(Comparator.comparingInt(entry -> {
            int index = priorityOrder.indexOf(entry.getKey());
            return index == -1 ? Integer.MAX_VALUE : index;
        }));

        // save to LinkedHashMap
        Map<String, Object> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : sortedList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public static String getTextForAttributeKey(String key, Context context) {
        RoomAttribute attr = RoomAttribute.fromKey(key);
        return attr != null ? attr.getDisplayName(context) : key;
    }

    public static int getIconForAttribute(String key) {
        int defaultIcon = R.drawable.ic_default_attribute;
        RoomAttribute attr = RoomAttribute.fromKey(key);
        return attr != null ? attr.getIconResId() : defaultIcon;
    }

    public static String getUnitForAttribute(String key) {
        RoomAttribute attr = RoomAttribute.fromKey(key);
        return attr != null ? attr.getUnit() : "";
    }

    public static String getAttributeStringValueWithUnit(String key, Object value) {
        String valueWithUnit = getAttributeStringValue(value);
        valueWithUnit += getUnitForAttribute(key);
        return valueWithUnit;
    }
    public static String getAttributeStringValue(Object value) {
        if (value instanceof Integer) {
            return String.format(Locale.getDefault(),"%d", (Integer) value);
        } else if (value instanceof Double) {
            return String.format(Locale.getDefault(),"%.2f", (Double) value);
        } else {
            return value.toString();
        }
    }
}
