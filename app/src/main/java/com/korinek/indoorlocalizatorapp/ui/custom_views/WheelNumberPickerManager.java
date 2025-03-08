package com.korinek.indoorlocalizatorapp.ui.custom_views;

import android.view.View;
import android.widget.LinearLayout;

import com.aigestudio.wheelpicker.WheelPicker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WheelNumberPickerManager {

    private final WheelPicker wheelPickerInt;
    private final LinearLayout wheelPickerDouble;
    private final WheelPicker wheelPickerIntPart;
    private final WheelPicker wheelPickerDecimalPart;
    private final int MAX_INT_NUMBER = 500;
    private boolean isValueInt = true;

    public WheelNumberPickerManager(WheelPicker wheelPickerInt, LinearLayout wheelPickerDouble, WheelPicker wheelPickerIntPart, WheelPicker wheelPickerDecimalPart) {
        this.wheelPickerInt = wheelPickerInt;
        this.wheelPickerDouble = wheelPickerDouble;
        this.wheelPickerIntPart = wheelPickerIntPart;
        this.wheelPickerDecimalPart = wheelPickerDecimalPart;

        initWheelPickers();
    }

    private void initWheelPickers() {
        List<String> numbersInt = new ArrayList<>();
        List<String> numbersDecimal = new ArrayList<>();
        for (int i = 0; i <= 99; i++) {
            numbersInt.add(String.format(Locale.getDefault(), "%d", i));
            numbersDecimal.add(String.format(Locale.getDefault(), "%02d", i));
        }
        for (int i = 100; i <= MAX_INT_NUMBER; i++) {
            numbersInt.add(String.format(Locale.getDefault(), "%d", i));
        }
        wheelPickerInt.setData(numbersInt);
        wheelPickerIntPart.setData(numbersInt);
        wheelPickerDecimalPart.setData(numbersDecimal);
    }

    public void setWheelPickersValues(Object value) {
        if(value instanceof Integer) {
            isValueInt = true;
            int valueInt = (int) value;

            wheelPickerInt.setVisibility(View.VISIBLE);
            wheelPickerDouble.setVisibility(View.GONE);
            wheelPickerInt.setSelectedItemPosition(valueInt);

            // bug - it needs to be called two times to show the value, don't know why
            wheelPickerInt.setSelectedItemPosition(valueInt);
        } else if (value instanceof Double) {
            isValueInt = false;
            double valueDouble = (double) value;
            int valueIntPart = (int) valueDouble;
            int valueDecimalPart = (int) Math.round((valueDouble - valueIntPart) * 100);

            wheelPickerInt.setVisibility(View.GONE);
            wheelPickerDouble.setVisibility(View.VISIBLE);
            wheelPickerIntPart.setSelectedItemPosition(valueIntPart);
            wheelPickerDecimalPart.setSelectedItemPosition(valueDecimalPart);

            // bug - it needs to be called two times to show the value, don't know why
            wheelPickerIntPart.setSelectedItemPosition(valueIntPart);
            wheelPickerDecimalPart.setSelectedItemPosition(valueDecimalPart);
        } else {
            // TODO - what if something else than int and double?
        }
    }

    public Object getSelectedValue() {
        if(isValueInt) {
            return wheelPickerInt.getCurrentItemPosition();
        } else {
            int valueIntPart = wheelPickerIntPart.getCurrentItemPosition();
            int valueDecimalPart = wheelPickerDecimalPart.getCurrentItemPosition();
            return valueIntPart + valueDecimalPart / 100.0;
        }
    }

    public void setAlignWithoutUnit() {
        wheelPickerInt.setItemAlign(WheelPicker.ALIGN_CENTER);
        wheelPickerIntPart.setItemAlign(WheelPicker.ALIGN_RIGHT);
        wheelPickerDecimalPart.setItemAlign(WheelPicker.ALIGN_LEFT);
    }

    public void setAlignWithUnit() {
        wheelPickerInt.setItemAlign(WheelPicker.ALIGN_RIGHT);
        wheelPickerIntPart.setItemAlign(WheelPicker.ALIGN_RIGHT);
        wheelPickerDecimalPart.setItemAlign(WheelPicker.ALIGN_LEFT);
    }

    public int getAlignOffset(Object value, boolean withUnits) {
        // return offset to properly align number pickers to center
        // because there are white spaces next to numbers - max value has more digits than current value
        int maxIntDigits = String.valueOf(this.MAX_INT_NUMBER).length();
        if(value instanceof Integer) {
            if(!withUnits) {
                // when integer without units, then wheelpicker is centered
                return 0;
            } else {
                // wheelpicker not centered, needs to have same blank spaces on both sides
                int digits = String.valueOf(value).length();
                return maxIntDigits - digits;
            }
        } else {
            // double - needs to have same blank spaces on both sides
            double doubleValue = (Double) value;
            int intPart = (int) doubleValue;
            int digits = String.valueOf(intPart).length();
            return maxIntDigits - digits;
        }
    }
}
