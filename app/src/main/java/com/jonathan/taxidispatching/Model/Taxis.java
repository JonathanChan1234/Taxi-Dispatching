package com.jonathan.taxidispatching.Model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Taxis {
    @SerializedName("owned_taxis")
    @Expose
    @Nullable
    public List<Taxi> taxis;
    public class Taxi {
        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("platenumber")
        @Expose
        public String platenumber;
        @SerializedName("last_login_time")
        @Expose
        @Nullable
        public String lastLoginTime;
        @SerializedName("last_logout_time")
        @Expose
        @Nullable
        public String lastLogoutTime;
        @SerializedName("driver_id")
        @Expose
        @Nullable
        public Driver driverId;
        @SerializedName("created_at")
        @Expose
        public String createdAt;
        @SerializedName("updated_at")
        @Expose
        public String updatedAt;
        @SerializedName("occupied")
        @Expose
        public Integer occupied;
        @SerializedName("password")
        @Expose
        public String password;
        @SerializedName("owner")
        @Expose
        public Driver owner;
    }

}
