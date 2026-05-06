package com.example.homesweethome.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TenantFlatResponse {

    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<FlatData> data;

    @SerializedName("totalData")
    private int totalData;

    // =========================================================
    // Getters
    // =========================================================

    public int getStatusCode() { return statusCode; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<FlatData> getData() { return data; }
    public int getTotalData() { return totalData; }


    // =========================================================
    // Inner class: FlatData
    // =========================================================

    public static class FlatData {

        @SerializedName("_id")
        private String id;

        @SerializedName("landlord_id")
        private LandlordInfo landlordId;

        @SerializedName("building_id")
        private BuildingInfo buildingId;

        @SerializedName("tenant_id")
        private String tenantId;

        @SerializedName("flat_name")
        private String flatName;

        @SerializedName("flat_image")
        private String flatImage;

        @SerializedName("flat_status")
        private String flatStatus;

        @SerializedName("flat_detail")
        private String flatDetail;

        @SerializedName("flat_images")
        private List<String> flatImages;

        @SerializedName("floor_number")
        private int floorNumber;

        @SerializedName("flat_rent")
        private double flatRent;

        @SerializedName("gas_bill")
        private double gasBill;

        @SerializedName("electricity_bill")
        private double electricityBill;

        @SerializedName("water_bill")
        private double waterBill;

        @SerializedName("service_charge")
        private double serviceCharge;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("updatedAt")
        private String updatedAt;

        // Getters
        public String getId() { return id; }
        public LandlordInfo getLandlordId() { return landlordId; }
        public BuildingInfo getBuildingId() { return buildingId; }
        public String getTenantId() { return tenantId; }
        public String getFlatName() { return flatName; }
        public String getFlatImage() { return flatImage; }
        public String getFlatStatus() { return flatStatus; }
        public String getFlatDetail() { return flatDetail; }
        public List<String> getFlatImages() { return flatImages; }
        public int getFloorNumber() { return floorNumber; }
        public double getFlatRent() { return flatRent; }
        public double getGasBill() { return gasBill; }
        public double getElectricityBill() { return electricityBill; }
        public double getWaterBill() { return waterBill; }
        public double getServiceCharge() { return serviceCharge; }
        public String getCreatedAt() { return createdAt; }
        public String getUpdatedAt() { return updatedAt; }

        // Total monthly expense helper
        public double getTotalExpenses() {
            return flatRent + gasBill + electricityBill + waterBill + serviceCharge;
        }
    }


    // =========================================================
    // Inner class: LandlordInfo
    // =========================================================

    public static class LandlordInfo {

        @SerializedName("_id")
        private String id;

        @SerializedName("landlord_name")
        private String landlordName;

        @SerializedName("landlord_phone")
        private String landlordPhone;

        @SerializedName("landlord_status")
        private String landlordStatus;

        @SerializedName("subscription_purchase_id")
        private String subscriptionPurchaseId;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("updatedAt")
        private String updatedAt;

        // Getters
        public String getId() { return id; }
        public String getLandlordName() { return landlordName; }
        public String getLandlordPhone() { return landlordPhone; }
        public String getLandlordStatus() { return landlordStatus; }
        public String getSubscriptionPurchaseId() { return subscriptionPurchaseId; }
        public String getCreatedAt() { return createdAt; }
        public String getUpdatedAt() { return updatedAt; }
    }


    // =========================================================
    // Inner class: BuildingInfo
    // =========================================================

    public static class BuildingInfo {

        @SerializedName("_id")
        private String id;

        @SerializedName("landlord_id")
        private String landlordId;

        @SerializedName("building_name")
        private String buildingName;

        @SerializedName("building_image")
        private String buildingImage;

        @SerializedName("building_status")
        private String buildingStatus;

        @SerializedName("building_address")
        private String buildingAddress;

        @SerializedName("building_total_floor")
        private int buildingTotalFloor;

        @SerializedName("building_total_flat")
        private int buildingTotalFlat;

        @SerializedName("building_zone_name")
        private String buildingZoneName;

        @SerializedName("building_sub_zone_name")
        private String buildingSubZoneName;

        @SerializedName("building_detail")
        private String buildingDetail;

        @SerializedName("building_images")
        private List<String> buildingImages;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("updatedAt")
        private String updatedAt;

        // Getters
        public String getId() { return id; }
        public String getLandlordId() { return landlordId; }
        public String getBuildingName() { return buildingName; }
        public String getBuildingImage() { return buildingImage; }
        public String getBuildingStatus() { return buildingStatus; }
        public String getBuildingAddress() { return buildingAddress; }
        public int getBuildingTotalFloor() { return buildingTotalFloor; }
        public int getBuildingTotalFlat() { return buildingTotalFlat; }
        public String getBuildingZoneName() { return buildingZoneName; }
        public String getBuildingSubZoneName() { return buildingSubZoneName; }
        public String getBuildingDetail() { return buildingDetail; }
        public List<String> getBuildingImages() { return buildingImages; }
        public String getCreatedAt() { return createdAt; }
        public String getUpdatedAt() { return updatedAt; }
    }
}
