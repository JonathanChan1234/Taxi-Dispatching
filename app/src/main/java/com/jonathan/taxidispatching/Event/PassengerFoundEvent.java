package com.jonathan.taxidispatching.Event;

import com.jonathan.taxidispatching.Model.Driver;
import com.jonathan.taxidispatching.Model.Transcation;

public class PassengerFoundEvent {
    private Transcation.Data transcation;
    private Driver driver;
    private String data;

    public PassengerFoundEvent(Transcation.Data transcation, Driver driver, String data) {
        this.driver = driver;
        this.transcation = transcation;
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
}
