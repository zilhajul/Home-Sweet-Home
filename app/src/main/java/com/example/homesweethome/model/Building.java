package com.example.homesweethome.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Building {

    @SerializedName("_id")
    private String id;

    @SerializedName("landlord_id")
    private String landlordId;

    @SerializedName("building_name")
    private String buildingName;

    @SerializedName("building_address")
    private String buildingAddress;

    @SerializedName("building_status")
    private String buildingStatus;

    @SerializedName("zone_name")
    private String zoneName;

    @SerializedName("sub_zone_name")
    private String subZoneName;

    @SerializedName("total_floors")
    private String totalFloors;

    @SerializedName("total_flats")
    private String totalFlats;

    @SerializedName("building_details")
    private String buildingDetails;

    @SerializedName("building_image")
    private String buildingImage;

    @SerializedName("additional_images")
    private List<String> additionalImages;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    public Building() {}

    public Building(String landlordId, String buildingName, String buildingAddress, 
                    String buildingStatus, String zoneName, String subZoneName,
                    String totalFloors, String totalFlats, String buildingDetails) {
        this.landlordId = landlordId;
        this.buildingName = buildingName;
        this.buildingAddress = buildingAddress;
        this.buildingStatus = buildingStatus;
        this.zoneName = zoneName;
        this.subZoneName = subZoneName;
        this.totalFloors = totalFloors;
        this.totalFlats = totalFlats;
        this.buildingDetails = buildingDetails;
    }

    // ─── Getters & Setters ──────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLandlordId() { return landlordId; }
    public void setLandlordId(String landlordId) { this.landlordId = landlordId; }

    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }

    public String getBuildingAddress() { return buildingAddress; }
    public void setBuildingAddress(String buildingAddress) { this.buildingAddress = buildingAddress; }

    public String getBuildingStatus() { return buildingStatus; }
    public void setBuildingStatus(String buildingStatus) { this.buildingStatus = buildingStatus; }

    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }

    public String getSubZoneName() { return subZoneName; }
    public void setSubZoneName(String subZoneName) { this.subZoneName = subZoneName; }

    public String getTotalFloors() { return totalFloors; }
    public void setTotalFloors(String totalFloors) { this.totalFloors = totalFloors; }

    public String getTotalFlats() { return totalFlats; }
    public void setTotalFlats(String totalFlats) { this.totalFlats = totalFlats; }

    public String getBuildingDetails() { return buildingDetails; }
    public void setBuildingDetails(String buildingDetails) { this.buildingDetails = buildingDetails; }

    public String getBuildingImage() { return buildingImage; }
    public void setBuildingImage(String buildingImage) { this.buildingImage = buildingImage; }

    public List<String> getAdditionalImages() { return additionalImages; }
    public void setAdditionalImages(List<String> additionalImages) { this.additionalImages = additionalImages; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}

