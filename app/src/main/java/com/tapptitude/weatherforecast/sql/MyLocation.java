package com.tapptitude.weatherforecast.sql;

/**
 * Created by ambroziepaval on 10/13/16.
 */
public class MyLocation {
    public long id;
    public String name;
    public String longitude;
    public String latitude;

    MyLocation(long id, String name, String longitude, String latitude) {
        this.id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
