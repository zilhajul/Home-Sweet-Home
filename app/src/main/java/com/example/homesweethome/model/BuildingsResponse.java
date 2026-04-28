package com.example.homesweethome.model;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BuildingsResponse {

    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<Building> data;

    @SerializedName("totalData")
    private int totalData;

    // Getters and Setters
    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Building> getData() {
        return data;
    }

    public void setData(List<Building> data) {
        this.data = data;
    }

    public int getTotalData() {
        return totalData;
    }

    public void setTotalData(int totalData) {
        this.totalData = totalData;
    }
}
