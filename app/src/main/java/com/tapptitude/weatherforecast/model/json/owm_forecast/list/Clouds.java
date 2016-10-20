package com.tapptitude.weatherforecast.model.json.owm_forecast.list;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ambroziepaval on 10/5/16.
 */
public class Clouds implements Parcelable {
    public int all;

    protected Clouds(Parcel in) {
        all = in.readInt();
    }

    public static final Creator<Clouds> CREATOR = new Creator<Clouds>() {
        @Override
        public Clouds createFromParcel(Parcel in) {
            return new Clouds(in);
        }

        @Override
        public Clouds[] newArray(int size) {
            return new Clouds[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(all);
    }
}
