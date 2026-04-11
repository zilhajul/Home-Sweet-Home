package com.example.homesweethome.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homesweethome.R;
import com.example.homesweethome.api.RetrofitClient;
import com.example.homesweethome.databinding.ActivityResetPasswordBinding;
import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.utils.NetworkUtils;
import com.example.homesweethome.utils.UiUtils;
import com.example.homesweethome.utils.ValidationUtils;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    public static final String EXTRA_EMAIL = "extra_email";
    public static final String EXTRA_OTP   = "extra_otp";

    private ActivityResetPasswordBinding binding;
    private String number;
    private String otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        number = getIntent().getStringExtra(EXTRA_EMAIL);

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnReset.setOnClickListener(v -> attemptReset());
    }

    private void attemptReset() {
        binding.tilNewPassword.setError(null);

        String newPass  = getText(binding.etNewPassword);

        boolean hasError = false;

        if (!ValidationUtils.isValidPassword(newPass)) {
            binding.tilNewPassword.setError(getString(R.string.error_short_password));
            hasError = true;
        }


        if (hasError) return;

        if (!NetworkUtils.isConnected(this)) {
            UiUtils.showError(binding.getRoot(), getString(R.string.error_network));
            return;
        }

        UiUtils.showLoading(binding.progressBar, binding.btnReset);

        Map<String, String> body = new HashMap<>();
        body.put("landlord_phone", number);

        body.put("landlord_password", newPass);


        RetrofitClient.getInstance(this).getAuthService().resetPassword(body)
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call,
                                           Response<ApiResponse<Void>> response) {
                        UiUtils.hideLoading(binding.progressBar, binding.btnReset);

                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            UiUtils.showSuccess(binding.getRoot(),
                                    getString(R.string.success_reset));
                            // Go back to role selection / login
                            Intent intent = new Intent(ResetPasswordActivity.this,
                                    RoleSelectionActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
                        UiUtils.hideLoading(binding.progressBar, binding.btnReset);
                        UiUtils.showError(binding.getRoot(), getString(R.string.error_network));
                    }
                });
    }

    private String getText(android.widget.EditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
}
