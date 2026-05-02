package com.example.homesweethome.model;

import com.google.gson.annotations.SerializedName;

public class ComplainRequest {

    @SerializedName("tenant_id")
    private String tenantId;

    @SerializedName("landlord_id")
    private String landlordId;

    @SerializedName("complain_text")
    private String complainText;

    @SerializedName("building_id")
    private String buildingId;

    @SerializedName("flat_id")
    private String flatId;

    // Empty constructor
    public ComplainRequest() {
    }

    // Full constructor
    public ComplainRequest(String tenantId, String landlordId, String complainText,
                           String buildingId, String flatId) {
        this.tenantId = tenantId;
        this.landlordId = landlordId;
        this.complainText = complainText;
        this.buildingId = buildingId;
        this.flatId = flatId;
    }

    // Getters and Setters

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getLandlordId() {
        return landlordId;
    }

    public void setLandlordId(String landlordId) {
        this.landlordId = landlordId;
    }

    public String getComplainText() {
        return complainText;
    }

    public void setComplainText(String complainText) {
        this.complainText = complainText;
    }

    public String getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(String buildingId) {
        this.buildingId = buildingId;
    }

    public String getFlatId() {
        return flatId;
    }

    public void setFlatId(String flatId) {
        this.flatId = flatId;
    }
}