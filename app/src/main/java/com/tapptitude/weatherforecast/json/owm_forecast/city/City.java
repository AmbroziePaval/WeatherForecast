package com.tapptitude.weatherforecast.json.owm_forecast.city;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ambroziepaval on 10/5/16.
 */
public class City {
    public int id;
    public String name;

    @SerializedName("coord")
    public Coord coordinates;

    public String country;
    public int population;
}
