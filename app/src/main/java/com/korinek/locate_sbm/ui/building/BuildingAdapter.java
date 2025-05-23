package com.korinek.locate_sbm.ui.building;


import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.korinek.locate_sbm.model.Building;
import com.korinek.locate_sbm.R;

import java.util.ArrayList;
import java.util.List;

public class BuildingAdapter extends RecyclerView.Adapter<BuildingAdapter.BuildingViewHolder> {
    private final List<Building> buildings = new ArrayList<>();
    private Building selectedBuilding;
    private final BuildingActionListener listener;

    public interface BuildingActionListener {
        void onBuildingClick(Building building);
        void onBuildingSwiped(Building building);
    }

    public BuildingAdapter(BuildingActionListener listener) {
        this.listener = listener;
    }

    public void updateBuildings(List<Building> buildings, Building selectedBuilding) {
        this.buildings.clear();
        this.buildings.addAll(buildings);
        this.selectedBuilding = selectedBuilding;
        notifyDataSetChanged();
    }

    public Building getBuildingAt(int position) {
        return buildings.get(position);
    }

    @NonNull
    @Override
    public BuildingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_building_item, parent, false);
        return new BuildingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BuildingViewHolder holder, int position) {
        Building building = buildings.get(position);
        holder.bind(building, selectedBuilding, listener);
    }

    @Override
    public int getItemCount() {
        return buildings.size();
    }

    public BuildingActionListener getListener() {
        return listener;
    }

    static class BuildingViewHolder extends RecyclerView.ViewHolder {
        private final View buildingColorView;
        private final TextView buildingNameTextView;
        private final ImageView checkMarkSelected;

        public BuildingViewHolder(@NonNull View itemView) {
            super(itemView);
            buildingNameTextView = itemView.findViewById(R.id.building_text_view);
            buildingColorView = itemView.findViewById(R.id.building_color_view);
            checkMarkSelected = itemView.findViewById(R.id.check_mark_selected);
        }

        public void bind(Building building, Building selectedBuilding, BuildingActionListener listener) {
            GradientDrawable drawable = (GradientDrawable) buildingColorView.getBackground();
            drawable.setColor(ContextCompat.getColor(itemView.getContext(), building.getColor()));
            buildingNameTextView.setText(building.getName());
            checkMarkSelected.setVisibility(isBuildingSelected(building, selectedBuilding) ? View.VISIBLE : View.GONE);
            itemView.setOnClickListener(v -> listener.onBuildingClick(building));
        }

        private boolean isBuildingSelected(Building building, Building selectedBuilding) {
            // TODO - maybe check by some id?
            return building.equals(selectedBuilding);
        }
    }
}
