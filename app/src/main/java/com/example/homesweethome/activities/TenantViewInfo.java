package com.example.homesweethome.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.homesweethome.R;
import com.example.homesweethome.activities.adapter.RentAdapter;
import com.example.homesweethome.api.FlatService;
import com.example.homesweethome.api.RetrofitClient;
import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.model.Flat;
import com.example.homesweethome.model.FlatResponse;
import com.example.homesweethome.model.Rent;
import com.example.homesweethome.model.Tenant;
import com.example.homesweethome.model.TenantResponse;
import com.example.homesweethome.preferences.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TenantViewInfo extends AppCompatActivity {

    private String flatId, buildingId, landlordId, tenantId;

    private ViewPager2 vpImages;
    private TextView tvFlatName, tvTenantName, tvTenantPhone;
    private RecyclerView rvList;
    private ProgressBar progressBar;
    private ImageView ivBack, ivTenant;

    private com.example.homesweethome.activities.ImageSliderAdapter imageAdapter;
    private RentAdapter rentAdapter;

    private List<String> imageList = new ArrayList<>();
    private List<Rent> rentList = new ArrayList<>();

    private FlatService flatService;
    private SessionManager sessionManager;

    private static final String TAG = "ViewInfo";
    private String From= "tenant";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_info);


        flatId = getIntent().getStringExtra("flatId");
        buildingId = getIntent().getStringExtra("buildingId");
        landlordId = getIntent().getStringExtra("landlordId");
        tenantId = getIntent().getStringExtra("tenantId");

        Log.d(TAG, "flatId: " + flatId + ", buildingId: " + buildingId + ", tenantId: " + tenantId);

        initViews();
        initServices();
        setupRecyclerView();
        setupViewPager();
        fetchFlatData();
        fetchRent();
        setupBackButton();
    }

    private void initViews() {
        vpImages = findViewById(R.id.vpImages);
        tvFlatName = findViewById(R.id.tvFlatName);
        tvTenantName = findViewById(R.id.tvTenantName);
        tvTenantPhone = findViewById(R.id.tvTenantPhone);
        rvList = findViewById(R.id.rvRentList);
        progressBar = findViewById(R.id.progressBar);
        ivBack = findViewById(R.id.ivBack);
        ivTenant = findViewById(R.id.imgTenant);
    }

    private void initServices() {
        flatService = RetrofitClient.getInstance(this).getFlatService();
        sessionManager = new SessionManager(this);
    }

    private void setupRecyclerView() {
        rentAdapter = new RentAdapter(rentList,this, From);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(rentAdapter);

    }

    private void setupViewPager() {
        imageAdapter = new com.example.homesweethome.activities.ImageSliderAdapter(imageList);
        vpImages.setAdapter(imageAdapter);
    }

    private void setupBackButton() {
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }
    }

    private void fetchFlatData() {
        if (buildingId == null || buildingId.isEmpty()) {
            Log.e(TAG, "buildingId is null or empty");
            Toast.makeText(this, "Building ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressBar(true);

        // Get landlord ID from session if not available
        if (landlordId == null || landlordId.isEmpty()) {
            landlordId = sessionManager.getLandlord().getId();
        }

        Log.d(TAG, "Fetching flats for landlordId: " + landlordId + ", buildingId: " + buildingId);

        flatService.getFlatsByBuilding(landlordId, buildingId).enqueue(new Callback<FlatResponse>() {
            @Override
            public void onResponse(Call<FlatResponse> call, Response<FlatResponse> response) {
                showProgressBar(false);

                if (response.isSuccessful() && response.body() != null) {
                    FlatResponse flatResponse = response.body();

                    Log.d(TAG, "API Response: " + flatResponse.isSuccess() + ", Total Flats: " + flatResponse.getTotalData());

                    if (flatResponse.isSuccess() && flatResponse.getData() != null && !flatResponse.getData().isEmpty()) {
                        // Get the flat by ID or get the first flat
                        Flat flat = findFlatById(flatResponse.getData(), flatId);

                        if (flat != null) {
                            displayFlatData(flat);
                            // Display tenant info directly from flat object
                            if (flat.getTenantId() != null) {
                                displayTenantInfo(flat.getTenantId());
                            } else {
                                Log.w(TAG, "Tenant info is null in flat object");
                                displayTenantInfo(null);
                            }
                        } else {
                            Log.e(TAG, "Flat with ID " + flatId + " not found");
                            Toast.makeText(TenantViewInfo.this, "Flat not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "No flats found in response");
                        Toast.makeText(TenantViewInfo.this, "No flats available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "API response failed: " + response.code());
                    Toast.makeText(TenantViewInfo.this, "Failed to fetch flat data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FlatResponse> call, Throwable t) {
                showProgressBar(false);
                Log.e(TAG, "API call failed: " + t.getMessage(), t);
                Toast.makeText(TenantViewInfo.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchRent() {
        RetrofitClient.getInstance(this).getAuthService().getRents(landlordId , tenantId, flatId, buildingId)
                .enqueue(new Callback<ApiResponse<List<Rent>>>() {


                    @Override
                    public void onResponse(Call<ApiResponse<List<Rent>>> call, Response<ApiResponse<List<Rent>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<List<Rent>> apiResponse = response.body();
                            List<Rent> rentLists = apiResponse.getData();
                            if (rentLists != null) {
                                rentList.clear();
                                rentList.addAll(rentLists);
                                rentAdapter.notifyDataSetChanged();
                                rvList.setVisibility(View.VISIBLE);

                                Log.d(TAG, "Rent list size: " + rentList.size());
                            }

                            Log.d(TAG, "Response body: " + response.body());
                            Log.d(TAG, "Rent list size: " + (response.body().getData() != null ? response.body().getData() : 0));
                        } else {
                            Log.e(TAG, "Failed to fetch rent data: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Rent>>> call, Throwable throwable) {

                    }
                });
    }


    private Flat findFlatById(List<Flat> flats, String flatId) {

        for (Flat flat : flats) {
            if (flat.getId() != null && flat.getId().equals(flatId)) {
                Log.d(TAG, "Found flat: " + flat.getFlatName() + " with tenant: " + (flat.getTenantId() != null ? flat.getTenantId().getTenantName() : "None"));
                return flat;
            }
        }

        Log.w(TAG, "Flat ID " + flatId + " not found, returning first flat");
        return !flats.isEmpty() ? flats.get(0) : null;
    }


    private void displayFlatData(Flat flat) {
        // Set flat name
        tvFlatName.setText(flat.getFlatName() != null ? flat.getFlatName() : "N/A");

        // Add images to the image list
        imageList.clear();
        if (flat.getFlatImages() != null && !flat.getFlatImages().isEmpty()) {
            imageList.addAll(flat.getFlatImages());
            imageList.add(flat.getFlatImage());
        } else if (flat.getFlatImage() != null && !flat.getFlatImage().isEmpty()) {
            imageList.add(flat.getFlatImage());
        }

        if (!imageList.isEmpty()) {
            imageAdapter.notifyDataSetChanged();
            Log.d(TAG, "Added " + imageList.size() + " images to slider");
        }

        // Add flat to list for rent display

        Log.d(TAG, "Flat data displayed: " + flat.getFlatName());
    }

    /**
     * Display tenant info in the UI
     */
    private void displayTenantInfo(Tenant tenant) {
        // Set tenant info (if available)
        if (tenant != null) {
            String tenantName = tenant.getTenantName() != null ? tenant.getTenantName() : "Not assigned";
            String tenantPhone = tenant.getTenantPhone() != null ? tenant.getTenantPhone() : "Not assigned";

            tvTenantName.setText("Name: " + tenantName);
            tvTenantPhone.setText("Phone: " + tenantPhone);

            Log.d(TAG, "Displaying tenant: " + tenantName + ", Phone: " + tenantPhone);

            // Load tenant image
            if (tenant.getTenantImage() != null && !tenant.getTenantImage().isEmpty()) {
                Glide.with(this)
                        .load(tenant.getTenantImage())
                        .placeholder(R.drawable.account)
                        .error(R.drawable.account)
                        .into(ivTenant);
            } else {
                ivTenant.setImageResource(R.drawable.account);
            }
        } else {
            tvTenantName.setText("Name: Not assigned");
            tvTenantPhone.setText("Phone: Not assigned");
            ivTenant.setImageResource(R.drawable.account);

            Log.w(TAG, "Tenant is null, showing default values");
        }
    }

    private void showProgressBar(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? android.view.View.VISIBLE : android.view.View.GONE);
        }
    }
}