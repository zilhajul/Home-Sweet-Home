package com.example.homesweethome.api;

import com.example.homesweethome.model.FlatResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FlatService {

    @GET("flats/landlord")
    Call<FlatResponse> getFlatsByBuilding(
            @Query("landlord_id") String landlordId,
            @Query("building_id") String buildingId
    );
}
