package com.example.homesweethome.api;

import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.model.Building;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface BuildingService {

    @Multipart
    @POST("buildings/landlord")
    Call<ApiResponse<Building>> addBuilding(
        @Part("landlord_id") String landlordId,
        @Part("building_name") String buildingName,
        @Part("building_address") String buildingAddress,
        @Part("building_status") String buildingStatus,
        @Part("zone_name") String zoneName,
        @Part("sub_zone_name") String subZoneName,
        @Part("total_floors") String totalFloors,
        @Part("total_flats") String totalFlats,
        @Part("building_details") String buildingDetails,
        @Part("building_image")MultipartBody.Part buildingImage,
        @Part("building_images")List<MultipartBody.Part> additionalImages
    );
/*
    @GET("buildings")
    Call<ApiResponse<List<Building>>> getBuildings();

    @GET("buildings/{id}")
    Call<ApiResponse<Building>> getBuilding(@Path("id") String buildingId);

 */
}

