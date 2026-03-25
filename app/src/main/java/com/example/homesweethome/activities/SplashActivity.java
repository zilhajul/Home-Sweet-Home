package com.example.homesweethome.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.example.homesweethome.R;
import com.example.homesweethome.preferences.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY_MS = 1800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SessionManager session = new SessionManager(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (session.isLoggedIn()) {
                // Already logged in — route to correct dashboard
                if (session.isLandlord()) {
                    startActivity(new Intent(this, LandlordDashboardActivity.class));
                } else {
                    startActivity(new Intent(this, TenantDashboardActivity.class));
                }
            } else {
                // Not logged in — show role selection
                startActivity(new Intent(this, RoleSelectionActivity.class));
            }
            finish();
        }, SPLASH_DELAY_MS);
    }
}
