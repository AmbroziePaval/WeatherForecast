package com.tapptitude.weatherforecast.json.owm_current_weather;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ambroziepaval on 10/14/16.
 */
public class Sys {
    @SerializedName("country")
    public String country;

    @SerializedName("sunrise")
    public int sunrise;

    @SerializedName("sunset")
    public int sunset;
}
