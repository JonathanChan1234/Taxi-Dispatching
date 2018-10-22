package com.jonathan.taxidispatching.APIObject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreatedAt {
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
