package com.korinek.locate_sbm.ui.startup_dialog;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.korinek.locate_sbm.R;
import com.korinek.locate_sbm.utils.SharedPreferencesHelper;

public class WifiScanInfoDialog {
    public static void showWifiScanInfoIfNeeded(Activity activity) {
        SharedPreferencesHelper sharedPrefs = new SharedPreferencesHelper(activity.getApplicationContext());
        if(!sharedPrefs.wasWifiScanInfoShown()) {
            show(activity);
            sharedPrefs.setWifiScanInfoShown();
        }
    }

    private static void show(Activity activity) {
        BottomSheetDialog dialog = new BottomSheetDialog(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.bottom_sheet_wifi_scan_info, null);
        dialog.setContentView(view);

        Button buttonUnderstand = view.findViewById(R.id.wifi_scan_info_button_understand);
        buttonUnderstand.setOnClickListener(v -> dialog.dismiss());

        Button buttonOpenSettings = view.findViewById(R.id.wifi_scan_info_button_open_settings);
        buttonOpenSettings.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivity(intent);
            } else {
                // fallback - open main settings screen
                Intent fallbackIntent = new Intent(Settings.ACTION_SETTINGS);
                activity.startActivity(fallbackIntent);
            }
            dialog.dismiss();
        });
        dialog.show();
    }
}
