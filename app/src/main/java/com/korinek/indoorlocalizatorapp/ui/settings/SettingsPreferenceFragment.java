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
                        .setTitle("Info o aplikaci")
                        .setMessage("Info o aplikaci... \n\nTato aplikace byla vytvořena v rámci diplomové práce na TUL FM.")
                        .setPositiveButton("Zavřít", (dialog, which) -> dialog.dismiss())
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
                        .setTitle("Reset aplikace")
                        .setMessage("Reset vymaže všechna data a obnoví všechna nastavení aplikace na výchozí hodnoty. Opravdu chcete resetovat celou aplikaci?")
                        .setPositiveButton("Ano", (dialog, which) -> {
                            resetApp();
                        })
                        .setNegativeButton("Ne", (dialog, which) -> dialog.dismiss())
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
