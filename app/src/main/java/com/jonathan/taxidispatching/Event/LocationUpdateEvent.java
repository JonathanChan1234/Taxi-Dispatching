package com.jonathan.taxidispatching.Event;

import com.jonathan.taxidispatching.Model.DriverLocation;

public class LocationUpdateEvent {
    private DriverLocation location;
    public LocationUpdateEvent(DriverLocation location) {
        this.location = location;
    }

    public DriverLocation getLocation() {
        return location;
    }
}
