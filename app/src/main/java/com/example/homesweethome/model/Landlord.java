package com.example.homesweethome.model;

import com.google.gson.annotations.SerializedName;

public class Landlord {

    @SerializedName("_id")
    private String id;

    @SerializedName("login_id")
    private String loginId;

    @SerializedName("landlord_name")
    private String landlordName;

    @SerializedName("landlord_phone")
    private String landlordPhone;

    @SerializedName("landlord_status")
    private String landlordStatus;

    @SerializedName("landlord_address")
    private String landlordAddress;

    @SerializedName("free_plan_use")
    private boolean freePlanUse;

    @SerializedName("forgot_password_otp")
    private int forgotPasswordOtp;

    @SerializedName("subscription_purchase_id")
    private SubscriptionPurchase subscriptionPurchase;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("__v")
    private int version;

    public Landlord() {}

    // ─── Getters & Setters ──────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLoginId() { return loginId; }
    public void setLoginId(String loginId) { this.loginId = loginId; }

    public String getLandlordName() { return landlordName; }
    public void setLandlordName(String landlordName) { this.landlordName = landlordName; }

    public String getLandlordPhone() { return landlordPhone; }
    public void setLandlordPhone(String landlordPhone) { this.landlordPhone = landlordPhone; }

    public String getLandlordStatus() { return landlordStatus; }
    public void setLandlordStatus(String landlordStatus) { this.landlordStatus = landlordStatus; }

    public String getLandlordAddress() { return landlordAddress; }
    public void setLandlordAddress(String landlordAddress) { this.landlordAddress = landlordAddress; }

    public boolean isFreePlanUse() { return freePlanUse; }
    public void setFreePlanUse(boolean freePlanUse) { this.freePlanUse = freePlanUse; }

    public int getForgotPasswordOtp() { return forgotPasswordOtp; }
    public void setForgotPasswordOtp(int forgotPasswordOtp) { this.forgotPasswordOtp = forgotPasswordOtp; }

    public SubscriptionPurchase getSubscriptionPurchase() { return subscriptionPurchase; }
    public void setSubscriptionPurchase(SubscriptionPurchase subscriptionPurchase) { this.subscriptionPurchase = subscriptionPurchase; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    // ─── Helper Methods ──────────────────────────────────────────────────────

    public boolean isActive() {
        return "active".equalsIgnoreCase(landlordStatus);
    }

    public boolean hasActiveSubscription() {
        return subscriptionPurchase != null && subscriptionPurchase.isPaymentComplete();
    }
}

