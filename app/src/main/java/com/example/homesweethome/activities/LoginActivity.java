package com.example.homesweethome.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homesweethome.R;
import com.example.homesweethome.api.RetrofitClient;
import com.example.homesweethome.databinding.ActivityLoginBinding;
import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.model.Landlord;
import com.example.homesweethome.model.LoginRequest;
import com.example.homesweethome.model.SignInResponse;
import com.example.homesweethome.model.TenantLoginRequest;
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

        binding.tilPassword.setError(null);

        String number = binding.etNumber.getText() != null
                ? "+88"+binding.etNumber.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null
                ? binding.etPassword.getText().toString() : "";

        boolean hasError = false;

        if (TextUtils.isEmpty(number)) {
            binding.etNumber.setError(getString(R.string.error_empty_phone));
            hasError = true;
        } else if (!ValidationUtils.isValidPhone(number)) {
            binding.etNumber.setError(getString(R.string.error_invalid_phone));
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

        if (!NetworkUtils.isConnected(this)) {
            UiUtils.showError(binding.getRoot(), getString(R.string.error_network));
            return;
        }

        UiUtils.showLoading(binding.progressBar, binding.btnLogin);

        if(role!=null && role.equalsIgnoreCase("landlord")){

            LoginRequest request = new LoginRequest(number, password);
            RetrofitClient.getInstance(this).setSessionManager(sessionManager);
            RetrofitClient.getInstance(this).getAuthService().landlordLogin(request)
                    .enqueue(new Callback<ApiResponse<SignInResponse>>() {
                        @Override
                        public void onResponse(@NonNull Call<ApiResponse<SignInResponse>> call,
                                               @NonNull Response<ApiResponse<SignInResponse>> response) {
                            UiUtils.hideLoading(binding.progressBar, binding.btnLogin);

                            Log.d("LoginActivity", "=== onResponse START ===");
                            Log.d("LoginActivity", "isSuccessful: " + response.isSuccessful());
                            Log.d("LoginActivity", "Response code: " + response.code());
                            Log.d("LoginActivity", "Body is null: " + (response.body() == null));
                            
                            if (!response.isSuccessful()) {
                                Log.d("LoginActivity", "Response is not successful (status code not 2xx)");
                                try {
                                    if (response.errorBody() != null) {
                                        String errorBody = response.errorBody().string();
                                        Log.d("LoginActivity", "Error body: " + errorBody);
                                    }
                                } catch (Exception e) {
                                    Log.e("LoginActivity", "Error reading error body", e);
                                }
                                UiUtils.showError(binding.getRoot(), "HTTP Error: " + response.code());
                                return;
                            }
                            
                            if (response.body() == null) {
                                Log.d("LoginActivity", "Response body is null");
                                UiUtils.showError(binding.getRoot(), getString(R.string.error_generic));
                                return;
                            }
                            
                            ApiResponse<SignInResponse> apiResponse = response.body();
                            Log.d("LoginActivity", "API success field: " + apiResponse.isSuccess());
                            Log.d("LoginActivity", "API message: " + apiResponse.getMessage());
                            Log.d("LoginActivity", "API data is null: " + (apiResponse.getData() == null));
                            
                            if (apiResponse.getData() != null) {
                                Log.d("LoginActivity", "Token: " + apiResponse.getData().getToken());
                            }
                            
                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                Log.d("LoginActivity", "SUCCESS - Calling onLoginSuccess");
                                Toast successToast = Toast.makeText(LoginActivity.this, "✓ Login Successful!", Toast.LENGTH_LONG);
                                successToast.show();
                                onLoginSuccess(apiResponse.getData());
                            } else {
                                Log.d("LoginActivity", "FAILED - success=" + apiResponse.isSuccess() + ", data=" + (apiResponse.getData() != null));
                                UiUtils.showError(binding.getRoot(),
                                        apiResponse.getMessage() != null
                                                ? apiResponse.getMessage()
                                                : getString(R.string.error_generic));
                            }
                            Log.d("LoginActivity", "=== onResponse END ===");
                        }

                        @Override
                        public void onFailure(@NonNull Call<ApiResponse<SignInResponse>> call, @NonNull Throwable t) {
                            UiUtils.hideLoading(binding.progressBar, binding.btnLogin);
                            Log.e("LoginActivity", "API call failed: " + t.getMessage(), t);
                            UiUtils.showError(binding.getRoot(), getString(R.string.error_network));
                        }
                    });
        }else {
            TenantLoginRequest request = new TenantLoginRequest(number, password);
            RetrofitClient.getInstance(this).setSessionManager(sessionManager);
            RetrofitClient.getInstance(this).getAuthService().tenantLogin(request)
                    .enqueue(new Callback<ApiResponse<SignInResponse>>() {
                        @Override
                        public void onResponse(@NonNull Call<ApiResponse<SignInResponse>> call,
                                               @NonNull Response<ApiResponse<SignInResponse>> response) {
                            UiUtils.hideLoading(binding.progressBar, binding.btnLogin);

                            Log.d("LoginActivity", "=== onResponse START ===");
                            Log.d("LoginActivity", "isSuccessful: " + response.isSuccessful());
                            Log.d("LoginActivity", "Response code: " + response.code());
                            Log.d("LoginActivity", "Body is null: " + (response.body() == null));

                            if (!response.isSuccessful()) {
                                Log.d("LoginActivity", "Response is not successful (status code not 2xx)");
                                try {
                                    if (response.errorBody() != null) {
                                        String errorBody = response.errorBody().string();
                                        Log.d("LoginActivity", "Error body: " + errorBody);
                                    }
                                } catch (Exception e) {
                                    Log.e("LoginActivity", "Error reading error body", e);
                                }
                                UiUtils.showError(binding.getRoot(), "HTTP Error: " + response.code());
                                return;
                            }

                            if (response.body() == null) {
                                Log.d("LoginActivity", "Response body is null");
                                UiUtils.showError(binding.getRoot(), getString(R.string.error_generic));
                                return;
                            }

                            ApiResponse<SignInResponse> apiResponse = response.body();
                            Log.d("LoginActivity", "API success field: " + apiResponse.isSuccess());
                            Log.d("LoginActivity", "API message: " + apiResponse.getMessage());
                            Log.d("LoginActivity", "API data is null: " + (apiResponse.getData() == null));

                            if (apiResponse.getData() != null) {
                                Log.d("LoginActivity", "Token: " + apiResponse.getData().getToken());
                            }

                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                Log.d("LoginActivity", "SUCCESS - Calling onLoginSuccess");
                                Toast successToast = Toast.makeText(LoginActivity.this, "✓ Login Successful!", Toast.LENGTH_LONG);
                                successToast.show();
                                onLoginSuccess(apiResponse.getData());
                            } else {
                                Log.d("LoginActivity", "FAILED - success=" + apiResponse.isSuccess() + ", data=" + (apiResponse.getData() != null));
                                UiUtils.showError(binding.getRoot(),
                                        apiResponse.getMessage() != null
                                                ? apiResponse.getMessage()
                                                : getString(R.string.error_generic));
                            }
                            Log.d("LoginActivity", "=== onResponse END ===");
                        }

                        @Override
                        public void onFailure(@NonNull Call<ApiResponse<SignInResponse>> call, @NonNull Throwable t) {
                            UiUtils.hideLoading(binding.progressBar, binding.btnLogin);
                            Log.e("LoginActivity", "API call failed: " + t.getMessage(), t);
                            UiUtils.showError(binding.getRoot(), getString(R.string.error_network));
                        }
                    });
        }

    }

    private void onLoginSuccess(SignInResponse response) {
        Log.d("LoginActivity", "onLoginSuccess called with token: " + response.getToken());
        
        // Store token temporarily in SessionManager
        sessionManager.saveSelectedRole(role);
        
        if ("landlord".equalsIgnoreCase(role)) {
            // For landlord: fetch landlord info and then proceed
            fetchLandlordInfo(response.getToken());
        } else {
            // For tenant: proceed directly to dashboard
            Intent intent = new Intent(this, TenantDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void fetchLandlordInfo(String token) {
        Log.d("LoginActivity", "Fetching landlord information with token");

        SharedPreferences prefs = getSharedPreferences("house_rent_session", MODE_PRIVATE);
        prefs.edit().putString("auth_token", token).apply();
        
        RetrofitClient.getInstance(this).setSessionManager(sessionManager);
        
        RetrofitClient.getInstance(this).getAuthService().getLandlord()
                .enqueue(new Callback<ApiResponse<Landlord>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<Landlord>> call,
                                         @NonNull Response<ApiResponse<Landlord>> response) {
                        Log.d("LoginActivity", "=== Landlord Info Response ===");
                        Log.d("LoginActivity", "isSuccessful: " + response.isSuccessful());
                        
                        if (!response.isSuccessful()) {
                            Log.e("LoginActivity", "Failed to fetch landlord info: HTTP " + response.code());
                            UiUtils.showError(binding.getRoot(), "Failed to fetch landlord information");
                            return;
                        }
                        
                        if (response.body() == null || response.body().getData() == null) {
                            Log.e("LoginActivity", "Landlord info response is null");
                            UiUtils.showError(binding.getRoot(), getString(R.string.error_generic));
                            return;
                        }
                        
                        Landlord landlord = response.body().getData();
                        Log.d("LoginActivity", "Landlord info fetched successfully: " + landlord.getLandlordName());
                        Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        
                        // Save landlord info to SessionManager
                        sessionManager.saveLandlordInfo(landlord);
                        
                        // Navigate to subscription screen
                        if (landlord.getSubscriptionPurchase() != null && landlord.getSubscriptionPurchase().getSubscriptionId() != null
                        && landlord.getSubscriptionPurchase().isPaymentComplete()){
                            Intent intent = new Intent(LoginActivity.this, LandlordDashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }else {
                            Intent intent = new Intent(LoginActivity.this, SubscriptionActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                    
                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<Landlord>> call,
                                         @NonNull Throwable t) {
                        Log.e("LoginActivity", "Failed to fetch landlord info: " + t.getMessage(), t);
                        UiUtils.showError(binding.getRoot(), getString(R.string.error_network));
                    }
                });
    }



}
