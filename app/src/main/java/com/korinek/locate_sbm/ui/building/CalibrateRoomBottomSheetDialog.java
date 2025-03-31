package com.korinek.locate_sbm.ui.building;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.korinek.locate_sbm.R;
import com.korinek.locate_sbm.model.RoomWithWifiFingerprints;
import com.korinek.locate_sbm.ui.building.measurements.RoomScanningStateViewModel;
import com.korinek.locate_sbm.ui.building.measurements.RoomScanningFragment;
import com.korinek.locate_sbm.utils.RoomIconsHelper;
import com.korinek.locate_sbm.utils.TimestampHelper;

import java.util.Locale;

public class CalibrateRoomBottomSheetDialog extends BottomSheetDialogFragment {

    private final RoomWithWifiFingerprints room;

    private RoomScanningStateViewModel roomScanningStateViewModel;

    public enum RoomSize {
        SMALL(3),
        MEDIUM(5),
        LARGE(8);

        private final int numberOfScans;

        RoomSize(int numberOfScans) {
            this.numberOfScans = numberOfScans;
        }

        public int getNumberOfScans() {
            return numberOfScans;
        }
    }

    public CalibrateRoomBottomSheetDialog(RoomWithWifiFingerprints room) {
        this.room = room;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        roomScanningStateViewModel = new ViewModelProvider(requireActivity()).get(RoomScanningStateViewModel.class);
        return inflater.inflate(R.layout.bottom_sheet_calibrate_room, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setExpandedState(view);
        initializeComponents(view);
    }

    private void setExpandedState(View view) {
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        LinearLayout layout = view.findViewById(R.id.layout_calibrate_bottom_sheet);
        double percentOfHeight = 0.92;
        int minimumHeight = (int) (Resources.getSystem().getDisplayMetrics().heightPixels * percentOfHeight);
        layout.setMinimumHeight(minimumHeight);
        bottomSheetBehavior.setPeekHeight(minimumHeight);
    }

    private void initializeComponents(View view) {
        ImageView roomIcon = view.findViewById(R.id.calibrate_room_icon);
        TextView roomName = view.findViewById(R.id.calibrate_room_name);
        ViewFlipper viewFlipper = view.findViewById(R.id.calibrate_view_flipper);
        TextView calibrationStateText = view.findViewById(R.id.text_calibration_state);
        MaterialButtonToggleGroup buttonGroupRoomSize = view.findViewById(R.id.button_group_room_size);
        LinearLayout layoutStartingCalibration = view.findViewById(R.id.layout_starting_calibration);
        Button startCalibrateButton = view.findViewById(R.id.button_start_calibration);

        roomIcon.setImageResource(RoomIconsHelper.getIconResId(room.getIcon()));
        roomName.setText(room.getName());

        long lastCalibrationTimestamp = room.getRoom().lastCalibrationTimestamp;
        Drawable drawable;
        if (lastCalibrationTimestamp == 0) {
            calibrationStateText.setText(getString(R.string.status_text_not_calibrated_yet));
            calibrationStateText.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
            drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_alert_circle);
            DrawableCompat.setTint(drawable, ContextCompat.getColor(getContext(), R.color.red));
            calibrationStateText.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        } else {
            calibrationStateText.setText(String.format(Locale.getDefault(), getString(R.string.status_text_calibrated_date), TimestampHelper.toLocalDateTime(lastCalibrationTimestamp)));
            calibrationStateText.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
            drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_check_circle);
            DrawableCompat.setTint(drawable, ContextCompat.getColor(getContext(), R.color.green));
            calibrationStateText.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }

        buttonGroupRoomSize.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            int checkedIdFromGroupButton = group.getCheckedButtonId();
            TextView calibrationInfoText = view.findViewById(R.id.calibration_info_text);
            if(checkedIdFromGroupButton != -1) {
                layoutStartingCalibration.setVisibility(View.VISIBLE);

                RoomSize selectedRoomSize;
                if(checkedIdFromGroupButton == R.id.btn_option_large) {
                    selectedRoomSize = RoomSize.LARGE;
                } else if(checkedIdFromGroupButton == R.id.btn_option_medium) {
                    selectedRoomSize = RoomSize.MEDIUM;
                } else {
                    selectedRoomSize = RoomSize.SMALL;
                }
                int numberOfScans = selectedRoomSize.getNumberOfScans();

                calibrationInfoText.setText(String.format(Locale.getDefault(), getContext().getString(R.string.calibration_info_text), numberOfScans));
                roomScanningStateViewModel.setNumberOfScans(numberOfScans);
            } else {
                layoutStartingCalibration.setVisibility(View.INVISIBLE);
            }
        });

        // Starting calibration - show room scaning fragment
        startCalibrateButton.setOnClickListener(v -> viewFlipper.setDisplayedChild(1));

        // RoomScanningFragment
        RoomScanningFragment roomScanningFragment = new RoomScanningFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("roomId", room.getRoom().getId());
        roomScanningFragment.setArguments(bundle);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.room_scanning_fragment_container, roomScanningFragment)
                .commit();
    }
}
