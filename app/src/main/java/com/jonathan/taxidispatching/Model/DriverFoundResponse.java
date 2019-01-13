package com.jonathan.taxidispatching.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.reactivex.annotations.Nullable;

public class DriverFoundResponse {
    @SerializedName("transcation")
    @Expose
    @Nullable
    public Transcation.Data transcation;

    @SerializedName("driver")
    @Expose
    @Nullable
    public Driver driver;
}
