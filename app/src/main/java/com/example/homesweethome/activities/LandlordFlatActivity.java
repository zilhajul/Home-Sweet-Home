package com.example.homesweethome.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homesweethome.R;
import com.example.homesweethome.api.AuthService;
import com.example.homesweethome.api.RetrofitClient;
import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.model.Building;
import com.example.homesweethome.model.Flat;
import com.example.homesweethome.model.Landlord;
import com.example.homesweethome.preferences.SessionManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LandlordFlatActivity extends AppCompatActivity {

    private EditText etFlatName, etFlatDetail, etFlatRent, etGasBill, etElectricityBill, etWaterBill, etServiceCharge;
    private Spinner spinnerFlatStatus, spinnerFloorNumber, etBuildingId;
    private Button btnSelectFlatImage, btnSelectFlatImages, btnSubmitFlat;
    private ImageView ivFlatImagePreview;
    private ImageView ivBack;
    private String buildingId;
    private ProgressBar progressBar;

    private RecyclerView rvFlatImagesPreview;
    private ImageAdapter imageAdapter;

    private Uri flatImageUri;
    private String landlordId;
    private List<Uri> flatImagesUris = new ArrayList<>();

    private AuthService authService;
    private SessionManager sessionManager;
    List<Building> buildings = new ArrayList<>();
    List<String> buildingNames = new ArrayList<>();

    private ActivityResultLauncher<Intent> singleImageLauncher;
    private ActivityResultLauncher<Intent> multipleImagesLauncher;

    private int pendingAction = 0; // 1 for single, 2 for multiple
    private Uri seletctedFlatImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_landlord_flat);

        initViews();
        initAuthService();
        getBuildings();
        setupImageLaunchers();
        setupListeners();
    }

    private void initViews() {
        etBuildingId = findViewById(R.id.et_building_id);
        etFlatName = findViewById(R.id.et_flat_name);
        etFlatDetail = findViewById(R.id.et_flat_detail);
        etFlatRent = findViewById(R.id.et_flat_rent);
        etGasBill = findViewById(R.id.et_gas_bill);
        etElectricityBill = findViewById(R.id.et_electricity_bill);
        etWaterBill = findViewById(R.id.et_water_bill);
        etServiceCharge = findViewById(R.id.et_service_charge);
        spinnerFlatStatus = findViewById(R.id.spinner_flat_status);
        spinnerFloorNumber = findViewById(R.id.spinner_floor_number);
        btnSelectFlatImage = findViewById(R.id.btn_select_flat_image);
        btnSelectFlatImages = findViewById(R.id.btn_select_flat_images);
        btnSubmitFlat = findViewById(R.id.btn_submit_flat);
        ivFlatImagePreview = findViewById(R.id.iv_flat_image_preview);
        ivBack = findViewById(R.id.ivBack);
        progressBar = findViewById(R.id.progressBar);

        rvFlatImagesPreview = findViewById(R.id.rv_flat_images_preview);
        rvFlatImagesPreview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new ImageAdapter(flatImagesUris);
        rvFlatImagesPreview.setAdapter(imageAdapter);
    }

    private void initAuthService() {
        authService = RetrofitClient.getInstance(this).getAuthService();
    }

    private void setupSpinners() {
        buildingNames.clear();

        for (Building b : buildings) {
            buildingNames.add(b.getBuildingName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                buildingNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etBuildingId.setAdapter(adapter);

        etBuildingId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                buildingId = buildings.get(position).getId();
                Log.d("Spinner", "Selected ID: " + buildingId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void getBuildings() {
        sessionManager = new SessionManager(this);
        landlordId = sessionManager.getLandlord().getId();

        Context context = getApplicationContext();
        if (context == null) return;

        RetrofitClient client = RetrofitClient.getInstance(context);
        client.setSessionManager(sessionManager);

        client.getBuildingService()
                .getBuildingsByLandlord(landlordId)
                .enqueue(new Callback<ApiResponse<List<Building>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Building>>> call,
                                           Response<ApiResponse<List<Building>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<List<Building>> apiResponse = response.body();

                            if (apiResponse.isSuccess()) {
                                buildings.addAll(apiResponse.getData());
                                if (buildings != null && !buildings.isEmpty()) {
                                    setupSpinners();
                                } else {
                                    Toast.makeText(context, "No Building Added", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d("LandlordFlatActivity", "message: " + apiResponse.getMessage());
                            }
                        } else {
                            Log.d("LandlordFlatActivity", "message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Building>>> call, Throwable t) {
                        Log.e("LandlordFlatActivity", "getBuildings failed: " + t.getMessage());
                    }
                });
    }

    private void setupImageLaunchers() {
        singleImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        flatImageUri = result.getData().getData();
                       // ivFlatImagePreview.setImageURI(flatImageUri);
                         seletctedFlatImageUri = flatImageUri;

                         if (flatImageUri != null) {
                             ivFlatImagePreview.setVisibility(View.VISIBLE);
                             ivFlatImagePreview.setImageURI(flatImageUri);

                         }
                    }
                });

        multipleImagesLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        if (result.getData().getClipData() != null) {
                            int count = result.getData().getClipData().getItemCount();
                            for (int i = 0; i < count; i++) {
                                Uri uri = result.getData().getClipData().getItemAt(i).getUri();
                                flatImagesUris.add(uri);
                            }
                        } else if (result.getData().getData() != null) {
                            flatImagesUris.add(result.getData().getData());
                        }
                        if (!flatImagesUris.isEmpty()) {
                            rvFlatImagesPreview.setVisibility(View.VISIBLE);
                        }
                        imageAdapter.notifyDataSetChanged();
                        Toast.makeText(this, "Selected " + flatImagesUris.size() + " images", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        btnSelectFlatImage.setOnClickListener(v -> selectSingleImage());
        btnSelectFlatImages.setOnClickListener(v -> selectMultipleImages());
        btnSubmitFlat.setOnClickListener(v -> submitFlat());
    }

    private void selectSingleImage() {
        if (checkPermission()) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            singleImageLauncher.launch(intent);
        } else {
            pendingAction = 1;
            requestPermission();
        }
    }

    private void selectMultipleImages() {
        if (checkPermission()) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setType("image/*");
            multipleImagesLauncher.launch(intent);
        } else {
            pendingAction = 2;
            requestPermission();
        }
    }

    private String getRequiredPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            return Manifest.permission.READ_EXTERNAL_STORAGE;
        }
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, getRequiredPermission()) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{getRequiredPermission()}, 100);
    }

    private void submitFlat() {
        sessionManager = new SessionManager(this);
        Landlord landlord = sessionManager.getLandlord();
        if (landlord != null) {
            landlordId = landlord.getId();
            Log.d("LandlordAddBuilding", "Landlord ID: " + landlordId);
        }

        String landlord_id = landlordId;
        String flatName = etFlatName.getText().toString().trim();
        String flatStatus = spinnerFlatStatus.getSelectedItem().toString();
        String flatDetail = etFlatDetail.getText().toString().trim();
        String floorNumber = spinnerFloorNumber.getSelectedItem().toString();
        String flatRent = etFlatRent.getText().toString().trim();
        String gasBill = etGasBill.getText().toString().trim();
        String electricityBill = etElectricityBill.getText().toString().trim();
        String waterBill = etWaterBill.getText().toString().trim();
        String serviceCharge = etServiceCharge.getText().toString().trim();

        // Validate required fields
        if (buildingId == null || buildingId.isEmpty() || flatName.isEmpty() || flatRent.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cover image is mandatory
        if (flatImageUri == null) {
            Toast.makeText(this, "Please select a cover image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare text RequestBodies
        RequestBody landlordIdBody = RequestBody.create(MediaType.parse("text/plain"), landlord_id);
        RequestBody buildingIdBody = RequestBody.create(MediaType.parse("text/plain"), buildingId);
        RequestBody flatNameBody = RequestBody.create(MediaType.parse("text/plain"), flatName);
        RequestBody flatStatusBody = RequestBody.create(MediaType.parse("text/plain"), flatStatus);
        RequestBody flatDetailBody = RequestBody.create(MediaType.parse("text/plain"), flatDetail);
        RequestBody floorNumberBody = RequestBody.create(MediaType.parse("text/plain"), floorNumber);
        RequestBody flatRentBody = RequestBody.create(MediaType.parse("text/plain"), flatRent);
        RequestBody gasBillBody = RequestBody.create(MediaType.parse("text/plain"), gasBill);
        RequestBody electricityBillBody = RequestBody.create(MediaType.parse("text/plain"), electricityBill);
        RequestBody waterBillBody = RequestBody.create(MediaType.parse("text/plain"), waterBill);
        RequestBody serviceChargeBody = RequestBody.create(MediaType.parse("text/plain"), serviceCharge);


        MultipartBody.Part flatImagePart = prepareFilePart("flat_image", flatImageUri);

        List<MultipartBody.Part> flatImagesParts = new ArrayList<>();
        for (Uri uri : flatImagesUris) {
            flatImagesParts.add(prepareFilePart("flat_images", uri));
        }


        if (flatImagesParts.isEmpty()) {
            RequestBody emptyBody = RequestBody.create(MediaType.parse("image/jpeg"), new byte[0]);
            flatImagesParts.add(MultipartBody.Part.createFormData("flat_images", "", emptyBody));
        }

        // Show progress, disable button
        progressBar.setVisibility(View.VISIBLE);
        btnSubmitFlat.setEnabled(false);

        // API Call
        Call<ApiResponse<Flat>> call = authService.addFlat(
                landlordIdBody, buildingIdBody, flatNameBody, flatImagePart,
                flatStatusBody, flatImagesParts, flatDetailBody, floorNumberBody,
                flatRentBody, gasBillBody, electricityBillBody, waterBillBody, serviceChargeBody
        );

        call.enqueue(new Callback<ApiResponse<Flat>>() {
            @Override
            public void onResponse(Call<ApiResponse<Flat>> call, Response<ApiResponse<Flat>> response) {
                progressBar.setVisibility(View.GONE);
                btnSubmitFlat.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(LandlordFlatActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(LandlordFlatActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Flat>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSubmitFlat.setEnabled(true);
                Toast.makeText(LandlordFlatActivity.this, "Failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss_SSS", java.util.Locale.US).format(new java.util.Date());
        String extension = getFileExtension(fileUri);
        String fileName = "IMG_" + timestamp + "." + extension;

        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            if (inputStream == null) {
                android.util.Log.e("LandlordFlatActivity", "Error: InputStream is null");
                Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
                return null;
            }
            java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[4096];

            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
            byte[] fileBytes = buffer.toByteArray();
            inputStream.close();


            if (fileName == null || !fileName.contains(".")) {
                fileName = fileName + ".jpg";
            }

            android.util.Log.d("UPLOAD_DEBUG", "Filename: " + fileName);

            RequestBody requestFile = RequestBody.create(fileBytes, MediaType.parse("image/*"));
            return MultipartBody.Part.createFormData(partName, fileName, requestFile);
        } catch (Exception e) {
            android.util.Log.e("LandlordFlatActivity", "Error preparing file: " + e.getMessage());
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
            return null;
        }

    }

    private String getFileExtension(Uri uri) {
        String extension = "jpg"; // default
        if ("content".equals(uri.getScheme())) {
            String mimeType = getContentResolver().getType(uri);
            if (mimeType != null) {
                if (mimeType.contains("png")) {
                    extension = "png";
                } else if (mimeType.contains("webp")) {
                    extension = "webp";
                } else if (mimeType.contains("jpeg") || mimeType.contains("jpg")) {
                    extension = "jpg";
                }
            }
        }
        return extension;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (pendingAction == 1) {
                    selectSingleImage();
                } else if (pendingAction == 2) {
                    selectMultipleImages();
                }
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
            pendingAction = 0;
        }
    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
        private List<Uri> imageUris;

        public ImageAdapter(List<Uri> imageUris) {
            this.imageUris = imageUris;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(parent.getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(100, 100));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(4, 4, 4, 4);
            return new ViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.imageView.setImageURI(imageUris.get(position));
        }

        @Override
        public int getItemCount() {
            return imageUris.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public ViewHolder(ImageView imageView) {
                super(imageView);
                this.imageView = imageView;
            }
        }
    }
}
