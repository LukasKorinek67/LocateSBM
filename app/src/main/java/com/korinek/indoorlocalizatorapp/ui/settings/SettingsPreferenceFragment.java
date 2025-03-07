package com.korinek.indoorlocalizatorapp.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
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

    EditTextPreference tecoApiUrlPref;
    EditTextPreference tecoApiBuildingNamePref;
    SwitchPreferenceCompat requestAuthorization;
    EditTextPreference authUsername;
    EditTextPreference authPassword;
    Preference roomLoadPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        buildingViewModel = new ViewModelProvider(requireActivity()).get(BuildingViewModel.class);

        initialize();
        setOnChangeListeners();
        setDynamicSummaries();
        initializeRoomLoadButton();
        initializeInfoButton();
        initializeResetButton();

        setEnabledDisabled();
    }

    private void initialize() {
        tecoApiUrlPref = findPreference("settings_teco_api_url");
        tecoApiBuildingNamePref = findPreference("settings_teco_api_building_name");
        requestAuthorization = findPreference("settings_request_authorization");
        authUsername = findPreference("settings_request_authorization_username");
        authPassword = findPreference("settings_request_authorization_password");
        roomLoadPreference = findPreference("settings_room_load");
    }

    private void setOnChangeListeners() {
        if (tecoApiUrlPref != null) {
            tecoApiUrlPref.setOnPreferenceChangeListener((preference, newValue) -> {
                if (newValue instanceof String) {
                    String newUrl = (String) newValue;
                    buildingViewModel.setBuildingTecoApiUrl(newUrl);
                    if (tecoApiBuildingNamePref.getText() != null) {
                        roomLoadPreference.setEnabled(!newUrl.isEmpty() && !tecoApiBuildingNamePref.getText().isEmpty());
                    }
                }
                return true;
            });
        }

        if (tecoApiBuildingNamePref != null) {
            tecoApiBuildingNamePref.setOnPreferenceChangeListener((preference, newValue) -> {
                if (newValue instanceof String) {
                    String newBuildingName = (String) newValue;
                    buildingViewModel.setBuildingTecoApiName(newBuildingName);
                    if (tecoApiUrlPref.getText() != null) {
                        roomLoadPreference.setEnabled(!newBuildingName.isEmpty() && !tecoApiUrlPref.getText().isEmpty());
                    }
                }
                return true;
            });
        }

        if (requestAuthorization != null) {
            requestAuthorization.setOnPreferenceChangeListener((preference, newValue) -> {
                if (newValue instanceof Boolean) {
                    boolean isEnabled = (Boolean) newValue;
                    preference.setIcon(isEnabled? R.drawable.ic_lock_locked :  R.drawable.ic_lock_unlocked);
                    if(authUsername != null && authPassword != null) {
                        authUsername.setEnabled(isEnabled);
                        authPassword.setEnabled(isEnabled);
                    }
                    buildingViewModel.setUseAuthorization(isEnabled);
                    ApiClient.resetClient();
                }
                return true;
            });
        }

        if (authUsername != null) {
            authUsername.setOnPreferenceChangeListener((preference, newValue) -> {
                if (newValue instanceof String) {
                    String newUsername = (String) newValue;
                    buildingViewModel.setAuthorizationUsername(newUsername);
                    ApiClient.resetClient();
                }
                return true;
            });
        }

        if (authPassword != null) {
            authPassword.setOnBindEditTextListener(
                    editText -> editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD));
            authPassword.setOnPreferenceChangeListener((preference, newValue) -> {
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
        buildingViewModel.getIsBuildingSelected().observe(requireActivity(), isBuildingSelected -> {
            if(isBuildingSelected) {
                if(tecoApiUrlPref != null) {
                    tecoApiUrlPref.setEnabled(true);
                }
                if(tecoApiBuildingNamePref != null) {
                    tecoApiBuildingNamePref.setEnabled(true);
                }
                if(requestAuthorization != null && authUsername != null && authPassword != null) {
                    authUsername.setEnabled(requestAuthorization.isChecked());
                    authPassword.setEnabled(requestAuthorization.isChecked());
                }
                if(roomLoadPreference != null &&
                        tecoApiUrlPref != null && tecoApiUrlPref.getText() != null &&
                        tecoApiBuildingNamePref != null && tecoApiBuildingNamePref.getText() != null ) {
                    roomLoadPreference.setEnabled(!tecoApiUrlPref.getText().isEmpty() && !tecoApiBuildingNamePref.getText().isEmpty());

                }
            } else {
                if(tecoApiUrlPref != null) {
                    tecoApiUrlPref.setEnabled(false);
                    tecoApiUrlPref.setText("");
                }
                if(tecoApiBuildingNamePref != null) {
                    tecoApiBuildingNamePref.setEnabled(false);
                    tecoApiBuildingNamePref.setText("");
                }
                if(requestAuthorization != null) {
                    requestAuthorization.setEnabled(false);
                    requestAuthorization.setChecked(false);
                }
                if(authUsername != null) {
                    authUsername.setEnabled(false);
                    authUsername.setText("");
                }
                if(authPassword != null) {
                    authPassword.setEnabled(false);
                    authPassword.setText("");
                }
                if(roomLoadPreference != null) {
                    roomLoadPreference.setEnabled(false);
                }
            }
        });
    }

    private void setDynamicSummaries() {
        if (tecoApiUrlPref != null) {
            tecoApiUrlPref.setSummaryProvider(preference -> {
                String value = tecoApiUrlPref.getText();
                return value != null && !value.isEmpty() ? value : getString(R.string.not_set_text);
            });
        }

        if (tecoApiBuildingNamePref != null) {
            tecoApiBuildingNamePref.setSummaryProvider(preference -> {
                String value = tecoApiBuildingNamePref.getText();
                return value != null && !value.isEmpty() ? value : getString(R.string.not_set_text);
            });
        }

        if (requestAuthorization != null) {
            requestAuthorization.setSummaryProvider(preference -> {
                boolean isEnabled = ((SwitchPreferenceCompat) preference).isChecked();
                return isEnabled ?
                        getString(R.string.settings_request_auth_summary_enabled) :
                        getString(R.string.settings_request_auth_summary_disabled);
            });
        }

        if (authUsername != null) {
            authUsername.setSummaryProvider(preference -> {
                String value = authUsername.getText();
                return value != null && !value.isEmpty() ? value : getString(R.string.not_set_text);
            });
        }

        if (authPassword != null) {
            authPassword.setSummaryProvider(preference -> {
                String value = authPassword.getText();
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
        if (roomLoadPreference != null) {
            roomLoadPreference.setOnPreferenceClickListener(preference -> {
                new AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.settings_room_load_dialog_title))
                        .setMessage(getString(R.string.settings_room_load_dialog_message))
                        .setPositiveButton(getString(R.string.yes), (dialog, which) -> loadRoomsFromApi())
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
                showLoadedRooms(rooms);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoadedRooms(List<Room> rooms) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_loaded_rooms, null);
        bottomSheetDialog.setContentView(view);

        RecyclerView loadedRoomsRecyclerView = view.findViewById(R.id.loaded_rooms_recycler_view);
        Button addLoadedRoomsButton = view.findViewById(R.id.add_loaded_rooms_button);
        Button cancelAddingButton = view.findViewById(R.id.cancel_adding_rooms_button);

        LoadedRoomEditAdapter loadedRoomEditAdapter = new LoadedRoomEditAdapter(requireContext(), rooms);
        loadedRoomsRecyclerView.setAdapter(loadedRoomEditAdapter);
        loadedRoomsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        addLoadedRoomsButton.setOnClickListener(v -> {
            for (Room room : rooms) {
                buildingViewModel.insertRoom(room);
            }
            bottomSheetDialog.dismiss();
            Toast.makeText(requireContext(), getString(R.string.settings_room_load_success_toast), Toast.LENGTH_SHORT).show();
        });

        cancelAddingButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
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
                        .setPositiveButton(getString(R.string.yes), (dialog, which) -> resetApp())
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
