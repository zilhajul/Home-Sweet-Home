package com.example.homesweethome.model;

import com.google.gson.annotations.SerializedName;

public class TenantRegisterRequest {
    @SerializedName("tenant_name")
    private String name;

    @SerializedName("tenant_phone")
    private String phone;

    @SerializedName("tenant_password")
    private String password;


    public TenantRegisterRequest(String name,  String phone,
                           String password) {
        this.name = name;

        this.phone = phone;
        this.password = password;

    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
}
