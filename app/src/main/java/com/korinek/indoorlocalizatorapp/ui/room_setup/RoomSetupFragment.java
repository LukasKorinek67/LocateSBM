package com.korinek.indoorlocalizatorapp.ui.room_setup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.korinek.indoorlocalizatorapp.R;
import com.korinek.indoorlocalizatorapp.databinding.FragmentRoomSetupBinding;
import com.korinek.indoorlocalizatorapp.ui.custom_views.WheelView;
import com.korinek.indoorlocalizatorapp.utils.RoomAttributesHelper;

import java.util.Locale;
import java.util.Map;

public class RoomSetupFragment extends Fragment {

    private static final String ARG_ROOM_NAME = "roomName";
    private static final String ARG_ROOM_ICON = "roomIcon";
    private FragmentRoomSetupBinding binding;
    RoomSetupViewModel roomSetupViewModel;
    private String roomName;
    private int roomIcon;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            roomName = getArguments().getString(ARG_ROOM_NAME);
            roomIcon = getArguments().getInt(ARG_ROOM_ICON);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        roomSetupViewModel = new ViewModelProvider(requireActivity()).get(RoomSetupViewModel.class);

        binding = FragmentRoomSetupBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (roomName != null) {
            roomSetupViewModel.setRoomName(roomName);
            initializeComponents();
        } else {
            // if no room name, then go back
            getParentFragmentManager().popBackStack();
            Log.e("RoomSetupFragment", "No room name provided, going back...");
        }
        return root;
    }

    private void initializeComponents() {
        TextView textRoomName = binding.textRoomName;
        TextView roomSetupInfoTextView = binding.roomSetupInfoTextView;
        TextView roomSetupErrorTextView = binding.roomSetupErrorTextView;
        ImageButton buttonBack = binding.buttonBack;
        ImageView roomSetupIcon = binding.roomSetupIcon;
        WheelView wheelView = binding.wheelView;

        textRoomName.setText(roomName);
        roomSetupIcon.setImageResource(roomIcon);
        roomSetupInfoTextView.setText(R.string.info_loading);
        roomSetupInfoTextView.setVisibility(View.VISIBLE);

        roomSetupViewModel.getRoom().observe(getViewLifecycleOwner(), room -> {
            if(room != null) {
                Map<String, Object> attributes = room.getAttributes();
                attributes = RoomAttributesHelper.filterNegativeAttributes(attributes);
                if (attributes.isEmpty()) {
                    roomSetupInfoTextView.setText(getString(R.string.info_room_has_no_attributtes));
                } else {
                    attributes = RoomAttributesHelper.sortAttributes(attributes);
                    wheelView.setData(attributes);
                    wheelView.setVisibility(View.VISIBLE);
                    roomSetupInfoTextView.setVisibility(View.GONE);
                    roomSetupErrorTextView.setVisibility(View.GONE);
                }
            } else {
                roomSetupErrorTextView.setVisibility(View.VISIBLE);
                roomSetupInfoTextView.setVisibility(View.GONE);
                wheelView.setVisibility(View.GONE);
            }
        });

        roomSetupViewModel.getErrorText().observe(getViewLifecycleOwner(), errorText -> {
            if(errorText != null) {
                roomSetupErrorTextView.setText(String.format(Locale.getDefault(), getString(R.string.info_data_not_loaded_with_error_message), errorText));
            } else {
                roomSetupErrorTextView.setText(getString(R.string.info_data_not_loaded));

            }
        });

        buttonBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
