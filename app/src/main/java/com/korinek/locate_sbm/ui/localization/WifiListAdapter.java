package com.korinek.locate_sbm.ui.localization;

import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.korinek.locate_sbm.R;

import java.util.List;
import java.util.Locale;

public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.WifiViewHolder>{

    private List<ScanResult> wifiList;

    public WifiListAdapter(List<ScanResult> wifiList) {
        this.wifiList = wifiList;
    }

    @NonNull
    @Override
    public WifiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_wifi_item, parent, false);
        return new WifiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WifiViewHolder holder, int position) {
        holder.bind(wifiList.get(position));
    }

    @Override
    public int getItemCount() {
        return wifiList.size();
    }

    public void updateWifiList(List<ScanResult> wifiList) {
        this.wifiList = wifiList;
        notifyDataSetChanged();
    }

    static class WifiViewHolder extends RecyclerView.ViewHolder {
        private final TextView wifiSignalStrength;
        private final TextView wifiSSIDTextView;
        private final TextView wifiMacTextView;

        public WifiViewHolder(@NonNull View itemView) {
            super(itemView);
            wifiSignalStrength = itemView.findViewById(R.id.wifi_signal_strength);
            wifiSSIDTextView = itemView.findViewById(R.id.wifi_ssid_text_view);
            wifiMacTextView = itemView.findViewById(R.id.wifi_mac_text_view);
        }

        public void bind(ScanResult wifi) {
            String ssid = (wifi.SSID == null || wifi.SSID.isBlank()) ? itemView.getContext().getString(R.string.no_ssid) : wifi.SSID;
            wifiSSIDTextView.setText(ssid);
            wifiSignalStrength.setText(String.format(Locale.getDefault(), itemView.getContext().getString(R.string.wifi_signal_with_unit), wifi.level));
            wifiMacTextView.setText(wifi.BSSID);
        }
    }
}
