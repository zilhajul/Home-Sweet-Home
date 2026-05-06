package com.example.homesweethome.model;

import com.google.gson.annotations.SerializedName;

public class Rent {
    @SerializedName("_id")
    private String id;

    @SerializedName("rent_month")
    private String rentMonth;

    @SerializedName("rent_year")
    private Integer rentYear;

    @SerializedName("rent_status")
    private String rentStatus;

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

    @SerializedName("total_rent")
    private double totalRent;

    @SerializedName("tenant_id")
    private Tenant tenantId;
    // getters...


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRentMonth() {
        return rentMonth;
    }

    public void setRentMonth(String rentMonth) {
        this.rentMonth = rentMonth;
    }

    public Integer getRentYear() {
        return rentYear;
    }

    public void setRentYear(Integer rentYear) {
        this.rentYear = rentYear;
    }

    public String getRentStatus() {
        return rentStatus;
    }

    public void setRentStatus(String rentStatus) {
        this.rentStatus = rentStatus;
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

    public double getTotalRent() {
        return totalRent;
    }

    public void setTotalRent(double totalRent) {
        this.totalRent = totalRent;
    }

    public Tenant getTenantId() {
        return tenantId;
    }

    public void setTenantId(Tenant tenantId) {
        this.tenantId = tenantId;
    }
}