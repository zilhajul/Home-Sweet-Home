package com.example.homesweethome.model;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("landlord_phone")
    private String number;

    @SerializedName("landlord_password")
    private String password;



    public LoginRequest(String number, String password) {
        this.number = number;
        this.password = password;

    }

    public String getNumber() { return number; }
    public String getPassword() { return password; }

}
