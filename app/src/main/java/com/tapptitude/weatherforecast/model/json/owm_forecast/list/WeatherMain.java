package com.tapptitude.weatherforecast.model.json.owm_forecast.list;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ambroziepaval on 10/5/16.
 */
public class WeatherMain implements Parcelable{
    public double temp;

    @SerializedName("temp_min")
    public double tempMin;

    @SerializedName("temp_max")
    public double tempMax;

    public double pressure;

    @SerializedName("sea_level")
    public double seaLevelPressure;

    @SerializedName("grnd_level")
    public double groundLevelPressure;

    public int humidity;

    protected WeatherMain(Parcel in) {
        temp = in.readDouble();
        tempMin = in.readDouble();
        tempMax = in.readDouble();
        pressure = in.readDouble();
        seaLevelPressure = in.readDouble();
        groundLevelPressure = in.readDouble();
        humidity = in.readInt();
    }

    public static final Creator<WeatherMain> CREATOR = new Creator<WeatherMain>() {
        @Override
        public WeatherMain createFromParcel(Parcel in) {
            return new WeatherMain(in);
        }

        @Override
        public WeatherMain[] newArray(int size) {
            return new WeatherMain[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(temp);
        dest.writeDouble(tempMin);
        dest.writeDouble(tempMax);
        dest.writeDouble(pressure);
        dest.writeDouble(seaLevelPressure);
        dest.writeDouble(groundLevelPressure);
        dest.writeInt(humidity);
    }
}
