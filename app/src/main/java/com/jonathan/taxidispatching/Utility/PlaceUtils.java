package com.jonathan.taxidispatching.Utility;

import org.json.JSONException;
import org.json.JSONObject;

public class PlaceUtils {
    public static String place_api = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?language=zh-TW&location=%s&radius=%s&key=%s";

    public static JSONObject getRouteInfo(JSONObject object) {
        try {
            String distance = object.getJSONArray("routes").
                    getJSONObject(0).
                    getJSONArray("legs").
                    getJSONObject(0).
                    getJSONObject("distance").
                    getString("text");
            String durationText = object.getJSONArray("routes").
                    getJSONObject(0).
                    getJSONArray("legs").
                    getJSONObject(0).
                    getJSONObject("duration").
                    getString("value");
            int durationTime = Integer.parseInt(durationText)/60;
            JSONObject json = new JSONObject();
            json.put("distance", distance);
            json.put("duration", durationTime);
            return json;
        } catch(JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
