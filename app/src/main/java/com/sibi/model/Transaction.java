package com.sibi.model;

/**
 * Created by adway on 23/11/17.
 */

public class Transaction {
    public static int TYPE_WISHLIST_SAVING = 0;
    public static int TYPE_EXPENSE = 1;
    private String key;
    private double amount;
    private long timestamp;
    private String name;
    private String locationJson;
    private String photoJson;
    private long latestUpdateTimestamp;
    private int type;
    private String category;
    private String userEmail;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getLatestUpdateTimestamp() {
        return latestUpdateTimestamp;
    }

    public void setLatestUpdateTimestamp(long latestUpdateTimestamp) {
        this.latestUpdateTimestamp = latestUpdateTimestamp;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocationJson() {
        return locationJson;
    }

    public void setLocationJson(String locationJson) {
        this.locationJson = locationJson;
    }

    public String getPhotoJson() {
        return photoJson;
    }

    public void setPhotoJson(String photoJson) {
        this.photoJson = photoJson;
    }

    @Override public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(amount);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + name.hashCode();
        result = 31 * result + (locationJson != null ? locationJson.hashCode() : 0);
        result = 31 * result + (photoJson != null ? photoJson.hashCode() : 0);
        return result;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;

        return timestamp == that.timestamp
            && name.equals(that.name);
    }
}
