package com.example.homesweethome.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homesweethome.R;
import com.example.homesweethome.api.RetrofitClient;
import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.model.Tenant;
import com.example.homesweethome.model.TenantFlatResponse;
import com.example.homesweethome.preferences.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TenantDashboardActivity extends AppCompatActivity {

    private static final String TAG = "TenantDashboard";

    // ---- Session ----
    private SessionManager sessionManager;

    // ---- Loading ----
    private ProgressBar progressBar;
    private ScrollView  scrollView;
    private ImageView ivNotification;

    // ---- Header ----
    private TextView tvTenantName;

    // ---- Flat Info Card ----
    private TextView tvFlatNumber;
    private TextView tvBuildingName;
    private TextView tvFlatStatus;
    private TextView tvMonthlyRent;
    private TextView tvFloor;
    private TextView tvRentSince;

    // ---- Bills & Charges ----
    private TextView tvUtilityBill;   // electricity_bill
    private TextView tvGasBill;
    private TextView tvWaterBill;
    private TextView tvServiceCharge;

    // ---- Buttons ----
    private Button btnComplain;

    // ---- Tenant & Flat data (keep for ComplainActivity) ----
    private String tenantId;
    Tenant tenant;
    private String landlordId;
    private String buildingId;
    private String flatId;
    private String tenantName;
    private String flatName;
    private String buildingName;

    // ---- API call tracking (call both APIs, show UI when both done) ----
    private boolean tenantLoaded = false;
    private boolean flatLoaded   = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_dashboard);


        sessionManager = new SessionManager(this);
        tenant = sessionManager.getTenant();
        tenantId = tenant.getId();
        tenantName = tenant.getTenantName();
        initViews();
//        showLoading(true);
        fetchTenantFlat(tenantId);
//        fetchTenantInfo();
    }

    // =========================================================================
    // Init
    // =========================================================================

    private void initViews() {
        // Loading overlay (programmatically added so no XML change needed)
        scrollView  = findViewById(R.id.scrollViewDashboard); // see note below *
        progressBar = createFullScreenProgressBar();

        // Header
        tvTenantName   = findViewById(R.id.tvTenantName);
        ivNotification = findViewById(R.id.ivNotification);

        // Flat card
        tvFlatNumber   = findViewById(R.id.tvFlatNumber);
        tvBuildingName = findViewById(R.id.tvBuildingName);
        tvFlatStatus   = findViewById(R.id.tvFlatStatus);
        tvMonthlyRent  = findViewById(R.id.tvMonthlyRent);
        tvFloor        = findViewById(R.id.tvFloor);
        tvRentSince    = findViewById(R.id.tvRentSince);

        // Bills
        tvUtilityBill  = findViewById(R.id.tvUtilityBill);
        tvGasBill      = findViewById(R.id.tvGasBill);
        tvWaterBill    = findViewById(R.id.tvWaterBill);
        tvServiceCharge = findViewById(R.id.tvServiceCharge);

        // Buttons
        btnComplain    = findViewById(R.id.btnComplain);
        btnComplain.setOnClickListener(v -> openComplainActivity());

        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(this, TenantNotification.class);
            intent.putExtra("tenantId",     tenantId);
            startActivity(intent);
        });

        // Logout
        findViewById(R.id.ivLogout).setOnClickListener(v -> logout());

        tvTenantName.setText(tenantName);

    }

    /**
     * Creates a centered ProgressBar and adds it as an overlay on top of the
     * root FrameLayout so we don't need to touch the XML.
     *
     * ⚠️ NOTE: Your root layout is a ScrollView. Wrap it in a FrameLayout in XML
     * and give the FrameLayout id="@+id/rootFrame", OR just give the ScrollView
     * id="@+id/scrollViewDashboard" and use the approach below.
     *
     * If you don't want to touch the XML at all, we hide/show the ScrollView
     * and show/hide a ProgressBar added to the window's DecorView.
     */
    private ProgressBar createFullScreenProgressBar() {
        ProgressBar pb = new ProgressBar(this);
        pb.setVisibility(View.GONE);

        // Add to DecorView so it appears over everything
        FrameLayout decorView = (FrameLayout) getWindow().getDecorView();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
        );
        decorView.addView(pb, params);
        return pb;
    }

    // =========================================================================
    // API: GET /tenants  →  Tenant info
    // =========================================================================

