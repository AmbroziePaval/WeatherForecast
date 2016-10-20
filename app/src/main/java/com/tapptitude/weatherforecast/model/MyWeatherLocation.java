package com.tapptitude.weatherforecast.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ambroziepaval on 10/20/16.
 */

public class MyWeatherLocation extends RealmObject{
    @PrimaryKey
    private String cityName;
    private double longitude;
    private double latitude;

    public MyWeatherLocation() {
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
