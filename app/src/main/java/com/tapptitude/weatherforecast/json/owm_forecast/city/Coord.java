package com.tapptitude.weatherforecast.json.owm_forecast.city;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ambroziepaval on 10/5/16.
 */
public class Coord {

    @SerializedName("lon")
    public double longitude;

    @SerializedName("lat")
    public double latitude;
}
