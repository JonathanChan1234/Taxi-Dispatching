package com.jonathan.taxidispatching.APIInterface;

import com.jonathan.taxidispatching.APIObject.PlaceResource;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleMapAPIInterface {
    @GET("place/nearbysearch/json")
    Call<PlaceResource> getNearbyPlace(@Query("language")String language,
                                       @Query("location") String location,
                                       @Query("radius") String radius,
                                       @Query("key") String key);
//    @GET("/directions/json")
//    Call<DirectionResource> getDirection(@Query("origin") String origin,
//                                         @Query("destination") String destination,
//                                         @Query("key") String key);
}
