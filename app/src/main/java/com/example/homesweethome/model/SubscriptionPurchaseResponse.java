package com.example.homesweethome.model;

import com.google.gson.annotations.SerializedName;

public class SubscriptionPurchaseResponse {

    @SerializedName("payment_url")
    private String paymentUrl;

    public SubscriptionPurchaseResponse() {}

    public SubscriptionPurchaseResponse(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }
}

