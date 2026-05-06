package com.example.homesweethome.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homesweethome.R;
import com.example.homesweethome.activities.fragment.TotalProperties;
import com.example.homesweethome.api.RetrofitClient;
import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.model.Building;
import com.example.homesweethome.model.BuildingsResponse;
import com.example.homesweethome.model.Landlord;
import com.example.homesweethome.model.User;
import com.example.homesweethome.preferences.SessionManager;
import com.example.homesweethome.databinding.LandlordDashboardBinding;
import com.example.homesweethome.utils.UiUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LandlordDashboardActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private LandlordDashboardBinding binding;
    private String landlordId;
    Landlord landlord;
    int TotalProperties;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LandlordDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        landlordId = getIntent().getStringExtra("landlordId");


        sessionManager = new SessionManager(this);
        landlord = sessionManager.getLandlord();
       getBuildings();

        binding.btnAddProperty.setOnClickListener(v -> openAddBuildingActivity());
        binding.propertyCardId.setOnClickListener(v -> openTotalPropertiesActivity());
        binding.tvLandlordName.setText(landlord.getLandlordName());
        binding.ivNotification.setOnClickListener(view -> openNotificationActivity());

        TotalProperties = landlord.getAlreadyBuildingAdded();
       // int TotalTenants = landlord.getTenantAdded();
        binding.tvPropertiesCount.setText(String.valueOf(TotalProperties));
        binding.btnAddFlat.setOnClickListener(v -> openFlatActivity());
        binding.btnLogout.setOnClickListener(v -> logout(v));
        binding.btnGenerateRent.setOnClickListener(v -> openGenerateRentActivity());


    }

    private void openNotificationActivity() {
        Intent intent = new Intent(this, Notification.class);
        intent.putExtra("landlordId", landlordId);
        startActivity(intent);
    }


    private void openGenerateRentActivity() {
        Intent intent = new Intent(this, Generate_Rent.class);
        intent.putExtra("landlordId", landlordId);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        getBuildings();
    }

    public void logout(View v) {
        RetrofitClient.getInstance(this).setSessionManager(sessionManager);
        RetrofitClient.getInstance(this).getAuthService().logout()
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call,
                                           Response<ApiResponse<Void>> response) {
                        clearAndGoToRole();
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                        clearAndGoToRole(); // Clear locally even if API fails
                    }
                });
    }

    private void getBuildings(){

        sessionManager = new SessionManager(this);
        landlordId = sessionManager.getLandlord().getId();



        Context context = getApplicationContext();
        if (context == null) return;

        RetrofitClient client = RetrofitClient.getInstance(context);
        client.setSessionManager(sessionManager);

        client.getBuildingService()
                .getBuildingsByLandlord(landlordId)
                .enqueue(new Callback<BuildingsResponse>() {
                    @Override
                    public void onResponse(Call<BuildingsResponse> call,
                                           Response<BuildingsResponse> response) {


                        if (response.isSuccessful() && response.body() != null) {
                            BuildingsResponse apiResponse = response.body();
                            if (apiResponse.isSuccess()) {
                                List<Building> buildings = apiResponse.getData();
                                TotalProperties = apiResponse.getTotalData();
//                                binding.tvPropertiesCount.setText(apiResponse.getTotalData());
                                if (buildings != null && !buildings.isEmpty()) {
                                    if (landlord.getRemainingBuildingAdd()==buildings.size()){
                                        binding.btnAddProperty.setEnabled(false);
                                        binding.btnAddFlat.setEnabled(false);
                                        Toast.makeText(LandlordDashboardActivity.this, "You are exceeded your limit", Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                } else {
                                    Toast.makeText(context, "No Building Added", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d("LandlordFlatActivity","message: "+apiResponse.getMessage());
                            }
                        } else {
                            Log.d("LandlordFlatActivity","message: "+response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<BuildingsResponse> call, Throwable t) {

                    }
                });

    }

    private void openAddBuildingActivity() {
        Intent intent = new Intent(this, LandlordAddBuildingActivity.class);
        intent.putExtra("landlordId", landlordId);
        startActivity(intent);
    }

    private void openTotalPropertiesActivity() {
      Intent intent = new Intent(this, TotalPropertiesActivity.class);
      intent.putExtra("landlordId", landlordId);
       startActivity(intent);

    }

    private void openFlatActivity() {
        Intent intent = new Intent(this, LandlordFlatActivity.class);
        startActivity(intent);
    }

    private void clearAndGoToRole() {
        sessionManager.clearSession();
        Intent intent = new Intent(this, RoleSelectionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
