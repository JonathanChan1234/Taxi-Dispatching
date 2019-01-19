package com.jonathan.taxidispatching.constants;

public class Constants {
    //API for http request
    public final static String local_ip_address = "192.168.86.137";
    public final static String google_map_api_key = "AIzaSyBwJyQDS_1ZZfic_OLFdB0q7UZC11B9vw4";

    //GPS Prompted
    public static final int REQUEST_CHECK_SETTINGS = 100;

    //Socket Event
    public static final String passenger_found_event = "passengerFound";
    public static final String driver_found_event = "driverFound";

    //Check account status
    public static final int NOT_LOGGED_IN = 100;
    public static final int LOGGED_IN_DRIVER = 200;
    public static final int LOGGED_IN_PASSENGER = 201;
    public static final int LOGGED_IN_DRIVER_TAXI = 300;
    public static final int LOGGED_IN_START_TRANSACTION_PASSENGER = 301;
    public static final int TRANSACTION_ONGOING_PASSENGER = 400;
    public static final int TRANSACTION_ONGOING_DRIVER = 401;
    public static final String TRANSCATION_INVITATION = "transcationInvitation";
    public static final String LOCATION_UPDATE = "locationUpdate";
}
