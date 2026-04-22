package com.example.homesweethome.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Flat {
    @SerializedName("landlord_id")
    private String landlordId;

    @SerializedName("building_id")
    private String buildingId;

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

    @SerializedName("_id")
    private String id;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("__v")
    private int v;

    // Getters and Setters
    public String getLandlordId() {
        return landlordId;
    }

    public void setLandlordId(String landlordId) {
        this.landlordId = landlordId;
    }

    public String getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(String buildingId) {
        this.buildingId = buildingId;
    }

    public String getFlatName() {
        return flatName;
    }

    public void setFlatName(String flatName) {
        this.flatName = flatName;
    }

    public String getFlatImage() {
        return flatImage;
    }

    public void setFlatImage(String flatImage) {
        this.flatImage = flatImage;
    }

    public String getFlatStatus() {
        return flatStatus;
    }

    public void setFlatStatus(String flatStatus) {
        this.flatStatus = flatStatus;
    }

    public String getFlatDetail() {
        return flatDetail;
    }

    public void setFlatDetail(String flatDetail) {
        this.flatDetail = flatDetail;
    }

    public List<String> getFlatImages() {
        return flatImages;
    }

    public void setFlatImages(List<String> flatImages) {
        this.flatImages = flatImages;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public double getFlatRent() {
        return flatRent;
    }

    public void setFlatRent(double flatRent) {
        this.flatRent = flatRent;
    }

    public double getGasBill() {
        return gasBill;
    }

    public void setGasBill(double gasBill) {
        this.gasBill = gasBill;
    }

    public double getElectricityBill() {
        return electricityBill;
    }

    public void setElectricityBill(double electricityBill) {
        this.electricityBill = electricityBill;
    }

    public double getWaterBill() {
        return waterBill;
    }

    public void setWaterBill(double waterBill) {
        this.waterBill = waterBill;
    }

    public double getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(double serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }
}
