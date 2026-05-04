package com.example.homesweethome.model;

import com.google.gson.annotations.SerializedName;

public class Tenant {

    @SerializedName("_id")
    private String id;

    @SerializedName("tenant_name")
    private String tenantName;

    @SerializedName("tenant_phone")
    private String tenantPhone;

    @SerializedName("tenant_status")
    private String tenantStatus;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("__v")
    private int version;

    @SerializedName("tenant_address")
    private String tenantAddress;

    @SerializedName("tenant_image")
    private String tenantImage;

    @SerializedName("tenant_nid_number")
    private String tenantNidNumber;

    @SerializedName("forgot_password_otp")
    private Integer forgotPasswordOtp; // ⚠️ null আসতে পারে, তাই Integer

    @SerializedName("createdAt")
    private String createdAt;
    private int forgotPasswordOtp;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("__v")
    private int version;

    public Tenant() {}

    public String getId() {return id;}

    public void setId(String id) {this.id = id;}
    // Getters & Setters

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTenantName() {return tenantName;}

    public void setTenantName(String tenantName) {this.tenantName = tenantName;}
    public String getTenantName() { return tenantName; }
    public void setTenantName(String tenantName) { this.tenantName = tenantName; }

    public String getTenantPhone() {return tenantPhone;}

    public void setTenantPhone(String tenantPhone) {this.tenantPhone = tenantPhone;}
    public String getTenantPhone() { return tenantPhone; }
    public void setTenantPhone(String tenantPhone) { this.tenantPhone = tenantPhone; }

    public String getTenantStatus() {return tenantStatus;}

    public void setTenantStatus(String tenantStatus) {this.tenantStatus = tenantStatus;}
    public String getTenantStatus() { return tenantStatus; }
    public void setTenantStatus(String tenantStatus) { this.tenantStatus = tenantStatus; }

    public String getCreatedAt() {return createdAt;}

    public void setCreatedAt(String createdAt) {this.createdAt = createdAt;}

    public String getUpdatedAt() {return updatedAt;}

    public void setUpdatedAt(String updatedAt) {this.updatedAt = updatedAt;}

    public int getVersion() {return version;}

    public void setVersion(int version) {this.version = version;}

    public String getTenantAddress() {return tenantAddress;}

    public void setTenantAddress(String tenantAddress) {this.tenantAddress = tenantAddress;}
    public String getTenantAddress() { return tenantAddress; }
    public void setTenantAddress(String tenantAddress) { this.tenantAddress = tenantAddress; }

    public String getTenantImage() {return tenantImage;}

    public void setTenantImage(String tenantImage) {this.tenantImage = tenantImage;}
    public String getTenantImage() { return tenantImage; }
    public void setTenantImage(String tenantImage) { this.tenantImage = tenantImage; }

    public String getTenantNidNumber() {return tenantNidNumber;}

    public void setTenantNidNumber(String tenantNidNumber) {this.tenantNidNumber = tenantNidNumber;}
    public String getTenantNidNumber() { return tenantNidNumber; }
    public void setTenantNidNumber(String tenantNidNumber) { this.tenantNidNumber = tenantNidNumber; }

    public int getForgotPasswordOtp() {return forgotPasswordOtp;}
    public Integer getForgotPasswordOtp() { return forgotPasswordOtp; }
    public void setForgotPasswordOtp(Integer forgotPasswordOtp) { this.forgotPasswordOtp = forgotPasswordOtp; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    public void setForgotPasswordOtp(int forgotPasswordOtp) {this.forgotPasswordOtp = forgotPasswordOtp;}
    // ✅ Helper Methods

    public boolean isActive() {
        return "active".equalsIgnoreCase(tenantStatus);
    }
}
