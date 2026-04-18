package com.example.homesweethome.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homesweethome.api.RetrofitClient;
import com.example.homesweethome.databinding.LandlordAddBuildingBinding;
import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.model.Building;
import com.example.homesweethome.model.Landlord;
import com.example.homesweethome.preferences.SessionManager;
import com.example.homesweethome.model.User;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.InputStream;

public class LandlordAddBuildingActivity extends AppCompatActivity {

    private LandlordAddBuildingBinding binding;
    
    private Uri selectedBuildingImageUri = null;
    private final java.util.List<Uri> additionalImageUris = new java.util.ArrayList<>();
    private SessionManager sessionManager;
    private String landlordId;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_ADDITIONAL_IMAGES_REQUEST = 2;

    // Activity result launchers
    private ActivityResultLauncher<Intent> buildingImageLauncher;
    private ActivityResultLauncher<Intent> additionalImagesLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LandlordAddBuildingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize activity result launchers
        initializeActivityResultLaunchers();

        // Initialize SessionManager and get landlord_id
        sessionManager = new SessionManager(this);
        Landlord landlord = sessionManager.getLandlord();
        if (landlord != null) {
            landlordId = landlord.getId();
            android.util.Log.d("LandlordAddBuilding", "Landlord ID: " + landlordId);
        } else {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            getOnBackPressedDispatcher().onBackPressed();
            return;
        }

        // Setup Spinners
        setupSpinners();

