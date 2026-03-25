package com.example.homesweethome.api;

import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.model.LoginRequest;
import com.example.homesweethome.model.RegisterRequest;
import com.example.homesweethome.model.User;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {

    @POST("auth/login")
    Call<ApiResponse<User>> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<ApiResponse<User>> register(@Body RegisterRequest request);

    @POST("auth/logout")
    Call<ApiResponse<Void>> logout();

    @POST("auth/forgot-password")
    Call<ApiResponse<Void>> forgotPassword(@Body Map<String, String> body);

    @POST("auth/verify-otp")
    Call<ApiResponse<Void>> verifyOtp(@Body Map<String, String> body);

    @POST("auth/reset-password")
    Call<ApiResponse<Void>> resetPassword(@Body Map<String, String> body);
}
