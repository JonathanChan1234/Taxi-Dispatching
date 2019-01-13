package com.jonathan.taxidispatching.Event;

import com.jonathan.taxidispatching.Model.Driver;
import com.jonathan.taxidispatching.Model.Transcation;

public class DriverResponseEvent {
    private Transcation.Data transcation;
    private Driver driver;
    private int response;

    public DriverResponseEvent(Transcation.Data transcation, Driver driver, int response) {
        this.driver = driver;
        this.transcation = transcation;
        this.response = response;
    }

    public Driver getDriver() {
        return driver;
    }

    public Transcation.Data getTranscation() {
        return transcation;
    }

    public int getResponse() {
        return response;
    }
}
