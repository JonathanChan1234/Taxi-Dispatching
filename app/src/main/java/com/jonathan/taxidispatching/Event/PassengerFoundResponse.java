package com.jonathan.taxidispatching.Event;

import com.jonathan.taxidispatching.Model.Driver;
import com.jonathan.taxidispatching.Model.Transcation;

public class PassengerFoundResponse {
    private Transcation.Data transcation;
    private Driver driver;
    private int response;
    private String data;

    public PassengerFoundResponse(Transcation.Data transcation, Driver driver, String data, int response) {
        this.driver = driver;
        this.transcation = transcation;
        this.response = response;
        this.data = data;
    }

    public Driver getDriver() {
        return driver;
    }

    public Transcation.Data getTranscation() {
        return transcation;
    }

    public String getData() {
        return data;
    }

    public int getResponse() {
        return response;
    }

}