        // Setup Click Listeners
        setupClickListeners();
    }

    private void initializeActivityResultLaunchers() {
        // Building image launcher
        buildingImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedUri = result.getData().getData();
                        selectedBuildingImageUri = selectedUri;
                        if (selectedUri != null) {
                            String fileName = getFileName(selectedUri);
                            binding.tvBuildingImageName.setText(fileName != null ? fileName : "Image Selected");
                        }
                    }
                });

        // Additional images launcher
        additionalImagesLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedUri = result.getData().getData();
                        if (selectedUri != null) {
                            addAdditionalImageToLayout(selectedUri);
                        }
                    }
                });
    }


    private void setupSpinners() {
        // Building Status Spinner
        String[] statusArray = {"Active", "Inactive", "Under Maintenance"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, statusArray);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerBuildingStatus.setAdapter(statusAdapter);

        // Total Floors Spinner
        String[] floorsArray = new String[10];
        for (int i = 0; i < 10; i++) {
            floorsArray[i] = String.valueOf(i + 1);
        }
        ArrayAdapter<String> floorsAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, floorsArray);
        floorsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTotalFloors.setAdapter(floorsAdapter);

        // Total Flats Spinner
        String[] flatsArray = new String[20];
        for (int i = 0; i < 20; i++) {
            flatsArray[i] = String.valueOf(i + 1);
        }
        ArrayAdapter<String> flatsAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, flatsArray);
        flatsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTotalFlats.setAdapter(flatsAdapter);
    }

    private void setupClickListeners() {
        binding.ivBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        binding.btnBuildingImagePicker.setOnClickListener(v -> pickImage(PICK_IMAGE_REQUEST));

        binding.btnAddMoreImages.setOnClickListener(v -> pickImage(PICK_ADDITIONAL_IMAGES_REQUEST));

        binding.btnSubmit.setOnClickListener(v -> submitBuildingData());

        binding.btnCancel.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void pickImage(int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        
        if (requestCode == PICK_IMAGE_REQUEST) {
            buildingImageLauncher.launch(Intent.createChooser(intent, "Select Image"));
        } else if (requestCode == PICK_ADDITIONAL_IMAGES_REQUEST) {
            additionalImagesLauncher.launch(Intent.createChooser(intent, "Select Image"));
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri != null && uri.getScheme() != null && uri.getScheme().equals("content")) {
            // For content URIs, get the display name
            result = uri.getLastPathSegment();
        }
        if (result == null && uri != null) {
            String path = uri.getPath();
            if (path != null) {
                int cut = path.lastIndexOf('/');
                if (cut != -1) {
                    result = path.substring(cut + 1);
                } else {
                    result = path;
                }
            }
        }
        return result;
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            if (inputStream == null) {
                android.util.Log.e("LandlordAddBuilding", "Error: InputStream is null");
                Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
                return null;
            }
            byte[] fileBytes = new byte[inputStream.available()];
            int bytesRead = inputStream.read(fileBytes);
            inputStream.close();

            if (bytesRead == 0) {
                android.util.Log.e("LandlordAddBuilding", "Error: No bytes read from file");
                Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
                return null;
            }

            String fileName = getFileName(fileUri);
            RequestBody requestFile = RequestBody.create(fileBytes, MediaType.parse("image/*"));
            return MultipartBody.Part.createFormData(partName, fileName, requestFile);
        } catch (Exception e) {
            android.util.Log.e("LandlordAddBuilding", "Error preparing file: " + e.getMessage());
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void addAdditionalImageToLayout(Uri imageUri) {
        // Store URI in list
        additionalImageUris.add(imageUri);
        
        // Create an ImageView for the additional image
        android.widget.ImageView imageView = new android.widget.ImageView(this);
        android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(100, 100);
        params.setMargins(8, 0, 8, 0);
        imageView.setLayoutParams(params);
        imageView.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
        imageView.setTag(imageUri); // Store URI as tag for reference
        
        try {
            imageView.setImageURI(imageUri);
        } catch (Exception e) {
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
        }

        // Add click listener to remove image
        imageView.setOnClickListener(v -> {
            additionalImageUris.remove(imageUri);
            binding.llAdditionalImages.removeView(imageView);
            Toast.makeText(this, "Image removed", Toast.LENGTH_SHORT).show();
        });

        binding.llAdditionalImages.addView(imageView);
        Toast.makeText(this, "Image added (click to remove)", Toast.LENGTH_SHORT).show();
    }

    private void submitBuildingData() {
        // Validate inputs
        String buildingName = binding.etBuildingName.getText().toString().trim();
        String buildingAddress = binding.etBuildingAddress.getText().toString().trim();
        String zoneName = binding.etZoneName.getText().toString().trim();
        String subZoneName = binding.etSubZoneName.getText().toString().trim();
        String buildingDetails = binding.etBuildingDetails.getText().toString().trim();
        String buildingStatus = binding.spinnerBuildingStatus.getSelectedItem().toString();
        String totalFloors = binding.spinnerTotalFloors.getSelectedItem().toString();
        String totalFlats = binding.spinnerTotalFlats.getSelectedItem().toString();

        if (buildingName.isEmpty()) {
            Toast.makeText(this, "Please enter building name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (buildingAddress.isEmpty()) {
            Toast.makeText(this, "Please enter building address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (zoneName.isEmpty()) {
            Toast.makeText(this, "Please enter zone name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (subZoneName.isEmpty()) {
            Toast.makeText(this, "Please enter sub zone name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedBuildingImageUri == null) {
            Toast.makeText(this, "Please select building image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading toast
        Toast.makeText(this, "Submitting building data...", Toast.LENGTH_SHORT).show();
        
        // Log all data
        android.util.Log.d("LandlordAddBuilding", "Landlord ID: " + landlordId);
        android.util.Log.d("LandlordAddBuilding", "Building Name: " + buildingName);
        android.util.Log.d("LandlordAddBuilding", "Building Address: " + buildingAddress);
        android.util.Log.d("LandlordAddBuilding", "Building Status: " + buildingStatus);
        android.util.Log.d("LandlordAddBuilding", "Zone Name: " + zoneName);
        android.util.Log.d("LandlordAddBuilding", "Sub Zone Name: " + subZoneName);
        android.util.Log.d("LandlordAddBuilding", "Total Floors: " + totalFloors);
        android.util.Log.d("LandlordAddBuilding", "Total Flats: " + totalFlats);
        android.util.Log.d("LandlordAddBuilding", "Building Details: " + buildingDetails);
        android.util.Log.d("LandlordAddBuilding", "Building Image URI: " + selectedBuildingImageUri.toString());
        android.util.Log.d("LandlordAddBuilding", "Additional Images Count: " + binding.llAdditionalImages.getChildCount());
        
        // Prepare building image
        MultipartBody.Part buildingImagePart = prepareFilePart("building_image", selectedBuildingImageUri);
        if (buildingImagePart == null) {
            Toast.makeText(this, "Error processing building image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare additional images
        java.util.List<MultipartBody.Part> additionalImageParts = new java.util.ArrayList<>();
        for (Uri imageUri : additionalImageUris) {
            MultipartBody.Part imagePart = prepareFilePart("additional_images", imageUri);
            if (imagePart != null) {
                additionalImageParts.add(imagePart);
            }
        }

        // Call API to submit building
        submitBuildingToServer(landlordId, buildingName, buildingAddress, buildingStatus,
                zoneName, subZoneName, totalFloors, totalFlats, buildingDetails,
                buildingImagePart, additionalImageParts);

        finish();
    }

    private void submitBuildingToServer(String landlordId, String buildingName, String buildingAddress,
                                        String buildingStatus, String zoneName, String subZoneName,
                                        String totalFloors, String totalFlats, String buildingDetails,
                                        MultipartBody.Part buildingImage, java.util.List<MultipartBody.Part> additionalImages) {
        
        RetrofitClient.getInstance(this).setSessionManager(sessionManager);
        RetrofitClient.getInstance(this).getBuildingService().addBuilding(
                landlordId, buildingName, buildingAddress, buildingStatus, zoneName, subZoneName,
                totalFloors, totalFlats, buildingDetails, buildingImage, additionalImages)
                .enqueue(new Callback<ApiResponse<Building>>() {
                    @Override
                    public void onResponse(@org.jetbrains.annotations.NotNull Call<ApiResponse<Building>> call, @org.jetbrains.annotations.NotNull Response<ApiResponse<Building>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            android.util.Log.d("LandlordAddBuilding", "Building added successfully: " + response.body().getData().getId());
                            Toast.makeText(LandlordAddBuildingActivity.this, "Building added successfully!", Toast.LENGTH_SHORT).show();
                            getOnBackPressedDispatcher().onBackPressed();
                        } else {
                            android.util.Log.e("LandlordAddBuilding", "Error: " + (response.body() != null ? response.body().getMessage() : "Unknown error"));
                            Toast.makeText(LandlordAddBuildingActivity.this, "Failed to add building", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@org.jetbrains.annotations.NotNull Call<ApiResponse<Building>> call, @org.jetbrains.annotations.NotNull Throwable t) {
                        android.util.Log.e("LandlordAddBuilding", "API Error: " + t.getMessage());
                        Toast.makeText(LandlordAddBuildingActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

