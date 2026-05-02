package com.example.homesweethome.model;

import com.google.gson.annotations.SerializedName;

public class ComplainItem {

    @SerializedName("_id")
    private String id;

    @SerializedName("tenant_id")
    private Tenant tenant;

    @SerializedName("landlord_id")
    private Landlord landlord; // ✅ existing class use

    @SerializedName("flat_id")
    private Flat flat; // ✅ existing class use

    @SerializedName("building_id")
    private Building building; // ✅ existing class use

    @SerializedName("complain_text")
    private String complainText;

    @SerializedName("complain_status")
    private String complainStatus;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("__v")
    private int version;

    // Getters
    public String getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public Landlord getLandlord() { return landlord; }
    public Flat getFlat() { return flat; }
    public Building getBuilding() { return building; }
    public String getComplainText() { return complainText; }
    public String getComplainStatus() { return complainStatus; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
}
