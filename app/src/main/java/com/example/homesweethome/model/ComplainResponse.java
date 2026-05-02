package com.example.homesweethome.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ComplainResponse {

    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<ComplainItem> data;

    // Getters & Setters
    public int getStatusCode() { return statusCode; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<ComplainItem> getData() { return data; }
}
