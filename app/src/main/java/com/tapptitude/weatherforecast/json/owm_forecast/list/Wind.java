package com.tapptitude.weatherforecast.json.owm_forecast.list;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ambroziepaval on 10/5/16.
 */
public class Wind implements Parcelable{
    public double speed;

    @SerializedName("deg")
    public double degrees;

    protected Wind(Parcel in) {
        speed = in.readDouble();
        degrees = in.readDouble();
    }

    public static final Creator<Wind> CREATOR = new Creator<Wind>() {
        @Override
        public Wind createFromParcel(Parcel in) {
            return new Wind(in);
        }

        @Override
        public Wind[] newArray(int size) {
            return new Wind[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(speed);
        dest.writeDouble(degrees);
    }
}
