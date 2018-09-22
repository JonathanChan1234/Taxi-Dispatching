package com.jonathan.taxidispatching.Utility;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

public class ErrorMessageUtility {
    public static Response.ErrorListener getNetworkErrorListener(final Context context) {
        return new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Network connection issue.\nPlease check your Internet connection", Toast.LENGTH_SHORT).show();
            }
        };
    }

}
