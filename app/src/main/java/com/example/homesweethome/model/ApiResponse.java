package com.example.homesweethome.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiResponse<T> {

    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private T data;

    @SerializedName("errorMessages")
    private List<ErrorMessage> errorMessages;

    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public List<ErrorMessage> getErrorMessages() { return errorMessages; }
    public void setErrorMessages(List<ErrorMessage> errorMessages) { this.errorMessages = errorMessages; }
}
