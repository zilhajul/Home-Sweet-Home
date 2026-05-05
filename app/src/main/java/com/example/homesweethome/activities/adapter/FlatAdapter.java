package com.example.homesweethome.activities.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homesweethome.R;
import com.example.homesweethome.activities.LandlordFlatActivity;
import com.example.homesweethome.activities.ShowAllTenants;
import com.example.homesweethome.activities.ViewInfo;
import com.example.homesweethome.activities.fragment.FlatListFragment;
import com.example.homesweethome.api.RetrofitClient;
import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.model.Flat;
import com.example.homesweethome.model.FlatResponse;
import com.example.homesweethome.model.TenantResponse;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FlatAdapter extends RecyclerView.Adapter<FlatAdapter.FlatViewHolder> {

    private List<Flat> flatList;
    private Context context;
    private String buildingId,landlordId;



    public FlatAdapter(List<Flat> flatList, Context context, String buildingId, String landlordId) {
        this.flatList = flatList;
        this.context = context;
        this.buildingId = buildingId;
        this.landlordId = landlordId;
    }

    @NonNull
    @Override
    public FlatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_flat, parent, false);
        return new FlatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlatViewHolder holder, int position) {
        Flat flat = flatList.get(position);
        android.util.Log.d("FlatAdapter", "Displaying Flat: " + flat.getFlatName());

        holder.tvFlatName.setText(String.valueOf(flat.getFlatName()));
        holder.tvFloorNumber.setText(String.valueOf(flat.getFloorNumber()));
        holder.tvFlatRent.setText(flat.getFlatRent() + " BDT");
        holder.tvStatus.setText(flat.getFlatStatus());
        holder.tvWaterBill.setText(flat.getWaterBill() + " BDT");
        holder.tvGasBill.setText(flat.getGasBill() + " BDT");
        holder.tvElectricityBill.setText(flat.getElectricityBill() + " BDT");
        holder.tvServiceCharge.setText(flat.getServiceCharge() + " BDT");

        if (flat.getTenantId()==null ){

            holder.btnAssignTenant.setVisibility(View.VISIBLE);
            holder.btnViewInfo.setVisibility(View.VISIBLE);
            holder.btnUpdateTenant.setVisibility(View.GONE);
            holder.btnDeleteTenant.setVisibility(View.GONE);


        }else {
            holder.btnAssignTenant.setVisibility(View.GONE);
            holder.btnUpdateTenant.setVisibility(View.VISIBLE);
            holder.btnDeleteTenant.setVisibility(View.VISIBLE);
            holder.btnViewInfo.setVisibility(View.VISIBLE);

        }

        holder.btnAssignTenant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ShowAllTenants.class);
                intent.putExtra("flatId", flat.getId());
                intent.putExtra("buildingId", buildingId);
                intent.putExtra("landlordId", landlordId);
                intent.putExtra("action", "assign");
                context.startActivity(intent);


            }
        });

        holder.btnUpdateTenant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ShowAllTenants.class);
                intent.putExtra("flatId", flat.getId());
                intent.putExtra("buildingId", buildingId);
                intent.putExtra("landlordId", landlordId);
                intent.putExtra("action", "update");
                context.startActivity(intent);

            }
        });

        holder.btnViewInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            Intent intent = new Intent(context, ViewInfo.class);
            intent.putExtra("flatId", flat.getId());
            intent.putExtra("buildingId", buildingId);
            intent.putExtra("landlordId", landlordId);
            if (flat.getTenantId() != null) {
                intent.putExtra("tenantId", flat.getTenantId().getId());
            }
            context.startActivity(intent);


            }
        });

        holder.btnDeleteTenant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String flatId = flat.getId();

                HashMap<String, String> body = new HashMap<>();
                body.put("flat_id", flatId);


                RetrofitClient.getInstance(context).getAuthService().deleteTenant(body)
                        .enqueue(new Callback<ApiResponse<TenantResponse>>() {
                            @Override
                            public void onResponse(Call<ApiResponse<TenantResponse>> call, Response<ApiResponse<TenantResponse>> response) {

                                if (response.isSuccessful() && response.body() != null) {
                                    ApiResponse<TenantResponse> apiResponse = response.body();

                                    if (apiResponse.isSuccess()) {
                                        Toast.makeText(context, "Tenant Deleted Successfully ", Toast.LENGTH_SHORT).show();
                                        flat.setTenantId(null);

                                        notifyItemChanged(holder.getAdapterPosition());

                                    }
                                }else {
                                    try {
                                        String errorBody = response.errorBody().string();

                                        JSONObject jsonObject = new JSONObject(errorBody);
                                        String message = jsonObject.getString("message");

                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(context, "Failed To Delete Tenant", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }

                            @Override
                            public void onFailure(Call<ApiResponse<TenantResponse>> call, Throwable throwable) {

                            }
                        });



            }
        });

        holder.btnDeleteFlat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String flatId = flat.getId();

                HashMap<String, String> body = new HashMap<>();
                body.put("_id", flatId);

                RetrofitClient.getInstance(context).getFlatService().deleteFlat(body)
                        .enqueue(new Callback<ApiResponse<FlatResponse>>() {


                            @Override
                            public void onResponse(Call<ApiResponse<FlatResponse>> call, Response<ApiResponse<FlatResponse>> response) {

                                if (response.isSuccessful() && response.body() != null) {
                                    ApiResponse<FlatResponse> apiResponse = response.body();

                                    if (apiResponse.isSuccess()) {
                                        Toast.makeText(context, "Flat Deleted Successfully ", Toast.LENGTH_SHORT).show();
                                        flatList.remove(holder.getAdapterPosition());
                                        notifyItemRemoved(holder.getAdapterPosition());
                                    }
                                }else {
                                    try {
                                        String errorBody = response.errorBody().string();

                                        JSONObject jsonObject = new JSONObject(errorBody);
                                        String message = jsonObject.getString("message");

                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(context, "Failed To Delete Flat", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }

                            @Override
                            public void onFailure(Call<ApiResponse<FlatResponse>> call, Throwable throwable) {

                                Toast.makeText(context, "Failed To Delete Flat", Toast.LENGTH_SHORT).show();

                            }
                        });

            }
        });

        holder.btnEditFlat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LandlordFlatActivity.class);
                intent.putExtra("flatId", flat.getId());
                intent.putExtra("buildingId", buildingId);
                intent.putExtra("landlordId", landlordId);
                intent.putExtra("action", "edit");
                context.startActivity(intent);
            }
        });


        Glide.with(context)
                .load(flat.getFlatImage())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivFlatImage);
    }

    @Override
    public int getItemCount() {
        return flatList != null ? flatList.size() : 0;
    }

    public static class FlatViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFlatImage;
        TextView tvFlatName, tvFloorNumber, tvFlatRent, tvStatus, tvGasBill, tvElectricityBill, tvWaterBill, tvServiceCharge;
        Button btnAssignTenant, btnViewInfo, btnUpdateTenant, btnDeleteTenant, btnDeleteFlat, btnEditFlat;

        public FlatViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFlatImage = itemView.findViewById(R.id.ivFlatImage);
            tvFlatName = itemView.findViewById(R.id.tvFlatName);
            tvFloorNumber = itemView.findViewById(R.id.tvFloorNumber);
            tvFlatRent = itemView.findViewById(R.id.tvFlatRent);
            tvStatus = itemView.findViewById(R.id.tvFlatStatus);
            tvGasBill = itemView.findViewById(R.id.tvGasBill);
            tvElectricityBill = itemView.findViewById(R.id.tvElectricityBill);
            tvWaterBill = itemView.findViewById(R.id.tvWaterBill);
            tvServiceCharge = itemView.findViewById(R.id.tvServiceCharge);
            btnAssignTenant = itemView.findViewById(R.id.assignTenantId);
            btnViewInfo = itemView.findViewById(R.id.viewInfoId);
            btnUpdateTenant = itemView.findViewById(R.id.changeTenantId);
            btnDeleteTenant = itemView.findViewById(R.id.deleteTenantId);
            btnDeleteFlat = itemView.findViewById(R.id.deleteFlatId);
            btnEditFlat = itemView.findViewById(R.id.editFlatId);
        }
    }
}
