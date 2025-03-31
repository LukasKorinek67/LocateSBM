package com.korinek.locate_sbm.database;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class Converters {
    @TypeConverter
    public static String fromMap(Map<String, Integer> map) {
        return new Gson().toJson(map);
    }

    @TypeConverter
    public static Map<String, Integer> toMap(String value) {
        return new Gson().fromJson(value, new TypeToken<Map<String, Integer>>(){}.getType());
    }
}
