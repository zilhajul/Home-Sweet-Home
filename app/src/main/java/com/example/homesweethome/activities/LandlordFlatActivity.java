package com.example.homesweethome.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homesweethome.R;
import com.example.homesweethome.api.AuthService;
import com.example.homesweethome.api.RetrofitClient;
import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.model.Flat;
import com.example.homesweethome.model.Landlord;
import com.example.homesweethome.preferences.SessionManager;

import java.io.File;
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

    private EditText etBuildingId, etFlatName, etFlatDetail, etFlatRent, etGasBill, etElectricityBill, etWaterBill, etServiceCharge;
    private Spinner spinnerFlatStatus, spinnerFloorNumber;
    private Button btnSelectFlatImage, btnSelectFlatImages, btnSubmitFlat;
    private ImageView ivFlatImagePreview;
    private ImageView ivBack;

    private RecyclerView rvFlatImagesPreview;
    private ImageAdapter imageAdapter;

    private Uri flatImageUri;
    private String landlordId;
    private List<Uri> flatImagesUris = new ArrayList<>();

    private AuthService authService;
    private SessionManager sessionManager;

    private ActivityResultLauncher<Intent> singleImageLauncher;
    private ActivityResultLauncher<Intent> multipleImagesLauncher;

    private int pendingAction = 0; // 1 for single, 2 for multiple

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_landlord_flat);

        initViews();
        initAuthService();
        setupSpinners();
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

        rvFlatImagesPreview = findViewById(R.id.rv_flat_images_preview);
        rvFlatImagesPreview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new ImageAdapter(flatImagesUris);
        rvFlatImagesPreview.setAdapter(imageAdapter);
    }

    private void initAuthService() {
        authService = RetrofitClient.getInstance(this).getAuthService();
    }

    private void setupSpinners() {
        // Spinners are already set in XML with entries
    }

    private void setupImageLaunchers() {
        singleImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        flatImageUri = result.getData().getData();
                        ivFlatImagePreview.setImageURI(flatImageUri);
                        ivFlatImagePreview.setVisibility(ImageView.VISIBLE);
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
            android.util.Log.d("LandlordAddBuilding", "Landlord ID: " + landlordId);
        }
        // Collect data
        String landlord_id = landlordId; // Get from prefs or intent
        String buildingId = etBuildingId.getText().toString().trim();
        String flatName = etFlatName.getText().toString().trim();
        String flatStatus = spinnerFlatStatus.getSelectedItem().toString();
        String flatDetail = etFlatDetail.getText().toString().trim();
        String floorNumber = spinnerFloorNumber.getSelectedItem().toString();
        String flatRent = etFlatRent.getText().toString().trim();
        String gasBill = etGasBill.getText().toString().trim();
        String electricityBill = etElectricityBill.getText().toString().trim();
        String waterBill = etWaterBill.getText().toString().trim();
        String serviceCharge = etServiceCharge.getText().toString().trim();

        // Validate
        if (buildingId.isEmpty() || flatName.isEmpty() || flatRent.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare RequestBody
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

        // Flat Image
        MultipartBody.Part flatImagePart = null;
        if (flatImageUri != null) {
            flatImagePart = prepareFilePart("flat_image", flatImageUri);
        }

        // Flat Images
        List<MultipartBody.Part> flatImagesParts = new ArrayList<>();
        for (Uri uri : flatImagesUris) {
            flatImagesParts.add(prepareFilePart("flat_images", uri));
        }

        // Call API
        Call<ApiResponse<Flat>> call = authService.addFlat(landlordIdBody, buildingIdBody, flatNameBody, flatImagePart, flatStatusBody, flatImagesParts, flatDetailBody, floorNumberBody, flatRentBody, gasBillBody, electricityBillBody, waterBillBody, serviceChargeBody);
        call.enqueue(new Callback<ApiResponse<Flat>>() {
            @Override
            public void onResponse(Call<ApiResponse<Flat>> call, Response<ApiResponse<Flat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(LandlordFlatActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                    // Handle success
                } else {
                    Toast.makeText(LandlordFlatActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Flat>> call, Throwable t) {
                Toast.makeText(LandlordFlatActivity.this, "Failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        File file = new File(getCacheDir(), "temp_image");
        try (InputStream inputStream = getContentResolver().openInputStream(fileUri);
             FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
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
