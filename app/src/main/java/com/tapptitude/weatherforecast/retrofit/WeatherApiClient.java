package com.tapptitude.weatherforecast.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ambroziepaval on 10/5/16.
 */
public class WeatherApiClient {
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private static final String IMG_URL = "http://download.spinetix.com/content/widgets/icons/weather/";
    private static Retrofit retrofit = null;

    private static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static WeatherApiInterface getAPI() {
        return getClient().create(WeatherApiInterface.class);
    }

    public static String getImageUrl(String weatherIconId) {
        return IMG_URL + weatherIconId + ".png";
    }
}
