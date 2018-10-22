package com.jonathan.taxidispatching.APIInterface;

import android.support.annotation.Nullable;

import com.jonathan.taxidispatching.APIObject.Transcation;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIInterface {
    @FormUrlEncoded
    @POST("transcation")
    Call<Transcation> startTranscation(@Field("userid")Integer userid,
                                       @Field("start_lat") Double start_lat,
                                       @Field("start_lat") Double start_long,
                                       @Field("start_addr")  String start_addr,
                                       @Field("des_lat") Double des_lat,
                                       @Field("des_long") Double des_long,
                                       @Field("des_addr") String des_arr,
                                       @Field("meet_up_time")@Nullable String meet_up_time,
                                       @Field("requirement") @Nullable String requirement);
}
