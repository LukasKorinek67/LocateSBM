package com.korinek.indoorlocalizatorapp.ui.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.korinek.indoorlocalizatorapp.R;
import com.korinek.indoorlocalizatorapp.utils.SharedPreferencesHelper;

public class SettingsPreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        initializeInfoButton();
        initializeResetButton();
    }

    private void initializeInfoButton() {
        Preference infoPreference = findPreference("settings_about_app_info");
        if (infoPreference != null) {
            infoPreference.setOnPreferenceClickListener(preference -> {
                new AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.settings_app_info))
                        .setMessage(getString(R.string.settings_app_info_text))
                        .setPositiveButton(getString(R.string.close), (dialog, which) -> dialog.dismiss())
                        .show();
                return true;
            });
        }
    }

    private void initializeResetButton() {
        Preference resetPreference = findPreference("settings_reset_app");
        if (resetPreference != null) {
            resetPreference.setOnPreferenceClickListener(preference -> {
                new AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.settings_app_reset))
                        .setMessage(getString(R.string.settings_app_reset_text))
                        .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                            resetApp();
                        })
                        .setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss())
                        .show();
                return true;
            });
        }
    }

    private void resetApp() {
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(requireContext());
        sharedPreferencesHelper.resetApp();

        Intent intent = requireActivity().getIntent();
        requireActivity().finish();
        startActivity(intent);
    }
}
