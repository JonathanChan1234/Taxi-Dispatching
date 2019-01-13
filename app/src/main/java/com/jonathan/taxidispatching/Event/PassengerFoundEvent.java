package com.jonathan.taxidispatching.Event;

import com.jonathan.taxidispatching.Model.Driver;
import com.jonathan.taxidispatching.Model.Transcation;

public class PassengerFoundEvent {
    private Transcation.Data transcation;
    private Driver driver;

    public PassengerFoundEvent(Transcation.Data transcation, Driver driver) {
        this.driver = driver;
        this.transcation = transcation;
    }

    public Driver getDriver() {
        return driver;
    }

    public Transcation.Data getTranscation() {
        return transcation;
    }
}
