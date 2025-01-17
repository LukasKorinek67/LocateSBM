package com.korinek.indoorlocalizatorapp.ui.building;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.korinek.indoorlocalizatorapp.databinding.FragmentBuildingBinding;


public class BuildingFragment extends Fragment {

    private FragmentBuildingBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        BuildingViewModel buildingViewModel =
                new ViewModelProvider(this).get(BuildingViewModel.class);

        binding = FragmentBuildingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textBuilding;
        buildingViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}