package com.example.homesweethome.model;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("password")
    private String password;

    @SerializedName("password_confirmation")
    private String passwordConfirmation;

    @SerializedName("role")
    private String role;

    public RegisterRequest(String name, String email, String phone,
                           String password, String passwordConfirmation, String role) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
        this.role = role;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public String getPasswordConfirmation() { return passwordConfirmation; }
    public String getRole() { return role; }
}
