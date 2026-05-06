package com.example.homesweethome.activities.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homesweethome.R;
import com.example.homesweethome.activities.LandlordAddBuildingActivity;
import com.example.homesweethome.api.RetrofitClient;
import com.example.homesweethome.databinding.ItemBuildingBinding;
import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.model.Building;
import com.example.homesweethome.model.BuildingsResponse;
import com.example.homesweethome.model.TenantResponse;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

                String buildingId = building.getId();

                Intent intent = new Intent(context, LandlordAddBuildingActivity.class);
                intent.putExtra("buildingId", buildingId);
                intent.putExtra("action", "edit");
                context.startActivity(intent);

            });

            binding.btnDeleteBuilding.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
            String buildingId = building.getId();

                    HashMap<String,String> body = new HashMap<>();

                    body.put("_id",buildingId);


                    RetrofitClient.getInstance(context).getBuildingService().deleteBuilding(body)
                            .enqueue(new Callback<ApiResponse<BuildingsResponse>>() {

                                @Override
                                public void onResponse(Call<ApiResponse<BuildingsResponse>> call, Response<ApiResponse<BuildingsResponse>> response) {
                                    if (response.isSuccessful() && response.body() != null){
                                        if (response.body().isSuccess()){
                                            buildingList.remove(building);
                                            notifyDataSetChanged();
                                        }
                                    }else {
                                        try {
                                            String errorBody = response.errorBody().string();

                                            JSONObject jsonObject = new JSONObject(errorBody);
                                            String message = jsonObject.getString("message");

                                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Toast.makeText(context, "Failed To DELETE BUILDING", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ApiResponse<BuildingsResponse>> call, Throwable throwable) {

                                }
                            });

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

