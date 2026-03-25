package com.example.homesweethome.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homesweethome.R;
import com.example.homesweethome.api.RetrofitClient;
import com.example.homesweethome.databinding.ActivityForgotPasswordBinding;
import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.utils.NetworkUtils;
import com.example.homesweethome.utils.UiUtils;
import com.example.homesweethome.utils.ValidationUtils;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    public static final String EXTRA_ROLE = "extra_role";

    private ActivityForgotPasswordBinding binding;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        role = getIntent().getStringExtra(EXTRA_ROLE);
        if (role == null) role = "tenant";

        binding.btnBack.setOnClickListener(v -> finish());
        binding.tvBackLogin.setOnClickListener(v -> finish());
        binding.btnSendOtp.setOnClickListener(v -> attemptSendOtp());
    }

    private void attemptSendOtp() {
        binding.tilEmail.setError(null);

        String email = binding.etEmail.getText() != null
                ? binding.etEmail.getText().toString().trim() : "";

        if (!ValidationUtils.isValidEmail(email)) {
            binding.tilEmail.setError(getString(R.string.error_invalid_email));
            return;
        }

        if (!NetworkUtils.isConnected(this)) {
            UiUtils.showError(binding.getRoot(), getString(R.string.error_network));
            return;
        }

        UiUtils.showLoading(binding.progressBar, binding.btnSendOtp);

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("role", role);

        RetrofitClient.getInstance().getAuthService().forgotPassword(body)
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call,
                                           Response<ApiResponse<Void>> response) {
                        UiUtils.hideLoading(binding.progressBar, binding.btnSendOtp);

                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            // Navigate to OTP screen
                            Intent intent = new Intent(ForgotPasswordActivity.this,
                                    OtpVerificationActivity.class);
                            intent.putExtra(OtpVerificationActivity.EXTRA_EMAIL, email);
                            intent.putExtra(OtpVerificationActivity.EXTRA_ROLE, role);
                            startActivity(intent);
                        } else {
                            String msg = response.body() != null
                                    ? response.body().getMessage()
                                    : getString(R.string.error_generic);
                            UiUtils.showError(binding.getRoot(), msg);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                        UiUtils.hideLoading(binding.progressBar, binding.btnSendOtp);
                        UiUtils.showError(binding.getRoot(), getString(R.string.error_network));
                    }
                });
    }
}
