package com.example.homesweethome.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homesweethome.R;
import com.example.homesweethome.api.RetrofitClient;
import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.model.User;
import com.example.homesweethome.preferences.SessionManager;
import com.example.homesweethome.databinding.LandlordDashboardBinding;
import com.example.homesweethome.utils.UiUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LandlordDashboardActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private LandlordDashboardBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LandlordDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        User user = sessionManager.getUser();

        binding.btnAddProperty.setOnClickListener(v -> openAddBuildingActivity());
        binding.btnAddFlat.setOnClickListener(v -> openFlatActivity());
        binding.btnLogout.setOnClickListener(v -> logout(v));

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

    private void openAddBuildingActivity() {
        Intent intent = new Intent(this, LandlordAddBuildingActivity.class);
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
