package com.example.homesweethome.api;

import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.model.BuildingsResponse;
import com.example.homesweethome.model.Complain;
import com.example.homesweethome.model.ComplainCreateResponse;
import com.example.homesweethome.model.ComplainRequest;
import com.example.homesweethome.model.ComplainResponse;
import com.example.homesweethome.model.Flat;
import com.example.homesweethome.model.Landlord;
import com.example.homesweethome.model.LoginRequest;
import com.example.homesweethome.model.RegisterRequest;
import com.example.homesweethome.model.Subscription;
import com.example.homesweethome.model.SignInResponse;
import com.example.homesweethome.model.SubscriptionPurchaseResponse;
import com.example.homesweethome.model.TenantLoginRequest;
import com.example.homesweethome.model.TenantRegisterRequest;
import com.example.homesweethome.model.TenantResponse;
import com.example.homesweethome.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface AuthService {

    @POST("landlords/login")
    Call<ApiResponse<SignInResponse>> landlordLogin(@Body LoginRequest request);

    @POST("tenants/login")
    Call<ApiResponse<SignInResponse>> tenantLogin(@Body TenantLoginRequest request);

    @POST("landlords")
    Call<ApiResponse<User>> register(@Body RegisterRequest request);

    @POST("auth/logout")
    Call<ApiResponse<Void>> logout();

    @POST("landlords/forgot_password_otp")
    Call<ApiResponse<Void>> forgotPassword(@Body Map<String, String> body);

    @POST("landlords/verify_otp")
    Call<ApiResponse<Void>> verifyOtp(@Body Map<String, Object> body);

    @POST("landlords/update_password")
    Call<ApiResponse<Void>> resetPassword(@Body Map<String, String> body);

    @GET("landlords")
    Call<ApiResponse<Landlord>> getLandlord();

    @GET("subscriptions")
    Call<ApiResponse<List<Subscription>>> getSubscriptions();

    @POST("subscription_purchases")
    Call<ApiResponse<SubscriptionPurchaseResponse>> purchaseSubscription(@Body Map<String, String> body);

    @POST("tenants")
    Call<ApiResponse<User>> tenantRegister(@Body TenantRegisterRequest request);

    @Multipart
    @POST("flats/landlord")
    Call<ApiResponse<Flat>> addFlat(
            @Part("landlord_id") RequestBody landlordId,
            @Part("building_id") RequestBody buildingId,
            @Part("flat_name") RequestBody flatName,
            @Part MultipartBody.Part flatImage,
            @Part("flat_status") RequestBody flatStatus,
            @Part List<MultipartBody.Part> flatImages,
            @Part("flat_detail") RequestBody flatDetail,
            @Part("floor_number") RequestBody floorNumber,
            @Part("flat_rent") RequestBody flatRent,
            @Part("gas_bill") RequestBody gasBill,
            @Part("electricity_bill") RequestBody electricityBill,
            @Part("water_bill") RequestBody waterBill,
            @Part("service_charge") RequestBody serviceCharge
    );
    @Multipart
    @PATCH("flats/landlord")
    Call<ApiResponse<Flat>> updateFlat(
            @Part("_id") RequestBody flatId,
            @Part("flat_name") RequestBody flatName,
            @Part MultipartBody.Part flatImage,
            @Part("flat_status") RequestBody flatStatus,
            @Part List<MultipartBody.Part> flatImages,
            @Part("flat_detail") RequestBody flatDetail,
            @Part("flat_rent") RequestBody flatRent,
            @Part("gas_bill") RequestBody gasBill,
            @Part("electricity_bill") RequestBody electricityBill,
            @Part("water_bill") RequestBody waterBill,
            @Part("service_charge") RequestBody serviceCharge
    );

    // ========== Complain Endpoints ==========

    @POST("complains")
    Call<ComplainCreateResponse> createComplain(@Body ComplainRequest request);

    @GET("complains")
    Call<ComplainResponse> getComplains(
            @Query("landlord_id") String landlordId,
            @Query("tenant_id") String tenantId
    );
