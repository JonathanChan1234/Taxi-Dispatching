package com.jonathan.taxidispatching.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdatedAt {
    @SerializedName("date")
    @Expose
    public String date;
    @SerializedName("timezone_type")
    @Expose
    public Integer timezoneType;
    @SerializedName("timezone")
    @Expose
    public String timezone;
}
