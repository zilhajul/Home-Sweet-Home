package com.example.homesweethome.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homesweethome.R;
import com.example.homesweethome.api.RetrofitClient;
import com.example.homesweethome.model.ComplainCreateResponse;
import com.example.homesweethome.model.ComplainRequest;
import com.example.homesweethome.preferences.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComplainActivity extends AppCompatActivity {

    private static final String TAG = "ComplainActivity";

    public static final String EXTRA_TENANT_ID    = "extra_tenant_id";
    public static final String EXTRA_LANDLORD_ID  = "extra_landlord_id";
    public static final String EXTRA_BUILDING_ID  = "extra_building_id";
    public static final String EXTRA_FLAT_ID      = "extra_flat_id";
    public static final String EXTRA_TENANT_NAME  = "extra_tenant_name";
    public static final String EXTRA_FLAT_NAME    = "extra_flat_name";
    public static final String EXTRA_BUILDING_NAME = "extra_building_name";

    // ---- Views ----
    private TextView          tvTenantName;
    private TextView          tvFlatName;
    private TextView          tvBuildingName;
    private TextInputLayout   tilComplainText;
    private TextInputEditText etComplainText;
    private Button            btnSubmitComplain;
    private ProgressBar       progressBar;

    // ---- Data ----
    private String tenantId;
    private String landlordId;
    private String buildingId;
    private String flatId;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain);

        sessionManager = new SessionManager(this);

        initViews();
        loadIntentData();
        setupClickListeners();
    }

    // -------------------------------------------------------------------------
    // Init
    // -------------------------------------------------------------------------

    private void initViews() {
        tvTenantName      = findViewById(R.id.tvTenantName);
        tvFlatName        = findViewById(R.id.tvFlatName);
        tvBuildingName    = findViewById(R.id.tvBuildingName);
        tilComplainText   = findViewById(R.id.tilComplainText);
        etComplainText    = findViewById(R.id.etComplainText);
        btnSubmitComplain = findViewById(R.id.btnSubmitComplain);
        progressBar       = findViewById(R.id.progressBar);
    }

    private void loadIntentData() {
        tenantId    = getIntent().getStringExtra(EXTRA_TENANT_ID);
        landlordId  = getIntent().getStringExtra(EXTRA_LANDLORD_ID);
        buildingId  = getIntent().getStringExtra(EXTRA_BUILDING_ID);
        flatId      = getIntent().getStringExtra(EXTRA_FLAT_ID);

        String tenantName   = getIntent().getStringExtra(EXTRA_TENANT_NAME);
        String flatName     = getIntent().getStringExtra(EXTRA_FLAT_NAME);
        String buildingName = getIntent().getStringExtra(EXTRA_BUILDING_NAME);

        // "Submitting As" card populate করো
        if (!TextUtils.isEmpty(tenantName))   tvTenantName.setText(tenantName);
        if (!TextUtils.isEmpty(flatName))     tvFlatName.setText(flatName);
        if (!TextUtils.isEmpty(buildingName)) tvBuildingName.setText(buildingName);

        Log.d(TAG, "tenantId=" + tenantId + " | landlordId=" + landlordId
                + " | buildingId=" + buildingId + " | flatId=" + flatId);
    }

    // -------------------------------------------------------------------------
    // Click Listeners
    // -------------------------------------------------------------------------

    private void setupClickListeners() {
        // Back button
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());

        // Submit button
        btnSubmitComplain.setOnClickListener(v -> attemptSubmit());
    }

    // -------------------------------------------------------------------------
    // Validation & Submit
    // -------------------------------------------------------------------------

    private void attemptSubmit() {
        tilComplainText.setError(null);

        String complainText = etComplainText.getText() != null
                ? etComplainText.getText().toString().trim() : "";

        // Validation
        if (TextUtils.isEmpty(complainText)) {
            tilComplainText.setError("Please write your complaint");
            etComplainText.requestFocus();
            return;
        }

        if (complainText.length() < 20) {
            tilComplainText.setError("Minimum 20 characters required");
            etComplainText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(tenantId) || TextUtils.isEmpty(landlordId)) {
            Toast.makeText(this, "Missing tenant or landlord information", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "tenantId or landlordId is null/empty — check Intent extras");
            return;
        }

        submitComplain(complainText);
    }

    private void submitComplain(String complainText) {
        showLoading(true);

        ComplainRequest request = new ComplainRequest(
                tenantId,
                landlordId,
                complainText,
                buildingId,
                flatId
        );

        RetrofitClient.getInstance(this)
                .getAuthService()
                .createComplain(request)
                .enqueue(new Callback<ComplainCreateResponse>() {

                    @Override
                    public void onResponse(@NonNull Call<ComplainCreateResponse> call,
                                           @NonNull Response<ComplainCreateResponse> response) {
                        showLoading(false);

                        Log.d(TAG, "onResponse — code: " + response.code());

                        if (!response.isSuccessful() || response.body() == null) {
                            Log.e(TAG, "HTTP error or null body: " + response.code());
                            Toast.makeText(ComplainActivity.this,
                                    "Failed to submit. Please try again.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ComplainCreateResponse body = response.body();
                        Log.d(TAG, "success=" + body.isSuccess() + " | message=" + body.getMessage());

                        if (body.isSuccess()) {
                            Toast.makeText(ComplainActivity.this,
                                    "✓ Complaint submitted successfully!", Toast.LENGTH_LONG).show();
                            finish(); // Dashboard এ ফিরে যাও
                        } else {
                            Toast.makeText(ComplainActivity.this,
                                    body.getMessage() != null
                                            ? body.getMessage()
                                            : "Something went wrong. Please try again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ComplainCreateResponse> call,
                                          @NonNull Throwable t) {
                        showLoading(false);
                        Log.e(TAG, "API failure: " + t.getMessage(), t);
                        Toast.makeText(ComplainActivity.this,
                                "Network error. Please check your connection.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // -------------------------------------------------------------------------
    // UI Helpers
    // -------------------------------------------------------------------------

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnSubmitComplain.setEnabled(!isLoading);
        btnSubmitComplain.setText(isLoading ? "Submitting..." : "Submit");
    }
}