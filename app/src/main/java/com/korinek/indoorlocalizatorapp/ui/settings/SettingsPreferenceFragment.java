package com.korinek.indoorlocalizatorapp.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.korinek.indoorlocalizatorapp.R;
import com.korinek.indoorlocalizatorapp.model.Room;
import com.korinek.indoorlocalizatorapp.ui.building.BuildingViewModel;
import com.korinek.indoorlocalizatorapp.utils.SharedPreferencesHelper;
import com.korinek.indoorlocalizatorapp.utils.api.ApiCalls;
import com.korinek.indoorlocalizatorapp.utils.api.ApiClient;
import com.korinek.indoorlocalizatorapp.utils.api.RequestHandler;

import java.util.List;

public class SettingsPreferenceFragment extends PreferenceFragmentCompat {

    BuildingViewModel buildingViewModel;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        buildingViewModel = new ViewModelProvider(requireActivity()).get(BuildingViewModel.class);

        setDynamicSummaries();
        initializeUrlSettings();
        initializeAuthorizationSettings();
        initializeRoomLoadButton();
        initializeInfoButton();
        initializeResetButton();

        setEnabledDisabled();
    }

    private void initializeUrlSettings() {
        EditTextPreference tecoApiPref = findPreference("settings_teco_api_url");
        if (tecoApiPref != null) {
            tecoApiPref.setOnPreferenceChangeListener((preference, newValue) -> {
                if (newValue instanceof String) {
                    String newUrl = (String) newValue;
                    buildingViewModel.setBuildingTecoApiUrl(newUrl);
                }
                return true;
            });
        }
    }

    private void initializeAuthorizationSettings() {
        SwitchPreferenceCompat requestAuthorization = findPreference("settings_request_authorization");
        EditTextPreference auth_username = findPreference("settings_request_authorization_username");
        EditTextPreference auth_password = findPreference("settings_request_authorization_password");

        if (requestAuthorization != null) {
            requestAuthorization.setOnPreferenceChangeListener((preference, newValue) -> {
                if (newValue instanceof Boolean) {
                    boolean isEnabled = (Boolean) newValue;
                    preference.setIcon(isEnabled? R.drawable.ic_lock_locked :  R.drawable.ic_lock_unlocked);

                    if(auth_username != null && auth_password != null) {
                        auth_username.setEnabled(isEnabled);
                        auth_password.setEnabled(isEnabled);
                    }
                    buildingViewModel.setUseAuthorization(isEnabled);
                    ApiClient.resetClient();
                }
                return true;
            });
        }

        if (auth_username != null) {
            auth_username.setOnPreferenceChangeListener((preference, newValue) -> {
                if (newValue instanceof String) {
                    String newUsername = (String) newValue;
                    buildingViewModel.setAuthorizationUsername(newUsername);
                    ApiClient.resetClient();
                }
                return true;
            });
        }

        if (auth_password != null) {
            auth_password.setOnBindEditTextListener(
                    new EditTextPreference.OnBindEditTextListener() {
                        @Override
                        public void onBindEditText(@NonNull EditText editText) {
                            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }
                    });
            auth_password.setOnPreferenceChangeListener((preference, newValue) -> {
                if (newValue instanceof String) {
                    String newPassword = (String) newValue;
                    buildingViewModel.setAuthorizationPassword(newPassword);
                    ApiClient.resetClient();
                }
                return true;
            });
        }
    }

    private void setEnabledDisabled() {
        EditTextPreference tecoApiPref = findPreference("settings_teco_api_url");
        Preference roomLoadPreference = findPreference("settings_room_load");
        SwitchPreferenceCompat requestAuthorization = findPreference("settings_request_authorization");
        EditTextPreference auth_username = findPreference("settings_request_authorization_username");
        EditTextPreference auth_password = findPreference("settings_request_authorization_password");

        buildingViewModel.getIsBuildingSelected().observe(requireActivity(), isBuildingSelected -> {
            if(isBuildingSelected) {
                if(tecoApiPref != null) {
                    tecoApiPref.setEnabled(true);
                }
                if(roomLoadPreference != null) {
                    roomLoadPreference.setEnabled(true);
                }
                if(requestAuthorization != null && auth_username != null && auth_password != null) {
                    auth_username.setEnabled(requestAuthorization.isChecked());
                    auth_password.setEnabled(requestAuthorization.isChecked());
                }
            } else {
                if(tecoApiPref != null) {
                    tecoApiPref.setEnabled(false);
                    tecoApiPref.setText("");
                }
                if(roomLoadPreference != null) {
                    roomLoadPreference.setEnabled(false);
                }
                if(requestAuthorization != null) {
                    requestAuthorization.setEnabled(false);
                    requestAuthorization.setChecked(false);
                }
                if(auth_username != null) {
                    auth_username.setEnabled(false);
                    auth_username.setText("");
                }
                if(auth_password != null) {
                    auth_password.setEnabled(false);
                    auth_password.setText("");
                }
            }
        });
    }

    private void setDynamicSummaries() {
        EditTextPreference tecoApiPref = findPreference("settings_teco_api_url");
        if (tecoApiPref != null) {
            tecoApiPref.setSummaryProvider(preference -> {
                String value = tecoApiPref.getText();
                return value != null && !value.isEmpty() ? value : getString(R.string.not_set_text);
            });
        }

        SwitchPreferenceCompat requestAuthorization = findPreference("settings_request_authorization");
        if (requestAuthorization != null) {
            requestAuthorization.setSummaryProvider(preference -> {
                boolean isEnabled = ((SwitchPreferenceCompat) preference).isChecked();
                return isEnabled ?
                        getString(R.string.settings_request_auth_summary_enabled) :
                        getString(R.string.settings_request_auth_summary_disabled);
            });
        }

        EditTextPreference auth_username = findPreference("settings_request_authorization_username");
        if (auth_username != null) {
            auth_username.setSummaryProvider(preference -> {
                String value = auth_username.getText();
                return value != null && !value.isEmpty() ? value : getString(R.string.not_set_text);
            });
        }

        EditTextPreference auth_password = findPreference("settings_request_authorization_password");
        if (auth_password != null) {
            auth_password.setSummaryProvider(preference -> {
                String value = auth_password.getText();
                if (value != null && !value.isEmpty()) {
                    return new String(new char[value.length()]).replace("\0", "•");
                } else {
                    return getString(R.string.not_set_text);
                }
            });
        }

        ListPreference wifiSorting = findPreference("settings_wifi_sorting");
        if (wifiSorting != null) {
            wifiSorting.setSummaryProvider(preference -> {
                String value = (String) wifiSorting.getEntry();
                return value != null && !value.isEmpty() ? value : getString(R.string.not_set_text);
            });
        }

        SwitchPreferenceCompat continuousWifiScan = findPreference("settings_enable_continuous_wifi_scan");
        if (continuousWifiScan != null) {
            continuousWifiScan.setSummaryProvider(preference -> {
                boolean isEnabled = ((SwitchPreferenceCompat) preference).isChecked();
                return isEnabled ? "Nepřetržité skenování zapnuto" : "Nepřetržité skenování vypnuto";
            });
        }
    }

    private void initializeRoomLoadButton() {
        Preference roomLoadPreference = findPreference("settings_room_load");
        if (roomLoadPreference != null) {
            roomLoadPreference.setOnPreferenceClickListener(preference -> {
                new AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.settings_room_load_dialog_title))
                        .setMessage(getString(R.string.settings_room_load_dialog_message))
                        .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                            loadRoomsFromApi();
                        })
                        .setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss())
                        .show();
                return true;
            });
        }
    }

    private void loadRoomsFromApi() {
        RequestHandler requestHandler = new RequestHandler(requireContext());
        requestHandler.getRoomsFromApi(new ApiCalls.RoomsFromApiCallback() {
            @Override
            public void onSuccess(List<Room> rooms) {
                for (Room room : rooms) {
                    buildingViewModel.insertRoom(room);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                // TODO

            }
        });
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
