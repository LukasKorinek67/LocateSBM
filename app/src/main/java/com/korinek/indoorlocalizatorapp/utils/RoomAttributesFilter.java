package com.korinek.indoorlocalizatorapp.utils;

import java.util.Map;
import java.util.stream.Collectors;

public class RoomAttributesFilter {


    public static Map<String, Object> filterNegativeAttributes(Map<String, Object> attributes) {

        return attributes.entrySet().stream()
                .filter(entry -> (entry.getValue() instanceof Number) && ((Number) entry.getValue()).doubleValue() >= 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
