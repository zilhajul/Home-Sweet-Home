package com.example.homesweethome.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerificationActivity extends AppCompatActivity {

    public static final String EXTRA_EMAIL = "extra_email";
    public static final String EXTRA_ROLE  = "extra_role";

    private static final long COUNTDOWN_MS   = 60_000L;
    private static final long COUNTDOWN_TICK = 1_000L;

    private ActivityOtpVerificationBinding binding;
    private String number;
    private String role;
    private CountDownTimer countDownTimer;

    private EditText[] otpBoxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        number = getIntent().getStringExtra(EXTRA_EMAIL);
        role  = getIntent().getStringExtra(EXTRA_ROLE);

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

    // ── OTP boxes: auto-advance and backspace handling ─────────────────────
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
            sb.append(box.getText().toString().trim());
        }
        return sb.toString();
    }

    private void startCountdown() {
        binding.tvTimer.setVisibility(View.VISIBLE);
        binding.llResend.setVisibility(View.GONE);

        countDownTimer = new CountDownTimer(COUNTDOWN_MS, COUNTDOWN_TICK) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secs = millisUntilFinished / 1000;
                binding.tvTimer.setText(getString(R.string.loading) + " " + secs + "s");
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
        body.put("number", number);
        body.put("role", role);

        RetrofitClient.getInstance().getAuthService().forgotPassword(body)
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
            UiUtils.showError(binding.getRoot(), getString(R.string.error_invalid_otp));
            return;
        }

        if (!NetworkUtils.isConnected(this)) {
            UiUtils.showError(binding.getRoot(), getString(R.string.error_network));
            return;
        }

        UiUtils.showLoading(binding.progressBar, binding.btnVerify);

        Map<String, String> body = new HashMap<>();
        body.put("landlord_phone", number);
        body.put("forgot_password_otp", otp);

        RetrofitClient.getInstance().getAuthService().verifyOtp(body)
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call,
                                           Response<ApiResponse<Void>> response) {
                        UiUtils.hideLoading(binding.progressBar, binding.btnVerify);

                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            Intent intent = new Intent(OtpVerificationActivity.this,
                                    ResetPasswordActivity.class);
                            intent.putExtra(ResetPasswordActivity.EXTRA_EMAIL, number);
                            intent.putExtra(ResetPasswordActivity.EXTRA_OTP, otp);
                            startActivity(intent);
                        } else {
                            String msg = response.body() != null
                                    ? response.body().getMessage() : getString(R.string.error_generic);
                            UiUtils.showError(binding.getRoot(), msg);
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
}
