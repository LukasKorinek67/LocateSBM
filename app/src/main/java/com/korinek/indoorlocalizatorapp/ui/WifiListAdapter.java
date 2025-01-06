package com.korinek.indoorlocalizatorapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.ViewHolder>{

    private final List<String> wifiList;

    public WifiListAdapter(List<String> wifiList) {
        this.wifiList = wifiList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(wifiList.get(position));
    }

    @Override
    public int getItemCount() {
        return wifiList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
