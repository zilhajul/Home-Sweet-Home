package com.example.homesweethome.api;

import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.model.FlatResponse;
import com.example.homesweethome.model.TenantResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Query;

public interface FlatService {

    @GET("flats/landlord")
    Call<FlatResponse> getFlatsByBuilding(
            @Query("landlord_id") String landlordId,
            @Query("building_id") String buildingId
    );

    @HTTP(method = "DELETE", path = "flats/landlord", hasBody = true)
    Call<ApiResponse<FlatResponse>> deleteFlat(@Body Map<String, String> body);
}
