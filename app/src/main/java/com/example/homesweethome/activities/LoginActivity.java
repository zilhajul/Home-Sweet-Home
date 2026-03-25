package com.example.homesweethome.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homesweethome.R;
import com.example.homesweethome.api.RetrofitClient;
import com.example.homesweethome.databinding.ActivityLoginBinding;
import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.model.LoginRequest;
import com.example.homesweethome.model.User;
import com.example.homesweethome.preferences.SessionManager;
import com.example.homesweethome.utils.NetworkUtils;
import com.example.homesweethome.utils.UiUtils;
import com.example.homesweethome.utils.ValidationUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_ROLE = "extra_role";

    private ActivityLoginBinding binding;
    private SessionManager sessionManager;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        role = getIntent().getStringExtra(EXTRA_ROLE);
        if (role == null) role = sessionManager.getRole();
        if (role == null) role = "tenant";

        setupUI();
        setupClickListeners();
    }

    private void setupUI() {
        // Style role badge based on role
        String badgeText = role.equals("landlord") ? "Landlord" : "Tenant";
        binding.tvRoleBadge.setText(badgeText);

        int badgeColor = role.equals("landlord")
                ? getColor(R.color.landlord_light)
                : getColor(R.color.tenant_light);
        int textColor = role.equals("landlord")
                ? getColor(R.color.landlord_color)
                : getColor(R.color.tenant_color);

        binding.tvRoleBadge.setBackgroundColor(badgeColor);
        binding.tvRoleBadge.setTextColor(textColor);
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.tvForgot.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            intent.putExtra(ForgotPasswordActivity.EXTRA_ROLE, role);
            startActivity(intent);
        });

        binding.tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.putExtra(RegisterActivity.EXTRA_ROLE, role);
            startActivity(intent);
        });

        binding.btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        // Clear previous errors
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);

        String email = binding.etEmail.getText() != null
                ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null
                ? binding.etPassword.getText().toString() : "";

        // Validate
        boolean hasError = false;

        if (!ValidationUtils.isValidEmail(email)) {
            binding.tilEmail.setError(getString(R.string.error_invalid_email));
            hasError = true;
        }

        if (TextUtils.isEmpty(password)) {
            binding.tilPassword.setError(getString(R.string.error_empty_password));
            hasError = true;
        } else if (!ValidationUtils.isValidPassword(password)) {
            binding.tilPassword.setError(getString(R.string.error_short_password));
            hasError = true;
        }

        if (hasError) return;

        // Check network
        if (!NetworkUtils.isConnected(this)) {
            UiUtils.showError(binding.getRoot(), getString(R.string.error_network));
            return;
        }

        // Make API call
        UiUtils.showLoading(binding.progressBar, binding.btnLogin);

        LoginRequest request = new LoginRequest(email, password, role);
        RetrofitClient.getInstance().setSessionManager(sessionManager);
        RetrofitClient.getInstance().getAuthService().login(request)
                .enqueue(new Callback<ApiResponse<User>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<User>> call,
                                           Response<ApiResponse<User>> response) {
                        UiUtils.hideLoading(binding.progressBar, binding.btnLogin);

                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<User> apiResponse = response.body();
                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                onLoginSuccess(apiResponse.getData());
                            } else {
                                UiUtils.showError(binding.getRoot(),
                                        apiResponse.getMessage() != null
                                                ? apiResponse.getMessage()
                                                : getString(R.string.error_generic));
                            }
                        } else {
                            UiUtils.showError(binding.getRoot(), getString(R.string.error_generic));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                        UiUtils.hideLoading(binding.progressBar, binding.btnLogin);
                        UiUtils.showError(binding.getRoot(), getString(R.string.error_network));
                    }
                });
    }

    private void onLoginSuccess(User user) {
        sessionManager.saveSession(user);

        Intent intent;
        if (user.isLandlord()) {
            intent = new Intent(this, LandlordDashboardActivity.class);
        } else {
            intent = new Intent(this, TenantDashboardActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
