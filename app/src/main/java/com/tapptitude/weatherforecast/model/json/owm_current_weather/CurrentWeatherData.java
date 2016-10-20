package com.tapptitude.weatherforecast.model.json.owm_current_weather;

import com.google.gson.annotations.SerializedName;
import com.tapptitude.weatherforecast.model.json.owm_forecast.city.Coord;
import com.tapptitude.weatherforecast.model.json.owm_forecast.list.Clouds;
import com.tapptitude.weatherforecast.model.json.owm_forecast.list.WeatherInfo;
import com.tapptitude.weatherforecast.model.json.owm_forecast.list.WeatherMain;
import com.tapptitude.weatherforecast.model.json.owm_forecast.list.Wind;

import java.util.List;

/**
 * Created by ambroziepaval on 10/14/16.
 */
public class CurrentWeatherData {
    @SerializedName("coord")
    public Coord coordinates;

    @SerializedName("sys")
    public Sys sys;

    @SerializedName("weather")
    public List<WeatherInfo> weather;

    @SerializedName("main")
    public WeatherMain main;

    @SerializedName("wind")
    public Wind wind;

    @SerializedName("rain")
    public Rain rain;

    @SerializedName("clouds")
    public Clouds clouds;

    @SerializedName("name")
    public String cityName;
}
