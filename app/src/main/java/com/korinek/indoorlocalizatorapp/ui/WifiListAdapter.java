package com.korinek.indoorlocalizatorapp.ui;

import android.annotation.SuppressLint;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.korinek.indoorlocalizatorapp.R;

import java.util.List;

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
        private final TextView wifiTextView;
        private final TextView wifiSignalStrength;
        private final TextView wifiRttRange;

        public WifiViewHolder(@NonNull View itemView) {
            super(itemView);
            wifiTextView = itemView.findViewById(R.id.wifi_text_view);
            wifiSignalStrength = itemView.findViewById(R.id.wifi_signal_strength);
            wifiRttRange = itemView.findViewById(R.id.rtt_text);
        }

        public void bind(ScanResult wifi) {
            String ssid = (wifi.SSID == null || wifi.SSID.isBlank()) ? "[No-SSID]" : wifi.SSID;
            wifiTextView.setText(ssid);
            wifiSignalStrength.setText(wifi.level + " dBm");
            wifiRttRange.setText(wifi.is80211mcResponder()? "RTT Wi-Fi" : "");
        }
    }
}
