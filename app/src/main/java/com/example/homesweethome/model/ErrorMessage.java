package com.example.homesweethome.model;

import com.google.gson.annotations.SerializedName;

public class ErrorMessage {

    @SerializedName("path")
    private String path;

    @SerializedName("message")
    private String message;

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
