package com.korinek.indoorlocalizatorapp.ui.localization;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.korinek.indoorlocalizatorapp.R;
import com.korinek.indoorlocalizatorapp.databinding.FragmentLocalizationBinding;
import com.korinek.indoorlocalizatorapp.ui.localization.accelerometer.AccelerometerFragment;
import com.korinek.indoorlocalizatorapp.ui.localization.gyroscope.GyroscopeFragment;
import com.korinek.indoorlocalizatorapp.ui.localization.wifi_analysis.WifiAnalysisFragment;

public class LocalizationFragment extends Fragment {

    private FragmentLocalizationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LocalizationViewModel localizationViewModel =
                new ViewModelProvider(this).get(LocalizationViewModel.class);

        binding = FragmentLocalizationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // check and request for permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        addFragmentsOnScreen();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void addFragmentsOnScreen() {
        // WifiAnalysisFragment
        getChildFragmentManager().beginTransaction()
                .replace(R.id.wifi_analysis_container, new WifiAnalysisFragment())
                .commit();

        // GyroscopeFragment
        getChildFragmentManager().beginTransaction()
                .replace(R.id.gyroscope_container, new GyroscopeFragment())
                .commit();

        // AccelerometerFragment
        getChildFragmentManager().beginTransaction()
                .replace(R.id.accelerometer_container, new AccelerometerFragment())
                .commit();
    }
}
