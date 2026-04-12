package com.example.homesweethome.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homesweethome.R;
import com.example.homesweethome.api.RetrofitClient;
import com.example.homesweethome.databinding.ActivityRegisterBinding;
import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.model.RegisterRequest;
import com.example.homesweethome.model.User;
import com.example.homesweethome.preferences.SessionManager;
import com.example.homesweethome.utils.NetworkUtils;
import com.example.homesweethome.utils.UiUtils;
import com.example.homesweethome.utils.ValidationUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    public static final String EXTRA_ROLE = "extra_role";

    private ActivityRegisterBinding binding;
    private SessionManager sessionManager;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
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

        binding.tvLogin.setOnClickListener(v -> finish());

        binding.btnRegister.setOnClickListener(v -> attemptRegister());
    }

    private void attemptRegister() {
        binding.tilName.setError(null);

        binding.tilPhone.setError(null);
        binding.tilPassword.setError(null);
        binding.tilConfirmPassword.setError(null);

        String name = getText(binding.etName);

        String phone = "+88"+ getText(binding.etPhone);
        String password = getText(binding.etPassword);
        String confirmPassword = getText(binding.etConfirmPassword);

        boolean hasError = false;

        if (!ValidationUtils.isNotEmpty(name)) {
            binding.tilName.setError(getString(R.string.error_empty_name));
            hasError = true;
        }

        if (!ValidationUtils.isValidPhone(phone)) {
            binding.tilPhone.setError(getString(R.string.error_invalid_phone));
            hasError = true;
        }
        if (!ValidationUtils.isValidPassword(password)) {
            binding.tilPassword.setError(getString(R.string.error_short_password));
            hasError = true;
        }
        if (!ValidationUtils.passwordsMatch(password, confirmPassword)) {
            binding.tilConfirmPassword.setError(getString(R.string.error_password_mismatch));
            hasError = true;
        }

        if (hasError) return;

        if (!NetworkUtils.isConnected(this)) {
            UiUtils.showError(binding.getRoot(), getString(R.string.error_network));
            return;
        }

        UiUtils.showLoading(binding.progressBar, binding.btnRegister);

        RegisterRequest request = new RegisterRequest(name, phone, password);
        RetrofitClient.getInstance(this).getAuthService().register(request)
                .enqueue(new Callback<ApiResponse<User>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<User>> call,
                                           Response<ApiResponse<User>> response) {
                        UiUtils.hideLoading(binding.progressBar, binding.btnRegister);

                        if (response.body() != null) {
                            ApiResponse<User> apiResponse = response.body();
                            if (apiResponse.isSuccess()) {
//                                UiUtils.showSuccess(binding.getRoot(), apiResponse.getMessage());
                                Toast.makeText(RegisterActivity.this, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                                navigateToLogin();
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else if (response.errorBody() != null) {
                            try {
                                Gson gson = new Gson();
                                ApiResponse<User> errorResponse = gson.fromJson(response.errorBody().string(), new TypeToken<ApiResponse<User>>(){}.getType());
                                Toast.makeText(RegisterActivity.this, errorResponse.getMessage(), Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                UiUtils.showError(binding.getRoot(), getString(R.string.error_generic));
                            }
                        } else {
                            UiUtils.showError(binding.getRoot(), getString(R.string.error_generic));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                        UiUtils.hideLoading(binding.progressBar, binding.btnRegister);
                        UiUtils.showError(binding.getRoot(), getString(R.string.error_network));
                    }
                });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_ROLE, role);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private String getText(android.widget.EditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
}
