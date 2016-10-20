package com.tapptitude.weatherforecast.model.json;

import com.google.gson.Gson;
import com.tapptitude.weatherforecast.model.json.owm_forecast.ForecastWeatherData;

/**
 * Created by ambroziepaval on 10/5/16.
 */
public class OpenWeatherApiGsonParser {
    public ForecastWeatherData parseOpenWeatherData(String json){
        Gson gson = new Gson();
        ForecastWeatherData forecastWeatherData = gson.fromJson(json, ForecastWeatherData.class);

        return forecastWeatherData;
    }
}
