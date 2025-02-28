package com.korinek.indoorlocalizatorapp.ui.custom_views;

import android.content.Context;

import com.korinek.indoorlocalizatorapp.utils.RoomAttributesHelper;

public class WheelViewSegment {

    private String text;
    private final Object value;
    private String valueWithUnit;
    private int icon;

    public WheelViewSegment(String key, Object value, Context context) {
        this.value = value;
        setText(key, context);
        setIcon(key);
        setValueWithUnit(key, value);
    }

    private void setText(String key, Context context) {
        this.text = RoomAttributesHelper.getTextForAttributeKey(key, context);
    }

    private void setIcon(String key) {
        this.icon = RoomAttributesHelper.getIconForAttribute(key);
    }

    private void setValueWithUnit(String key, Object value) {
        this.valueWithUnit = RoomAttributesHelper.getAttributeStringValueWithUnit(key, value);
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
