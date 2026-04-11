package com.example.homesweethome.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homesweethome.R;
import com.example.homesweethome.api.RetrofitClient;
import com.example.homesweethome.databinding.ActivityOtpVerificationBinding;
import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.utils.NetworkUtils;
import com.example.homesweethome.utils.UiUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerificationActivity extends AppCompatActivity {

    public static final String EXTRA_EMAIL = "extra_email";

    private static final long COUNTDOWN_MS   = 60_000L;
    private static final long COUNTDOWN_TICK = 1_000L;

    private ActivityOtpVerificationBinding binding;
    private String number;
    private CountDownTimer countDownTimer;

    private EditText[] otpBoxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        number = getIntent().getStringExtra(EXTRA_EMAIL);

        binding.tvSubtitle.setText(
                getString(R.string.otp_subtitle, number != null ? number : "your number"));

        otpBoxes = new EditText[]{
                binding.etOtp1, binding.etOtp2, binding.etOtp3,
                binding.etOtp4
        };

        setupOtpBoxes();
        startCountdown();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.tvResend.setOnClickListener(v -> resendOtp());
        binding.btnVerify.setOnClickListener(v -> attemptVerify());
    }

    private void setupOtpBoxes() {
        for (int i = 0; i < otpBoxes.length; i++) {
            final int index = i;

            otpBoxes[i].addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
                @Override public void afterTextChanged(Editable s) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < otpBoxes.length - 1) {
                        otpBoxes[index + 1].requestFocus();
                    }
                }
            });

            otpBoxes[i].setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && keyCode == KeyEvent.KEYCODE_DEL
                        && otpBoxes[index].getText().toString().isEmpty()
                        && index > 0) {
                    otpBoxes[index - 1].requestFocus();
                    otpBoxes[index - 1].setText("");
                    return true;
                }
                return false;
            });
        }
        otpBoxes[0].requestFocus();
    }

    private String getOtpValue() {
        StringBuilder sb = new StringBuilder();
        for (EditText box : otpBoxes) {
            String value = box.getText().toString().trim();
            sb.append(value);
            Log.d("OtpVerification", "OTP Box value: '" + value + "' (length: " + value.length() + ")");
        }
        String otp = sb.toString();
        Log.d("OtpVerification", "Final OTP: '" + otp + "' (total length: " + otp.length() + ")");
        return otp;
    }

    private void startCountdown() {
        binding.tvTimer.setVisibility(View.VISIBLE);
        binding.llResend.setVisibility(View.GONE);

        countDownTimer = new CountDownTimer(COUNTDOWN_MS, COUNTDOWN_TICK) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secs = millisUntilFinished / 1000;
                binding.tvTimer.setText("Resend in " + secs + "s");
            }

            @Override
            public void onFinish() {
                binding.tvTimer.setVisibility(View.GONE);
                binding.llResend.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void resendOtp() {
        if (!NetworkUtils.isConnected(this)) {
            UiUtils.showError(binding.getRoot(), getString(R.string.error_network));
            return;
        }

        for (EditText box : otpBoxes) box.setText("");
        otpBoxes[0].requestFocus();

        Map<String, String> body = new HashMap<>();
        body.put("landlord_phone", number);

        RetrofitClient.getInstance(this).getAuthService().forgotPassword(body)
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call,
                                           Response<ApiResponse<Void>> response) {
                        UiUtils.showSuccess(binding.getRoot(), "OTP sent again!");
                        startCountdown();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {

                        UiUtils.showError(binding.getRoot(), getString(R.string.error_network));
                    }
                });
    }

    private void attemptVerify() {
        String otp = getOtpValue();

        if (otp.length() != 4) {
            UiUtils.showError(binding.getRoot(), "OTP must be 4 digits");
            return;
        }

        if (!NetworkUtils.isConnected(this)) {
            UiUtils.showError(binding.getRoot(), getString(R.string.error_network));
            return;
        }

        UiUtils.showLoading(binding.progressBar, binding.btnVerify);

        Map<String, Object> body = new HashMap<>();
        body.put("landlord_phone", number);
        body.put("forgot_password_otp", Integer.parseInt(otp));

        RetrofitClient.getInstance(this).getAuthService().verifyOtp(body)
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call,
                                           Response<ApiResponse<Void>> response) {
                        UiUtils.hideLoading(binding.progressBar, binding.btnVerify);

                        Log.d("OtpVerification", "Final OTP: '" + otp + "'");
                        Log.d("OtpVerification", "Phone: " + number);

                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<Void> apiResponse = response.body();

                            if (apiResponse.isSuccess()) {
                                Intent intent = new Intent(OtpVerificationActivity.this,
                                        ResetPasswordActivity.class);
                                intent.putExtra(ResetPasswordActivity.EXTRA_EMAIL, number);
                                startActivity(intent);
                                finish();
                            } else {
                                UiUtils.showError(binding.getRoot(), apiResponse.getMessage());
                            }
                        } else {
                            ApiResponse<Void> errorResponse = parseErrorResponse(response);
                            if (errorResponse != null) {
                                UiUtils.showError(binding.getRoot(), errorResponse.getMessage());
                            } else {
                                UiUtils.showError(binding.getRoot(), getString(R.string.error_generic));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {

                        UiUtils.hideLoading(binding.progressBar, binding.btnVerify);
                        UiUtils.showError(binding.getRoot(), getString(R.string.error_network));
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }

    
    private ApiResponse<Void> parseErrorResponse(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                Gson gson = new Gson();
                return gson.fromJson(errorBody, ApiResponse.class);
            }
        } catch (IOException e) {
            Log.e("OtpVerification", "Error parsing error body: " + e.getMessage());
        } catch (Exception e) {
            Log.e("OtpVerification", "Exception parsing error response: " + e.getMessage());
        }
        return null;
    }
}
