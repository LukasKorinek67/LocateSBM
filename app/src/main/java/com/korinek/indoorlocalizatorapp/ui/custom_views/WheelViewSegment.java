package com.korinek.indoorlocalizatorapp.ui.custom_views;

import android.content.Context;

import com.korinek.indoorlocalizatorapp.utils.RoomAttributesHelper;

public class WheelViewSegment {

    private final String key;
    private Object value;
    private String text;
    private String unit;
    private String valueWithUnit;
    private int icon;

    public WheelViewSegment(String key, Object value, Context context) {
        this.key = key;
        this.value = value;
        setText(key, context);
        setIcon(key);
        setUnit(key);
        setValueWithUnit(key, value);
    }

    private void setText(String key, Context context) {
        this.text = RoomAttributesHelper.getTextForAttributeKey(key, context);
    }

    private void setIcon(String key) {
        this.icon = RoomAttributesHelper.getIconForAttribute(key);
    }

    private void setUnit(String key) {
        this.unit = RoomAttributesHelper.getUnitForAttribute(key);
    }

    private void setValueWithUnit(String key, Object value) {
        this.valueWithUnit = RoomAttributesHelper.getAttributeStringValueWithUnit(key, value);
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public String getValueWithUnit() {
        return valueWithUnit;
    }


    public int getIcon() {
        return icon;
    }
}
