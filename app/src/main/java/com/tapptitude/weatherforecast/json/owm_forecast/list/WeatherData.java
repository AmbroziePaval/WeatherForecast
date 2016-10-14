package com.tapptitude.weatherforecast.json.owm_forecast.list;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ambroziepaval on 10/5/16.
 */
public class WeatherData implements Parcelable{
    @SerializedName("dt")
    public int dataForcasted;

    @SerializedName("main")
    public WeatherMain main;

    @SerializedName("weather")
    public List<WeatherInfo> weather;

    @SerializedName("clouds")
    public Clouds clouds;

    @SerializedName("wind")
    public Wind wind;

    @SerializedName("dt_txt")
    public String timeOfCalculation;

    protected WeatherData(Parcel in) {
        dataForcasted = in.readInt();
        main = in.readParcelable(WeatherMain.class.getClassLoader());
        weather = new ArrayList<>();
        in.readTypedList(weather, WeatherInfo.CREATOR);
        clouds = in.readParcelable(Clouds.class.getClassLoader());
        wind = in.readParcelable(Wind.class.getClassLoader());
        timeOfCalculation = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(dataForcasted);
        dest.writeParcelable(main, flags);
        dest.writeTypedList(weather);
        dest.writeParcelable(clouds, flags);
        dest.writeParcelable(wind, flags);
        dest.writeString(timeOfCalculation);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WeatherData> CREATOR = new Creator<WeatherData>() {
        @Override
        public WeatherData createFromParcel(Parcel in) {
            return new WeatherData(in);
        }

        @Override
        public WeatherData[] newArray(int size) {
            return new WeatherData[size];
        }
    };
}
