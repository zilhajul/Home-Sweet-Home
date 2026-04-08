package com.example.homesweethome.model;

/**
 * Model class representing a Subscription plan from the API.
 */
public class Subscription {

    private String id;
    private String subscriptionName;
    private String subscriptionStatus;
    private String subscriptionDescription;
    private double subscriptionPrice;
    private int subscriptionDuration;
    private int buildingAddMaxNumber;
    private int flatAddMaxNumber;
    private String createdAt;
    private String updatedAt;

    // ─── Constructors ──────────────────────────────────────────────────────────

    public Subscription() {}

    public Subscription(String id,
                        String subscriptionName,
                        String subscriptionStatus,
                        String subscriptionDescription,
                        double subscriptionPrice,
                        int subscriptionDuration,
                        int buildingAddMaxNumber,
                        int flatAddMaxNumber,
                        String createdAt,
                        String updatedAt) {
        this.id = id;
        this.subscriptionName = subscriptionName;
        this.subscriptionStatus = subscriptionStatus;
        this.subscriptionDescription = subscriptionDescription;
        this.subscriptionPrice = subscriptionPrice;
        this.subscriptionDuration = subscriptionDuration;
        this.buildingAddMaxNumber = buildingAddMaxNumber;
        this.flatAddMaxNumber = flatAddMaxNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ─── Getters & Setters ──────────────────────────────────────────────────────

    public String getId()                              { return id; }
    public void setId(String id)                       { this.id = id; }

    public String getSubscriptionName()                { return subscriptionName; }
    public void setSubscriptionName(String v)          { this.subscriptionName = v; }

    public String getSubscriptionStatus()              { return subscriptionStatus; }
    public void setSubscriptionStatus(String v)        { this.subscriptionStatus = v; }

    public String getSubscriptionDescription()         { return subscriptionDescription; }
    public void setSubscriptionDescription(String v)   { this.subscriptionDescription = v; }

    public double getSubscriptionPrice()               { return subscriptionPrice; }
    public void setSubscriptionPrice(double v)         { this.subscriptionPrice = v; }

    public int getSubscriptionDuration()               { return subscriptionDuration; }
    public void setSubscriptionDuration(int v)         { this.subscriptionDuration = v; }

    public int getBuildingAddMaxNumber()               { return buildingAddMaxNumber; }
    public void setBuildingAddMaxNumber(int v)         { this.buildingAddMaxNumber = v; }

    public int getFlatAddMaxNumber()                   { return flatAddMaxNumber; }
    public void setFlatAddMaxNumber(int v)             { this.flatAddMaxNumber = v; }

    public String getCreatedAt()                       { return createdAt; }
    public void setCreatedAt(String v)                 { this.createdAt = v; }

    public String getUpdatedAt()                       { return updatedAt; }
    public void setUpdatedAt(String v)                 { this.updatedAt = v; }
}