//    private void fetchTenantInfo() {
//
//
//
//        RetrofitClient.getInstance(this)
//                .getAuthService()
//                .getTenant()
//                .enqueue(new Callback<ApiResponse<Tenant>>() {
//
//                    @Override
//                    public void onResponse(@NonNull Call<ApiResponse<Tenant>> call,
//                                           @NonNull Response<ApiResponse<Tenant>> response) {
//
//                        if (!response.isSuccessful() || response.body() == null) {
//                            Log.e(TAG, "getTenant failed: " + response.code());
//                            onApiError("Failed to load tenant info.");
//                            return;
//                        }
//
//                        ApiResponse<Tenant> apiResp = response.body();
//                        if (!apiResp.isSuccess() || apiResp.getData() == null) {
//                            Log.e(TAG, "getTenant — success=false or data null");
//                            onApiError(apiResp.getMessage());
//                            return;
//                        }
//
//                        Tenant tenant = apiResp.getData();
//                        tenantId   = tenant.getId();
//                        tenantName = tenant.getTenantName();
//
//                        Log.d(TAG, "Tenant loaded: " + tenantName + " | id=" + tenantId);
//
//                        // Set tenant name in header immediately
//                        tvTenantName.setText(tenantName);
//
//                        tenantLoaded = true;
//                        checkBothLoaded();
//
//                        // Now fetch flat info using tenant id
//                        fetchTenantFlat(tenantId);
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<ApiResponse<Tenant>> call,
//                                         @NonNull Throwable t) {
//                        Log.e(TAG, "getTenant network error: " + t.getMessage(), t);
//                        onApiError("Network error. Please check your connection.");
//                    }
//                });
//    }

    // =========================================================================
    // API: GET /flats/tenant?tenant_id=...  →  Flat + Bill info
    // =========================================================================

    private void fetchTenantFlat(String tId) {
        RetrofitClient.getInstance(this)
                .getAuthService()
                .getTenantFlats(tId)
                .enqueue(new Callback<TenantFlatResponse>() {

                    @Override
                    public void onResponse(@NonNull Call<TenantFlatResponse> call,
                                           @NonNull Response<TenantFlatResponse> response) {

                        if (!response.isSuccessful() || response.body() == null) {
                            Log.e(TAG, "getTenantFlats failed: " + response.code());
                            onApiError("Failed to load flat info.");
                            return;
                        }

                        TenantFlatResponse resp = response.body();
                        List<TenantFlatResponse.FlatData> flatList = resp.getData();

                        if (flatList == null || flatList.isEmpty()) {
                            Log.w(TAG, "No flat assigned to this tenant.");
                            flatLoaded = true;
                            checkBothLoaded();
                            return;
                        }

                        // Take the first flat (tenant usually has 1 flat)
                        TenantFlatResponse.FlatData flat = flatList.get(0);

                        // Save IDs for ComplainActivity
                        flatId     = flat.getId();
                        buildingId = flat.getBuildingId() != null ? flat.getBuildingId().getId() : "";
                        landlordId = flat.getLandlordId() != null ? flat.getLandlordId().getId() : "";
                        flatName   = flat.getFlatName();
                        buildingName = flat.getBuildingId() != null
                                ? flat.getBuildingId().getBuildingName() : "";

                        Log.d(TAG, "Flat loaded: " + flatName + " | landlordId=" + landlordId);

                        populateFlatUI(flat);

                        flatLoaded = true;
                        checkBothLoaded();
                    }

                    @Override
                    public void onFailure(@NonNull Call<TenantFlatResponse> call,
                                         @NonNull Throwable t) {
                        Log.e(TAG, "getTenantFlats network error: " + t.getMessage(), t);
                        onApiError("Network error. Please check your connection.");
                    }
                });
    }

    // =========================================================================
    // Populate UI
    // =========================================================================

    private void populateFlatUI(TenantFlatResponse.FlatData flat) {
        // Flat Info Card
        tvFlatNumber.setText(flat.getFlatName());

        if (flat.getBuildingId() != null) {
            String address = flat.getBuildingId().getBuildingName()
                    + ", " + flat.getBuildingId().getBuildingAddress();
            tvBuildingName.setText(address);
        }

        // Status badge
        String status = flat.getFlatStatus();
        tvFlatStatus.setText(status != null
                ? status.substring(0, 1).toUpperCase() + status.substring(1)
                : "Active");

        // Floor
        tvFloor.setText(flat.getFloorNumber() + (flat.getFloorNumber() == 1
                ? "st" : flat.getFloorNumber() == 2 ? "nd"
                : flat.getFloorNumber() == 3 ? "rd" : "th") + " Floor");

        // Rent since (from createdAt — e.g. "2026-04-25T12:08:33.409Z" → "Apr 2026")
        tvRentSince.setText(formatMonthYear(flat.getCreatedAt()));

        // Monthly Rent
        tvMonthlyRent.setText("৳ " + formatAmount(flat.getFlatRent()));

        // Bills
        tvUtilityBill.setText("৳ " + formatAmount(flat.getElectricityBill()));
        tvGasBill.setText("৳ " + formatAmount(flat.getGasBill()));
        tvWaterBill.setText("৳ " + formatAmount(flat.getWaterBill()));
        tvServiceCharge.setText("৳ " + formatAmount(flat.getServiceCharge()));
    }

    // =========================================================================
    // Navigation
    // =========================================================================

    private void openComplainActivity() {
        if (tenantId == null || landlordId == null) {
            Toast.makeText(this, "Please wait, data is still loading...", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, ComplainActivity.class);
        intent.putExtra(ComplainActivity.EXTRA_TENANT_ID,     tenantId);
        intent.putExtra(ComplainActivity.EXTRA_LANDLORD_ID,   landlordId);
        intent.putExtra(ComplainActivity.EXTRA_BUILDING_ID,   buildingId);
        intent.putExtra(ComplainActivity.EXTRA_FLAT_ID,       flatId);
        intent.putExtra(ComplainActivity.EXTRA_TENANT_NAME,   tenantName);
        intent.putExtra(ComplainActivity.EXTRA_FLAT_NAME,     flatName);
        intent.putExtra(ComplainActivity.EXTRA_BUILDING_NAME, buildingName);
        startActivity(intent);
    }

    private void logout() {
        sessionManager.clearSession();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // =========================================================================
    // Loading State
    // =========================================================================

    private void showLoading(boolean loading) {
        if (progressBar != null) progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (scrollView  != null) scrollView.setVisibility(loading ? View.INVISIBLE : View.VISIBLE);
    }

    private void checkBothLoaded() {
        // Hide loading only after BOTH APIs respond
        if (tenantLoaded && flatLoaded) {
//            runOnUiThread(() -> showLoading(false));
        }
    }

    private void onApiError(String message) {
        runOnUiThread(() -> {
//            showLoading(false);
            Toast.makeText(this,
                    message != null ? message : "Something went wrong.",
                    Toast.LENGTH_LONG).show();
        });
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    /** Format double to comma-separated string, e.g. 5000.0 → "5,000" */
    private String formatAmount(double amount) {
        long val = (long) amount;
        return String.format("%,d", val);
    }

    /** Parse ISO date string to "MMM yyyy", e.g. "2026-04-25T12:08:33.409Z" → "Apr 2026" */
    private String formatMonthYear(String isoDate) {
        if (isoDate == null || isoDate.length() < 7) return "";
        try {
            String[] months = {"Jan","Feb","Mar","Apr","May","Jun",
                               "Jul","Aug","Sep","Oct","Nov","Dec"};
            int month = Integer.parseInt(isoDate.substring(5, 7)) - 1;
            String year  = isoDate.substring(0, 4);
            return months[month] + " " + year;
        } catch (Exception e) {
            return "";
        }
    }
}
