package com.example.homesweethome.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homesweethome.R;
import com.example.homesweethome.api.RetrofitClient;
import com.example.homesweethome.databinding.ActivityForgotPasswordBinding;
import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.preferences.SessionManager;
import com.example.homesweethome.utils.NetworkUtils;
import com.example.homesweethome.utils.UiUtils;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());
        binding.tvBackLogin.setOnClickListener(v -> finish());
        binding.btnSendOtp.setOnClickListener(v -> attemptSendOtp());
    }

    private void attemptSendOtp() {
        binding.tilNumber.setError(null);

        String inputNumber = binding.etNumber.getText() != null
                ? binding.etNumber.getText().toString().trim() : "";
        
        String number = "+88" + inputNumber;

        Log.d("ForgotPassword", "Phone number entered: " + inputNumber);
        Log.d("ForgotPassword", "Formatted number: " + number);
        Log.d("ForgotPassword", "Number length: " + number.length());


        if (inputNumber.isEmpty() || inputNumber.length() < 11) {
            Log.e("ForgotPassword", "Validation failed - Invalid phone number. Input length: " + inputNumber.length());
            binding.tilNumber.setError("Please enter a valid 11-digit phone number");
            return;
        }

        if (!NetworkUtils.isConnected(this)) {
            Log.e("ForgotPassword", "No network connection");
            UiUtils.showError(binding.getRoot(), getString(R.string.error_network));
            return;
        }

        UiUtils.showLoading(binding.progressBar, binding.btnSendOtp);

        Map<String, String> body = new HashMap<>();
        body.put("landlord_phone", number);


        
        Log.d("ForgotPassword", "Sending request with body: " + body.toString());

        RetrofitClient.getInstance().getAuthService().forgotPassword(body)
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call,
                                           Response<ApiResponse<Void>> response) {
                        Log.d("ForgotPassword", "=== onResponse START ===");
                        Log.d("ForgotPassword", "Response code: " + response.code());
                        Log.d("ForgotPassword", "isSuccessful: " + response.isSuccessful());
                        Log.d("ForgotPassword", "Body is null: " + (response.body() == null));
                        
                        UiUtils.hideLoading(binding.progressBar, binding.btnSendOtp);

                        if (response.isSuccessful() && response.body() != null) {
                            Log.d("ForgotPassword", "API success: " + response.body().isSuccess());
                            Log.d("ForgotPassword", "API message: " + response.body().getMessage());
                            
                            if (response.body().isSuccess()) {
                                Log.d("ForgotPassword", "SUCCESS - Navigating to OTP screen");

                                Intent intent = new Intent(ForgotPasswordActivity.this,
                                        OtpVerificationActivity.class);
                                intent.putExtra(OtpVerificationActivity.EXTRA_EMAIL, number);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.e("ForgotPassword", "API returned success=false");
                                String msg = response.body().getMessage();
                                Log.e("ForgotPassword", "Error message: " + msg);
                                UiUtils.showError(binding.getRoot(), msg);
                            }
                        } else {
                            Log.e("ForgotPassword", "Response not successful or body is null");
                            try {
                                String errorBody = response.errorBody().string();
                                Log.e("ForgotPassword", "Error body: " + errorBody);
                            } catch (Exception e) {
                                Log.e("ForgotPassword", "Could not read error body", e);
                            }
                            String msg = response.body() != null
                                    ? response.body().getMessage()
                                    : getString(R.string.error_generic);
                            UiUtils.showError(binding.getRoot(), msg);
                        }
                        Log.d("ForgotPassword", "=== onResponse END ===");
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                        Log.e("ForgotPassword", "API call failed", t);
                        UiUtils.hideLoading(binding.progressBar, binding.btnSendOtp);
                        UiUtils.showError(binding.getRoot(), getString(R.string.error_network));
                    }
                });
    }
}
