package com.korinek.indoorlocalizatorapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.korinek.indoorlocalizatorapp.R;

import java.util.List;

public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.WifiViewHolder>{

    private final List<String> wifiList;

    public WifiListAdapter(List<String> wifiList) {
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

    static class WifiViewHolder extends RecyclerView.ViewHolder {
        private final TextView wifiTextView;

        public WifiViewHolder(@NonNull View itemView) {
            super(itemView);
            wifiTextView = itemView.findViewById(R.id.wifi_text_view);
        }

        public void bind(String text) {
            wifiTextView.setText(text);
        }
    }
}
