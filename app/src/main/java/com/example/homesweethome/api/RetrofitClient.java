package com.example.homesweethome.api;

import android.content.Context;

import com.example.homesweethome.preferences.SessionManager;

import okhttp3.OkHttpClient;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class RetrofitClient {


    private static final String BASE_URL = "https://housebackend.ilhamcollection.com/api/v1/";

    private static RetrofitClient instance;
    private final Retrofit retrofit;
    private SessionManager sessionManager;

    private RetrofitClient(Context context) {

        if (sessionManager == null) {
            sessionManager = new SessionManager(context);
        }

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder()
                            .header("Accept", "application/json");

                    // Check if this is a multipart request by checking the body type
                    boolean isMultipart = original.body() instanceof MultipartBody;
                    
                    // Alternative check: look at the request headers
                    String existingContentType = original.header("Content-Type");
                    if (existingContentType != null && existingContentType.contains("multipart")) {
                        isMultipart = true;
                    }

                    // Log for debugging
                    android.util.Log.d("RetrofitClient", "Request URL: " + original.url());
                    android.util.Log.d("RetrofitClient", "Is Multipart: " + isMultipart);
                    android.util.Log.d("RetrofitClient", "Body Class: " + (original.body() != null ? original.body().getClass().getSimpleName() : "null"));
                    android.util.Log.d("RetrofitClient", "Existing Content-Type: " + existingContentType);

                    // Only set Content-Type: application/json for non-multipart requests
                    if (!isMultipart) {
                        builder.header("Content-Type", "application/json");
                        android.util.Log.d("RetrofitClient", "Setting Content-Type to application/json");
                    } else {
                        android.util.Log.d("RetrofitClient", "NOT setting Content-Type (multipart request)");
                    }

                    // Attach Bearer token if available
                    if (sessionManager != null && sessionManager.getToken() != null) {
                        builder.header("Authorization", "Bearer " + sessionManager.getToken());
                    }

                    return chain.proceed(builder.build());
                })
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance(Context context) {
        if (instance == null) {
            instance = new RetrofitClient(context);
        }
        return instance;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;

    }

    public AuthService getAuthService() {
        return retrofit.create(AuthService.class);
    }

    public BuildingService getBuildingService() {
        return retrofit.create(BuildingService.class);
    }

    public FlatService getFlatService() { return retrofit.create(FlatService.class); }
}
