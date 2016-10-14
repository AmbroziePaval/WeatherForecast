package com.tapptitude.weatherforecast.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tapptitude.weatherforecast.R;
import com.tapptitude.weatherforecast.adapters.WeatherContentAdapter;
import com.tapptitude.weatherforecast.json.owm_forecast.list.WeatherData;

import java.util.ArrayList;

/**
 * Created by ambroziepaval on 10/5/16.
 */
public class WeatherItemListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_item_list, container, false);

        RecyclerView mWeatherForecastRV = (RecyclerView) view.findViewById(R.id.wil_rv_weatherItemList);

        Bundle bundle = getArguments();
        ArrayList<WeatherData> mWeatherDataArrayList = bundle.getParcelableArrayList("all_weather_data");
        ArrayList<WeatherData> mDisplayWeatherList = bundle.getParcelableArrayList("display_weather");

        WeatherContentAdapter weatherAdapter = new WeatherContentAdapter(getContext(), getFragmentManager(), mWeatherDataArrayList, mDisplayWeatherList);
        mWeatherForecastRV.setAdapter(weatherAdapter);
        mWeatherForecastRV.setLayoutManager(new LinearLayoutManager(getContext()));
        mWeatherForecastRV.setItemAnimator(new DefaultItemAnimator());

        return view;
    }
}