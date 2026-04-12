package com.example.homesweethome.api;

import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.model.LoginRequest;
import com.example.homesweethome.model.RegisterRequest;
import com.example.homesweethome.model.Subscription;
import com.example.homesweethome.model.SignInResponse;
import com.example.homesweethome.model.TenantLoginRequest;
import com.example.homesweethome.model.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

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
    Call<ApiResponse<User>> getLandlord();

    @GET("subscriptions")
    Call<ApiResponse<List<Subscription>>> getSubscriptions();
}
