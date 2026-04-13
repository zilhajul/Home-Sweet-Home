package com.example.homesweethome.model;

import com.google.gson.annotations.SerializedName;

public class SubscriptionPurchase {

    @SerializedName("_id")
    private String id;

    @SerializedName("landlord_id")
    private String landlordId;

    @SerializedName("subscription_id")
    private String subscriptionId;

    @SerializedName("payment_status")
    private String paymentStatus;

    @SerializedName("subscription_start_date")
    private String subscriptionStartDate;

    @SerializedName("subscription_end_date")
    private String subscriptionEndDate;

    @SerializedName("grand_total_amount")
    private double grandTotalAmount;

    @SerializedName("invoice_id")
    private String invoiceId;

    @SerializedName("building_add_max_number")
    private int buildingAddMaxNumber;

    @SerializedName("flat_add_max_number")
    private int flatAddMaxNumber;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("__v")
    private int version;

    public SubscriptionPurchase() {}

    // ─── Getters & Setters ──────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLandlordId() { return landlordId; }
    public void setLandlordId(String landlordId) { this.landlordId = landlordId; }

    public String getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(String subscriptionId) { this.subscriptionId = subscriptionId; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getSubscriptionStartDate() { return subscriptionStartDate; }
    public void setSubscriptionStartDate(String subscriptionStartDate) { this.subscriptionStartDate = subscriptionStartDate; }

    public String getSubscriptionEndDate() { return subscriptionEndDate; }
    public void setSubscriptionEndDate(String subscriptionEndDate) { this.subscriptionEndDate = subscriptionEndDate; }

    public double getGrandTotalAmount() { return grandTotalAmount; }
    public void setGrandTotalAmount(double grandTotalAmount) { this.grandTotalAmount = grandTotalAmount; }

    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }

    public int getBuildingAddMaxNumber() { return buildingAddMaxNumber; }
    public void setBuildingAddMaxNumber(int buildingAddMaxNumber) { this.buildingAddMaxNumber = buildingAddMaxNumber; }

    public int getFlatAddMaxNumber() { return flatAddMaxNumber; }
    public void setFlatAddMaxNumber(int flatAddMaxNumber) { this.flatAddMaxNumber = flatAddMaxNumber; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    public boolean isPaymentComplete() {
        return "paid".equalsIgnoreCase(paymentStatus);
    }
}

