package com.tapptitude.weatherforecast.retrofit;


import com.tapptitude.weatherforecast.model.json.owm_current_weather.CurrentWeatherData;
import com.tapptitude.weatherforecast.model.json.owm_forecast.ForecastWeatherData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by ambroziepaval on 10/5/16.
 */
public interface WeatherApiInterface {
    @GET("forecast")
    Call<ForecastWeatherData> getOpenWeatherData(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("units") String units,
            @Query("APPID") String apiKey);

    @GET("weather")
    Call<CurrentWeatherData> getCurrentWeatherData(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("units") String units,
            @Query("APPID") String apiKey);
}
