package com.jonathan.taxidispatching.Model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Driver implements Serializable {
    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("phonenumber")
    @Expose
    public String phonenumber;

    @SerializedName("username")
    public String username;

    @SerializedName("lat")
    @Expose
    @Nullable
    public String latitude;

    @SerializedName("long")
    @Expose
    @Nullable
    public String longitude;

    @SerializedName("occupied")
    @Expose
    public Integer occupied;

    @SerializedName("location_updated")
    @Expose
    public String location_updated;

    @SerializedName("updated_at")
    @Expose
    public String updated_at;

    @SerializedName("rating")
    @Expose
    @Nullable
    List<Rating> rating;

}
