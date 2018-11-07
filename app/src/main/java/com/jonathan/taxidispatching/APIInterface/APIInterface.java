package com.jonathan.taxidispatching.APIInterface;

import android.support.annotation.Nullable;

import com.jonathan.taxidispatching.APIObject.AccountResponse;
import com.jonathan.taxidispatching.APIObject.Transcation;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIInterface {
    @FormUrlEncoded
    @POST("transcation/startTranscation")
    Call<Transcation> startTranscation(@Field("userid")Integer userid,
                                       @Field("start_lat") Double start_lat,
                                       @Field("start_lat") Double start_long,
                                       @Field("start_addr")  String start_addr,
                                       @Field("des_lat") Double des_lat,
                                       @Field("des_long") Double des_long,
                                       @Field("des_addr") String des_arr,
                                       @Field("meet_up_time")@Nullable String meet_up_time,
                                       @Field("requirement") @Nullable String requirement);

    @POST("user/login")
    Single<AccountResponse> passengerSignIn(
            @Field("phonenumber") String phonenumber,
            @Field("password") String password
    );

    @Multipart
    @POST("user/register")
    Single<AccountResponse> passengerCreateAccount(
            @Part MultipartBody.Part profileImg,
            @Part("username") RequestBody username,
            @Part("password") RequestBody password,
            @Part("phonenumber") RequestBody phonenumber,
            @Part("email") RequestBody email
    );

    @POST("driver/login")
    Single<AccountResponse> driverSignIn(
            @Field("phonenumber") String phonenumber,
            @Field("password") String password
    );

    @Multipart
    @POST("driver/register")
    Single<AccountResponse> driverCreateAccount(
            @Part MultipartBody.Part profileImg,
            @Part("username") RequestBody username,
            @Part("password") RequestBody password,
            @Part("phonenumber") RequestBody phonenumber,
            @Part("email") RequestBody email
    );
}
