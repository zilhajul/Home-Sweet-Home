package com.example.homesweethome.api;

import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.model.Building;
import com.example.homesweethome.model.BuildingsResponse;
import com.example.homesweethome.model.TenantResponse;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BuildingService {
     @Multipart
        @POST("buildings/landlord")
        Call<ApiResponse<Building>> addBuilding(
                @Part("landlord_id") RequestBody landlordId,
                @Part("building_name") RequestBody buildingName,
                @Part("building_address") RequestBody buildingAddress,
                @Part("building_status") RequestBody buildingStatus,
                @Part("building_zone_name") RequestBody zoneName,       // Updated
                @Part("building_sub_zone_name") RequestBody subZoneName, // Updated
                @Part("building_total_floor") RequestBody totalFloors,   // Updated
                @Part("building_total_flat") RequestBody totalFlats,     // Updated
                @Part("building_detail") RequestBody buildingDetails,    // Updated
                @Part MultipartBody.Part buildingImage,
                @Part List<MultipartBody.Part> additionalImages
        );
@Multipart
        @PATCH("buildings/landlord")
        Call<ApiResponse<Building>> updateBuilding(
                @Part("_id") RequestBody buildingId,
                @Part("building_name") RequestBody buildingName,
                @Part("building_address") RequestBody buildingAddress,
                @Part("building_status") RequestBody buildingStatus,
                @Part("building_zone_name") RequestBody zoneName,       // Updated
                @Part("building_sub_zone_name") RequestBody subZoneName, // Updated
                @Part("building_total_floor") RequestBody totalFloors,   // Updated
                @Part("building_total_flat") RequestBody totalFlats,     // Updated
                @Part("building_detail") RequestBody buildingDetails,    // Updated
                @Part MultipartBody.Part buildingImage,
                @Part List<MultipartBody.Part> additionalImages
        );

    @GET("buildings/landlord")
    Call<BuildingsResponse> getBuildingsByLandlord(
            @Query("landlord_id") String landlordId
    );

    @HTTP(method = "DELETE", path = "buildings/landlord", hasBody = true)
    Call<ApiResponse<BuildingsResponse>> deleteBuilding(@Body Map<String, String> body);


}

