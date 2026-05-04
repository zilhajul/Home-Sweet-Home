package com.example.homesweethome.model;

import com.google.gson.annotations.SerializedName;

public class Complain {

    @SerializedName("_id")
    private String _id;

    @SerializedName("tenant_id")
    private Tenant tenant_id;

    @SerializedName("landlord_id")
    private Landlord landlord_id;

    @SerializedName("flat_id")
    private Flat flat_id;

    @SerializedName("building_id")
    private Building building_id;

    @SerializedName("complain_text")
    private String complain_text;

    @SerializedName("complain_status")
    private String complain_status;

    @SerializedName("complain_response")
    private String complain_response;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("__v")
    private int __v;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Tenant getTenant_id() {
        return tenant_id;
    }

    public void setTenant_id(Tenant tenant_id) {
        this.tenant_id = tenant_id;
    }

    public Landlord getLandlord_id() {
        return landlord_id;
    }

    public void setLandlord_id(Landlord landlord_id) {
        this.landlord_id = landlord_id;
    }

    public Flat getFlat_id() {
        return flat_id;
    }

    public void setFlat_id(Flat flat_id) {
        this.flat_id = flat_id;
    }

    public Building getBuilding_id() {
        return building_id;
    }

    public void setBuilding_id(Building building_id) {
        this.building_id = building_id;
    }

    public String getComplain_text() {
        return complain_text;
    }

    public void setComplain_text(String complain_text) {
        this.complain_text = complain_text;
    }

    public String getComplain_status() {
        return complain_status;
    }

    public void setComplain_status(String complain_status) {
        this.complain_status = complain_status;
    }

    public String getComplain_response() {
        return complain_response;
    }

    public void setComplain_response(String complain_response) {
        this.complain_response = complain_response;
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

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }
}
