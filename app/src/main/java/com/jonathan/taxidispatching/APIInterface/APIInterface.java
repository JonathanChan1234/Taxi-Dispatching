package com.jonathan.taxidispatching.APIInterface;

import android.support.annotation.Nullable;

import com.jonathan.taxidispatching.Model.AccountResponse;
import com.jonathan.taxidispatching.Model.StandardResponse;
import com.jonathan.taxidispatching.Model.Taxis;
import com.jonathan.taxidispatching.Model.Transcation;

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

    @FormUrlEncoded
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

    @FormUrlEncoded
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

    @FormUrlEncoded
    @POST("taxi/checkDuplicate")
    Call<StandardResponse> checkDuplicate(
            @Field("platenumber") String platenumber
    );

    @FormUrlEncoded
    @POST("taxi/register")
    Call<StandardResponse> registerNewTaxi(
            @Field("platenumber") String platenumber,
            @Field("password") String password,
            @Field("id") Integer id
    );

    @FormUrlEncoded
    @POST("taxi/signIn")
    Call<StandardResponse> signInTaxi(
            @Field("platenumber") String platenumber,
            @Field("password") String password,
            @Field("id") Integer id
    );

    @FormUrlEncoded
    @POST("taxi/checkOwnerTaxi")
    Call<Taxis> getTaxiList(
            @Field("id") Integer id
    );

    @FormUrlEncoded
    @POST("taxi/deleteAccount")
    Call<StandardResponse> deleteTaxiAccount(
            @Field("password") String password,
            @Field("platenumber") String platenumber
    );
}
