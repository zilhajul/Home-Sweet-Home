package com.example.homesweethome.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homesweethome.R;
import com.example.homesweethome.api.RetrofitClient;
import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.model.User;
import com.example.homesweethome.preferences.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Placeholder for Epic 2 — Tenant Dashboard.
 * Currently shows a welcome screen and logout.
 */
public class TenantDashboardActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setGravity(android.view.Gravity.CENTER);
        layout.setPadding(48, 48, 48, 48);
        layout.setBackgroundColor(getColor(R.color.background));

        User user = sessionManager.getUser();

        TextView tvWelcome = new TextView(this);
        tvWelcome.setText("Welcome, " + (user != null ? user.getName() : "Tenant") + "!");
        tvWelcome.setTextSize(24f);
        tvWelcome.setTextColor(getColor(R.color.text_primary));
        tvWelcome.setGravity(android.view.Gravity.CENTER);

        TextView tvSub = new TextView(this);
        tvSub.setText("Tenant Dashboard\n(Epic 2 — coming next)");
        tvSub.setTextSize(14f);
        tvSub.setTextColor(getColor(R.color.text_secondary));
        tvSub.setGravity(android.view.Gravity.CENTER);
        tvSub.setPadding(0, 16, 0, 48);

        Button btnLogout = new Button(this);
        btnLogout.setText("Logout");
        btnLogout.setOnClickListener(v -> logout());

        layout.addView(tvWelcome);
        layout.addView(tvSub);
        layout.addView(btnLogout);

        setContentView(layout);
    }

    private void logout() {
        RetrofitClient.getInstance().setSessionManager(sessionManager);
        RetrofitClient.getInstance().getAuthService().logout()
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call,
                                           Response<ApiResponse<Void>> response) {
                        clearAndGoToRole();
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                        clearAndGoToRole();
                    }
                });
    }

    private void clearAndGoToRole() {
        sessionManager.clearSession();
        Intent intent = new Intent(this, RoleSelectionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
