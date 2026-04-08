package com.example.homesweethome.model;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {

    @SerializedName("landlord_name")
    private String name;



    @SerializedName("landlord_phone")
    private String phone;

    @SerializedName("landlord_password")
    private String password;


    public RegisterRequest(String name,  String phone,
                           String password) {
        this.name = name;

        this.phone = phone;
        this.password = password;

    }

    public String getName() { return name; }
   public String getPhone() { return phone; }
    public String getPassword() { return password; }

}
