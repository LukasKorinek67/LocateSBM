package com.korinek.indoorlocalizatorapp.utils;

import com.korinek.indoorlocalizatorapp.R;

import java.util.HashMap;
import java.util.Map;

public class RoomIconsHelper {

    public static final int DEFAULT_ICON = R.drawable.ic_room_floor_plan;
    public static final String DEFAULT_ICON_NAME = "ic_room_floor_plan";

    private static final Map<String, Integer> ICON_MAP = new HashMap<>();
    static {
        ICON_MAP.put("ic_room_pc_desktop", R.drawable.ic_room_pc_desktop);
        ICON_MAP.put("ic_room_meeting_board", R.drawable.ic_room_meeting_board);
        ICON_MAP.put("ic_room_floor_plan", R.drawable.ic_room_floor_plan);
        ICON_MAP.put("ic_room_sofa", R.drawable.ic_room_sofa);
        ICON_MAP.put("ic_room_bed", R.drawable.ic_room_bed);
        ICON_MAP.put("ic_room_kitchen_faucet", R.drawable.ic_room_kitchen_faucet);
        ICON_MAP.put("ic_room_fridge", R.drawable.ic_room_fridge);
        ICON_MAP.put("ic_room_shower", R.drawable.ic_room_shower);
        ICON_MAP.put("ic_room_toilet", R.drawable.ic_room_toilet);
        ICON_MAP.put("ic_stairs", R.drawable.ic_stairs);
    }

    public static int getIconResId(String iconName) {
        return ICON_MAP.getOrDefault(iconName, DEFAULT_ICON);
    }

    public static String getIconName(int iconResId) {
        for (Map.Entry<String, Integer> entry : ICON_MAP.entrySet()) {
            if (entry.getValue() == iconResId) {
                return entry.getKey();
            }
        }
        return DEFAULT_ICON_NAME;
    }

    public static int[] getIcons() {
        return ICON_MAP.values().stream().mapToInt(Integer::intValue).toArray();
    }
}
