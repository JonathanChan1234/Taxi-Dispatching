package com.jonathan.taxidispatching.APIObject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("username")
    @Expose
    public String username;
    @SerializedName("phonenumber")
    @Expose
    public String phonenumber;
    @SerializedName("email_verified_at")
    @Expose
    public Object emailVerifiedAt;
    @SerializedName("transcation_id")
    @Expose
    public Object transcationId;
    @SerializedName("created_at")
    @Expose
    public String createdAt;
    @SerializedName("updated_at")
    @Expose
    public String updatedAt;
}
