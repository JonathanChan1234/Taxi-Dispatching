package com.jonathan.taxidispatching.Model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Transcation {
    @SerializedName("data")
    @Expose
    public Data data;
    public class Data {
        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("user")
        @Expose
        public User user;
        @SerializedName("driver")
        @Expose
        @Nullable
        public Driver driver;
        @SerializedName("start_lat")
        @Expose
        public String startLat;
        @SerializedName("start_long")
        @Expose
        public String startLong;
        @SerializedName("start_addr")
        @Expose
        public String startAddr;
        @SerializedName("des_lat")
        @Expose
        public String desLat;
        @SerializedName("des_long")
        @Expose
        public String desLong;
        @SerializedName("des_addr")
        @Expose
        public String desAddr;
        @SerializedName("requirement")
        @Expose
        @Nullable
        public String requirement;
        @SerializedName("first_driver")
        @Expose
        @Nullable
        public Driver firstDriver;
        @SerializedName("second_driver")
        @Expose
        @Nullable
        public Driver secondDriver;
        @SerializedName("third_driver")
        @Expose
        @Nullable
        public Driver thirdDriver;
        @SerializedName("status")
        @Expose
        public Integer status;
        @SerializedName("meet_up_time")
        @Expose
        public String meetUpTime;
        @SerializedName("created_at")
        @Expose
        public CreatedAt createdAt;
        @SerializedName("updated_at")
        @Expose
        public UpdatedAt updatedAt;
    }
}
