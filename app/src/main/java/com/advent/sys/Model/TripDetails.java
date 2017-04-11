package com.advent.sys.Model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TripDetails {

    @SerializedName("operation")
    @Expose
    private String operation;
    @SerializedName("trip")
    @Expose
    private Trip trip;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

}