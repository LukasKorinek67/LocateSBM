package com.korinek.indoorlocalizatorapp.ui.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aigestudio.wheelpicker.WheelPicker;
import com.korinek.indoorlocalizatorapp.R;
import com.korinek.indoorlocalizatorapp.ui.room_setup.RoomSetupFragment;
import com.korinek.indoorlocalizatorapp.utils.RoomAttributesHelper;
import com.korinek.indoorlocalizatorapp.utils.api.ApiCalls;
import com.korinek.indoorlocalizatorapp.utils.api.RequestHandler;

import java.util.Locale;
import java.util.Map;

public class RoomAttributesWheelContainer extends FrameLayout {

    private WheelView wheelView;
    private LinearLayout numberPickerContainer;
    private WheelNumberPickerManager numberPickerManager;
    private Button setButton;
    private Button cancelButton;

    private RoomSetupFragment.RoomSetupReloadCallback roomSetupReloadCallback;
    private String roomName;
    private String editingAttributeKey;

    public RoomAttributesWheelContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.custom_wheel_view_container, this, true);
        wheelView = findViewById(R.id.wheel_view);
        numberPickerContainer = findViewById(R.id.number_picker_container);
        setButton = findViewById(R.id.set_attribute_change_button);
        cancelButton = findViewById(R.id.cancel_attribute_change_button);

        WheelPicker wheelPickerInt = findViewById(R.id.number_picker_int);
        LinearLayout wheelPickerDouble = findViewById(R.id.number_picker_double);
        WheelPicker wheelPickerIntPart = findViewById(R.id.number_picker_int_part);
        WheelPicker wheelPickerDecimalPart = findViewById(R.id.number_picker_decimal_part);

        numberPickerManager = new WheelNumberPickerManager(wheelPickerInt, wheelPickerDouble, wheelPickerIntPart, wheelPickerDecimalPart);

        // start position
        showEditingOff();

        // buttons
        setButton.setOnClickListener(v -> setSelection());
        cancelButton.setOnClickListener(v -> cancelSelection());

        // wheelView
        wheelView.setOnCenterClickListener(this::editAttribute);
    }


    public void setRoomSetupCallback(RoomSetupFragment.RoomSetupReloadCallback roomSetupReloadCallback) {
        this.roomSetupReloadCallback = roomSetupReloadCallback;
    }

    private void editAttribute(String key, Object value, String unit) {
        editingAttributeKey = key;
        numberPickerManager.setWheelPickersValues(value);
        setRightUnitAndCenter(value, unit);
        showEditingOn();
    }

    private void setRightUnitAndCenter(Object value, String unit){
        TextView unitText = findViewById(R.id.number_picker_unit);
        int alignOffset;
        if(unit.equalsIgnoreCase("")) {
            numberPickerManager.setAlignWithoutUnit();
            alignOffset = numberPickerManager.getAlignOffset(value, false);
        } else {
            numberPickerManager.setAlignWithUnit();
            alignOffset = numberPickerManager.getAlignOffset(value, true);
        }

        // add some characters to the end for better centering
        // as it it like this, it matches center text position from WheelView before editing
        StringBuilder unitBuilder = new StringBuilder(unit);
        for (int i = 0; i < alignOffset; i++) {
            unitBuilder.append("  ");
        }
        unitText.setText(unitBuilder.toString());
    }

    private void setSelection() {
        if(roomName != null && editingAttributeKey != null) {
            Object selectedValue = numberPickerManager.getSelectedValue();
            RequestHandler requestHandler = new RequestHandler(getContext());
            requestHandler.setRoomAttribute(roomName, editingAttributeKey, selectedValue, new ApiCalls.SetRoomAttributeCallback() {
                @Override
                public void onSuccess() {
                    wheelView.editingDone();
                    Toast.makeText(getContext(),
                            String.format(Locale.getDefault(), getContext().getString(R.string.confirm_set_toast),
                                    RoomAttributesHelper.getTextForAttributeKey(editingAttributeKey, getContext()),
                                    selectedValue.toString(),
                                    RoomAttributesHelper.getUnitForAttribute(editingAttributeKey)),
                            Toast.LENGTH_SHORT).show();
                    // load data again
                    roomSetupReloadCallback.reloadRoomData();
                    showEditingOff();
                }

                @Override
                public void onFailure(String errorMessage) {
                    cancelSelection();
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void cancelSelection() {
        wheelView.editingCanceled();
        showEditingOff();
    }

    private void showEditingOn() {
        numberPickerContainer.setVisibility(VISIBLE);
        setButton.setVisibility(VISIBLE);
        cancelButton.setVisibility(VISIBLE);
    }

    private void showEditingOff() {
        numberPickerContainer.setVisibility(GONE);
        setButton.setVisibility(GONE);
        cancelButton.setVisibility(GONE);
    }

    public void setData(String roomName, Map<String, Object> items) {
        this.roomName = roomName;
        wheelView.setData(items);
    }
}
