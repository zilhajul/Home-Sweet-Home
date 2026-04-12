package com.example.homesweethome.model;
import com.google.gson.annotations.SerializedName;

public class TenantLoginRequest {

    @SerializedName("tenant_phone")
    private String number;

    @SerializedName("tenant_password")
    private String password;



    public TenantLoginRequest(String number, String password) {
        this.number = number;
        this.password = password;

    }

    public String getNumber() { return number; }
    public String getPassword() { return password; }

}
