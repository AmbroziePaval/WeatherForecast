package com.tapptitude.weatherforecast.json.owm_forecast.list;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ambroziepaval on 10/5/16.
 */
public class WeatherInfo implements Parcelable
{
    public int id;
    public String main;
    public String description;
    public String icon;

    protected WeatherInfo(Parcel in) {
        id = in.readInt();
        main = in.readString();
        description = in.readString();
        icon = in.readString();
    }

    public static final Creator<WeatherInfo> CREATOR = new Creator<WeatherInfo>() {
        @Override
        public WeatherInfo createFromParcel(Parcel in) {
            return new WeatherInfo(in);
        }

        @Override
        public WeatherInfo[] newArray(int size) {
            return new WeatherInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(main);
        dest.writeString(description);
        dest.writeString(icon);
    }
}
