package com.jonathan.taxidispatching.Model;

import com.google.gson.annotations.SerializedName;

public class StandardResponse {
    @SerializedName("success")
    public Integer success;
    @SerializedName("message")
    public String message;
}
