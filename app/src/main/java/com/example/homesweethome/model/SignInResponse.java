package com.example.homesweethome.model;

import com.google.gson.annotations.SerializedName;

public class SignInResponse {

    @SerializedName("token")
    private String token;

    // Constructors
    public SignInResponse() {}

    public SignInResponse(String token) {
        this.token = token;
    }

    // Getters & Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

