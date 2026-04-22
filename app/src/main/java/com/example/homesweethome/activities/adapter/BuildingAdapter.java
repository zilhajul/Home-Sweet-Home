package com.example.homesweethome.activities.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homesweethome.R;
import com.example.homesweethome.databinding.ItemBuildingBinding;
import com.example.homesweethome.model.Building;

import java.util.List;

public class BuildingAdapter extends RecyclerView.Adapter<BuildingAdapter.BuildingViewHolder> {

    private List<Building> buildingList;
    private Context context;
    private OnBuildingClickListener listener;

    public interface OnBuildingClickListener {
        void onViewClick(Building building);
        void onEditClick(Building building);
    }

    public BuildingAdapter(List<Building> buildingList, Context context, OnBuildingClickListener listener) {
        this.buildingList = buildingList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BuildingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBuildingBinding binding = ItemBuildingBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new BuildingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BuildingViewHolder holder, int position) {
        Building building = buildingList.get(position);
        holder.bind(building);
    }

    @Override
    public int getItemCount() {
        return buildingList != null ? buildingList.size() : 0;
    }

    public void updateList(List<Building> newList) {
        this.buildingList = newList;
        notifyDataSetChanged();
    }

    public class BuildingViewHolder extends RecyclerView.ViewHolder {
        private ItemBuildingBinding binding;

        public BuildingViewHolder(ItemBuildingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Building building) {
            // Load building image using Glide
            if (building.getBuildingImage() != null && !building.getBuildingImage().isEmpty()) {
                Glide.with(context)
                        .load(building.getBuildingImage())
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(binding.ivBuildingImage);
            }

            // Set building details
            binding.tvBuildingName.setText(building.getBuildingName() != null ? 
                    building.getBuildingName() : "N/A");
            
            binding.tvBuildingAddress.setText(building.getBuildingAddress() != null ? 
                    building.getBuildingAddress() : "N/A");
            
            binding.tvBuildingStatus.setText(building.getBuildingStatus() != null ? 
                    building.getBuildingStatus() : "Active");
            
            binding.tvTotalFloors.setText(building.getTotalFloors() != null ? 
                    building.getTotalFloors() : "0");
            
            binding.tvTotalFlats.setText(building.getTotalFlats() != null ? 
                    building.getTotalFlats() : "0");
            
            binding.tvZone.setText(building.getZoneName() != null ? 
                    building.getZoneName() : "N/A");

            // Set status badge color based on status
            setStatusBadgeColor(building.getBuildingStatus());

            // Set click listeners
            binding.btnView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewClick(building);
                }
            });

            binding.btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(building);
                }
            });
        }

        private void setStatusBadgeColor(String status) {
            if ("Active".equalsIgnoreCase(status)) {
                binding.tvBuildingStatus.setBackgroundResource(R.drawable.bg_status_active);
                binding.tvBuildingStatus.setTextColor(0xFF27ae60);
            } else if ("Inactive".equalsIgnoreCase(status)) {
                binding.tvBuildingStatus.setBackgroundResource(R.drawable.bg_status_inactive);
                binding.tvBuildingStatus.setTextColor(0xFF95a5a6);
            }
        }
    }
}

