package com.example.homesweethome.activities.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homesweethome.R;
import com.example.homesweethome.activities.ShowAllTenants;
import com.example.homesweethome.api.RetrofitClient;
import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.model.Building;
import com.example.homesweethome.model.Landlord;
import com.example.homesweethome.model.Tenant;
import com.example.homesweethome.model.TenantResponse;
import com.example.homesweethome.preferences.SessionManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TenantAdapter extends RecyclerView.Adapter<TenantAdapter.TenantViewHolder> {

    private List<Tenant> tenantList;
    private List<Tenant> filteredTenantList;
    private Context context;
    private String flatId,buildingId,landlordId;
    private String action;
    private SessionManager sessionManager;

    public TenantAdapter(List<Tenant> tenantList, Context context, String flatId, String buildingId, String landlordId, String action) {
        this.tenantList = tenantList;
        this.context = context;
        this.action = action;
        this.flatId = flatId;
        this.buildingId = buildingId;
        this.landlordId = landlordId;
        this.filteredTenantList= tenantList;



    }

    @NonNull
    @Override
    public TenantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_tenant, parent, false);

        return new TenantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TenantViewHolder holder, int position) {

        Tenant tenant = filteredTenantList.get(position);

        holder.tvTenantName.setText(tenant.getTenantName());
        holder.tvTenantPhone.setText(tenant.getTenantPhone());
        holder.tvTenantAddress.setText(tenant.getTenantAddress());
        holder.tvTenantStatus.setText(tenant.getTenantStatus());

        Glide.with(context)
                .load(tenant.getTenantImage())
                .placeholder(R.drawable.account)
                .into(holder.ivTenantImage);




        holder.tenantCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               if(action.equals("assign")){

                   holder.tenantCardView.setCardBackgroundColor(context.getResources().getColor(R.color.text_hint));


                   String tenantId = tenant.getId();


                   Map<String, String> body = new HashMap<>();

                   body.put("flat_id", flatId);
                   body.put("tenant_id", tenantId);
                   body.put("building_id", buildingId);
                   body.put("landlord_id", landlordId);

                   Log.d("TenantAdapter", "FlatId : "+flatId);
                   Log.d("TenantAdapter", "TenantId : "+tenantId);
                   Log.d("TenantAdapter", "BuildingId : "+buildingId);
                   Log.d("TenantAdapter", "LandlordId : "+landlordId);



                   RetrofitClient.getInstance(context).getAuthService().assignTenant(body)
                           .enqueue(new Callback<ApiResponse<TenantResponse>>() {


                               @Override
                               public void onResponse(Call<ApiResponse<TenantResponse>> call, Response<ApiResponse<TenantResponse>> response) {
                                   if (response.isSuccessful() && response.body() != null) {
                                       ApiResponse<TenantResponse> apiResponse = response.body();

                                       if (apiResponse.isSuccess()) {
                                           Toast.makeText(context, "Tenant Added Successfully ", Toast.LENGTH_SHORT).show();

                                           Log.d("TenantAdapter", "IsSuccess: "+apiResponse.isSuccess());
                                           Log.d("TenantAdapter", "API Success: "+apiResponse.getMessage());

                                           ((ShowAllTenants) context).finish();




                                       } else {
                                           String message = apiResponse.getMessage();

                                           Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                                           Log.d("TenantAdapter", "Fail Message: " + message);

                                       }
                                   }else {
                                       try {
                                           String errorBody = response.errorBody().string();

                                           JSONObject jsonObject = new JSONObject(errorBody);
                                           String message = jsonObject.getString("message");

                                           Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                                       } catch (Exception e) {
                                           e.printStackTrace();
                                           Toast.makeText(context, "Failed To Add Tenant", Toast.LENGTH_SHORT).show();
                                       }
                                   }
                               }

                               @Override
                               public void onFailure(Call<ApiResponse<TenantResponse>> call, Throwable throwable) {

                                   Toast.makeText(context, "Failed To Add Tenant", Toast.LENGTH_SHORT).show();
                                   Log.d("TenantAdapter", "API Failure: "+throwable.getMessage());


                               }
                           });


               }
               else if (action.equals("update")) {
                   holder.tenantCardView.setCardBackgroundColor(context.getResources().getColor(R.color.text_hint));


                   String tenantId = tenant.getId();


                   Map<String, String> body = new HashMap<>();

                   body.put("flat_id", flatId);
                   body.put("tenant_id", tenantId);
                   body.put("building_id", buildingId);
                   body.put("landlord_id", landlordId);

                   Log.d("TenantAdapter", "FlatId : "+flatId);
                   Log.d("TenantAdapter", "TenantId : "+tenantId);
                   Log.d("TenantAdapter", "BuildingId : "+buildingId);
                   Log.d("TenantAdapter", "LandlordId : "+landlordId);



                   RetrofitClient.getInstance(context).getAuthService().updateTenant(body)
                           .enqueue(new Callback<ApiResponse<TenantResponse>>() {


                               @Override
                               public void onResponse(Call<ApiResponse<TenantResponse>> call, Response<ApiResponse<TenantResponse>> response) {
                                   if (response.isSuccessful() && response.body() != null) {
                                       ApiResponse<TenantResponse> apiResponse = response.body();

                                       if (apiResponse.isSuccess()) {
                                           Toast.makeText(context, "Tenant Added Successfully ", Toast.LENGTH_SHORT).show();

                                           Log.d("TenantAdapter", "IsSuccess: "+apiResponse.isSuccess());
                                           Log.d("TenantAdapter", "API Success: "+apiResponse.getMessage());

                                           ((ShowAllTenants) context).finish();


                                       } else {
                                           String message = apiResponse.getMessage();

                                           Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                                           Log.d("TenantAdapter", "Fail Message: " + message);

                                       }
                                   }else {
                                       try {
                                           String errorBody = response.errorBody().string();

                                           JSONObject jsonObject = new JSONObject(errorBody);
                                           String message = jsonObject.getString("message");

                                           Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                                       } catch (Exception e) {
                                           e.printStackTrace();
                                           Toast.makeText(context, "Failed To Add Tenant", Toast.LENGTH_SHORT).show();
                                       }
                                   }
                               }

                               @Override
                               public void onFailure(Call<ApiResponse<TenantResponse>> call, Throwable throwable) {

                                   Toast.makeText(context, "Failed To Add Tenant", Toast.LENGTH_SHORT).show();
                                   Log.d("TenantAdapter", "API Failure: "+throwable.getMessage());


                               }
                           });







               }
            }


        });
    }

    @Override
    public int getItemCount() {

        return filteredTenantList != null ? filteredTenantList.size() : 0;
    }

    public void filter(String text) {

        filteredTenantList.clear();

        if (text == null || text.isEmpty()) {
            filteredTenantList.addAll(tenantList);
        } else {

            String query = text.toLowerCase();
            for (Tenant t : tenantList) {

                if (t.getTenantName().toLowerCase().contains(query) ||
                        t.getTenantPhone().contains(query)) {

                    filteredTenantList.add(t);
                }
            }
        }

        notifyDataSetChanged();
    }




    public static class TenantViewHolder extends RecyclerView.ViewHolder{

        ImageView ivTenantImage;
        TextView tvTenantName, tvTenantPhone, tvTenantAddress, tvTenantStatus;
        CardView tenantCardView;

        public TenantViewHolder(@NonNull View itemView) {
            super(itemView);

            ivTenantImage = itemView.findViewById(R.id.imgTenant);
            tvTenantName = itemView.findViewById(R.id.tvTenantName);
            tvTenantPhone = itemView.findViewById(R.id.tvTenantPhone);
            tvTenantAddress = itemView.findViewById(R.id.tvTenantAddress);
            tvTenantStatus = itemView.findViewById(R.id.tvTenantStatus);
            tenantCardView = itemView.findViewById(R.id.cvTenant);

        }
    }


}
