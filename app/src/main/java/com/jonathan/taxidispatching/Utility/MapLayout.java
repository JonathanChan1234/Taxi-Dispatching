package com.jonathan.taxidispatching.Utility;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.ColorInt;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.jonathan.taxidispatching.R;

import java.lang.reflect.Field;

public class MapLayout extends RelativeLayout {
    public EditText fromText;
    public EditText toText;
    public Button requestButton, directionRequestButton;
    public PlaceAutocompleteFragment fragment;

    public MapLayout(Context context) {
        super(context);
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_maps, null);
        this.addView(view, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        RelativeLayout addressLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams addressLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addressLayout.setLayoutParams(addressLayoutParams);
        this.addView(addressLayout);

        fromText = new EditText(context);
        fromText.setId(0x1001);
        RelativeLayout.LayoutParams fromEditParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fromEditParams.topMargin = dp2px(10);
        fromEditParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        fromText.setLayoutParams(fromEditParams);
        fromText.setBackgroundResource(R.drawable.round_input);
        fromText.setTextSize(20);
        fromText.setTextColor(getResources().getColor(R.color.white));
        fromText.setHint("from ...");
        fromText.setSingleLine(true);
        fromText.setPadding(dp2px(20), dp2px(5), dp2px(20), dp2px(5));
        setCursorColor(fromText);
        addressLayout.addView(fromText);

        toText = new EditText(context);
        toText.setId(0x1002);
        RelativeLayout.LayoutParams toEditParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        toEditParams.addRule(RelativeLayout.BELOW, fromText.getId());
        toEditParams.topMargin = dp2px(5);
        toText.setLayoutParams(toEditParams);
        toText.setBackgroundResource(R.drawable.round_input);
        toText.setTextSize(20);
        toText.setTextColor(getResources().getColor(R.color.white));
        toText.setHint("to ...");
        toText.setSingleLine(true);
        toText.setPadding(dp2px(20), dp2px(5), dp2px(20), dp2px(5));
        setCursorColor(toText);
        addressLayout.addView(toText);

        requestButton = new Button(context);
        requestButton.setId(0x1003);
        RelativeLayout.LayoutParams requestButtonParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        requestButtonParams.topMargin = dp2px(5);
        requestButtonParams.addRule(RelativeLayout.BELOW, toText.getId());
        requestButton.setLayoutParams(requestButtonParams);
        requestButton.setBackgroundResource(R.drawable.round_button);
        requestButton.setTextSize(20);
        requestButton.setText("Make Taxis Call");
        requestButton.setGravity(Gravity.CENTER);
        requestButton.setPadding(dp2px(20), dp2px(5), dp2px(20), dp2px(5));
        addressLayout.addView(requestButton);

        directionRequestButton = new Button(context);
        directionRequestButton.setId(0x1004);
        RelativeLayout.LayoutParams directionRequestButtonParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        directionRequestButtonParams.topMargin = dp2px(5);
        directionRequestButtonParams.addRule(RelativeLayout.BELOW, requestButton.getId());
        directionRequestButton.setLayoutParams(directionRequestButtonParams);
        directionRequestButton.setBackgroundResource(R.drawable.round_button);
        directionRequestButton.setTextSize(20);
        directionRequestButton.setText("Make Taxis Call (Direction)");
        directionRequestButton.setGravity(Gravity.CENTER);
        directionRequestButton.setPadding(dp2px(20), dp2px(5), dp2px(20), dp2px(5));
        addressLayout.addView(directionRequestButton);
    }

    public int dp2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    //Using reflection to get the cursor drawable class
    public void setCursorColor(EditText view) {
        try {
            Field field = TextView.class.getDeclaredField("mCursorDrawableRes");
            field.setAccessible(true);
            field.set(view, R.drawable.white_cursor);
        } catch (Exception ignored) {

        }
    }
}
