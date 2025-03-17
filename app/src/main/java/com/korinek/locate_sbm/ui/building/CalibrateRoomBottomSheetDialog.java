package com.korinek.locate_sbm.ui.building;

import android.content.res.Resources;
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
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.korinek.locate_sbm.R;
import com.korinek.locate_sbm.model.Room;
import com.korinek.locate_sbm.ui.building.measurements.RoomScanningViewModel;
import com.korinek.locate_sbm.ui.building.measurements.RoomScanningFragment;
import com.korinek.locate_sbm.utils.RoomIconsHelper;

import java.util.Locale;

public class CalibrateRoomBottomSheetDialog extends BottomSheetDialogFragment {

    private final Room room;

    private RoomScanningViewModel roomScanningViewModel;

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

    public CalibrateRoomBottomSheetDialog(Room room) {
        this.room = room;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        roomScanningViewModel = new ViewModelProvider(requireActivity()).get(RoomScanningViewModel.class);
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
        MaterialButtonToggleGroup buttonGroupRoomSize = view.findViewById(R.id.button_group_room_size);
        LinearLayout layoutStartingCalibration = view.findViewById(R.id.layout_starting_calibration);
        Button startCalibrateButton = view.findViewById(R.id.button_start_calibration);

        roomIcon.setImageResource(RoomIconsHelper.getIconResId(room.getIcon()));
        roomName.setText(room.getName());

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
                roomScanningViewModel.setNumberOfScans(numberOfScans);
            } else {
                layoutStartingCalibration.setVisibility(View.INVISIBLE);
            }
        });

        // Starting calibration - show room scaning fragment
        startCalibrateButton.setOnClickListener(v -> viewFlipper.setDisplayedChild(1));

        // RoomScanningFragment
        getChildFragmentManager().beginTransaction()
                .replace(R.id.room_scanning_fragment_container, new RoomScanningFragment())
                .commit();
    }
}
