package com.example.homesweethome.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homesweethome.databinding.ActivityRoleSelectionBinding;
import com.example.homesweethome.preferences.SessionManager;

public class RoleSelectionActivity extends AppCompatActivity {

    private ActivityRoleSelectionBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRoleSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        binding.cardLandlord.setOnClickListener(v -> {
            sessionManager.saveSelectedRole("landlord");
            navigateToLogin("landlord");
        });

        binding.cardTenant.setOnClickListener(v -> {
            sessionManager.saveSelectedRole("tenant");
            navigateToLogin("tenant");
        });
    }

    private void navigateToLogin(String role) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_ROLE, role);
        startActivity(intent);
    }
}
