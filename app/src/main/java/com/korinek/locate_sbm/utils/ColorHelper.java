package com.korinek.locate_sbm.utils;

import android.content.Context;
import com.korinek.locate_sbm.R;
import java.util.Arrays;
import java.util.List;

public class ColorHelper {

    public enum ColorTheme {
        BLACK(R.id.color_black, R.string.black, R.color.black, R.style.Theme_LocateSBM),
        BLUE(R.id.color_blue, R.string.blue, R.color.blue, R.style.Theme_LocateSBMBlue),
        GRAY(R.id.color_gray, R.string.gray, R.color.gray, R.style.Theme_LocateSBMGray),
        GREEN(R.id.color_green, R.string.green, R.color.green, R.style.Theme_LocateSBMGreen),
        ORANGE(R.id.color_orange, R.string.orange, R.color.orange, R.style.Theme_LocateSBMOrange),
        PINK(R.id.color_pink, R.string.pink, R.color.pink, R.style.Theme_LocateSBMPink),
        PURPLE(R.id.color_purple, R.string.purple, R.color.purple, R.style.Theme_LocateSBMPurple),
        RED(R.id.color_red, R.string.red, R.color.red, R.style.Theme_LocateSBMRed),
        YELLOW(R.id.color_yellow, R.string.yellow, R.color.yellow, R.style.Theme_LocateSBMYellow);

        private final int colorId;
        private final int colorNameResId;
        private final int color;
        private final int theme;

        ColorTheme(int colorId, int colorNameResId, int color, int theme) {
            this.colorId = colorId;
            this.colorNameResId = colorNameResId;
            this.color = color;
            this.theme = theme;
        }

        public static ColorHelper.ColorTheme fromIntColor(int colorInt) {
            for (ColorHelper.ColorTheme colorTheme : values()) {
                if (colorTheme.color == colorInt) {
                    return colorTheme;
                }
            }
            return null;
        }

        public int getColorId() {
            return colorId;
        }

        public int getColorNameResId() {
            return colorNameResId;
        }

        public int getColor() {
            return color;
        }

        public int getTheme() {
            return theme;
        }
    }

    public static int getThemeForColor(int color) {
        int defaultTheme =  R.style.Theme_LocateSBM;
        ColorHelper.ColorTheme colorTheme = ColorHelper.ColorTheme.fromIntColor(color);
        return colorTheme != null ? colorTheme.getTheme() : defaultTheme;
    }

    public static String getColorName(Context context, int color) {
        ColorHelper.ColorTheme colorTheme = ColorHelper.ColorTheme.fromIntColor(color);
        return colorTheme != null ? context.getString(colorTheme.getColorNameResId()) : "";
    }

    public static List<ColorTheme> getAllColors() {
        return Arrays.asList(ColorTheme.values());
    }
}
