package com.tapptitude.weatherforecast.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tapptitude.weatherforecast.R;
import com.tapptitude.weatherforecast.activities.WeatherItemDetailActivity;
import com.tapptitude.weatherforecast.adapters.WeatherContentAdapter;
import com.tapptitude.weatherforecast.json.owm_forecast.list.WeatherData;
import com.tapptitude.weatherforecast.utils.WeatherDateUtils;

import java.util.ArrayList;

/**
 * Created by ambroziepaval on 10/5/16.
 */
public class WeatherItemListFragment extends Fragment implements WeatherContentAdapter.MyAdapterListener {
    private FragmentManager mFragmentManager;
    RecyclerView mWeatherForecastRV;

    public static final String KEY_ALL_WEATHER_DATA = "KEY_ALL_WEATHER_DATA";
    private ArrayList<WeatherData> mWeatherDataArrayList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_item_list, container, false);

        Bundle bundle = getArguments();
        mWeatherDataArrayList = bundle.getParcelableArrayList(KEY_ALL_WEATHER_DATA);
        ArrayList<WeatherData> displayWeatherList = getDisplayWeatherData();

        mWeatherForecastRV = (RecyclerView) view.findViewById(R.id.wil_rv_weatherItemList);
        mFragmentManager = getFragmentManager();
        WeatherContentAdapter weatherAdapter = new WeatherContentAdapter(getContext(), displayWeatherList);
        weatherAdapter.setMyAdapterListener(this);
        mWeatherForecastRV.setAdapter(weatherAdapter);
        mWeatherForecastRV.setLayoutManager(new LinearLayoutManager(getContext()));
        mWeatherForecastRV.setItemAnimator(new DefaultItemAnimator());

        return view;
    }

    private ArrayList<WeatherData> getDisplayWeatherData() {
        boolean todayIncluded = false;
        ArrayList<WeatherData> displayWeather = new ArrayList<>();
        for (int i = 1; i < mWeatherDataArrayList.size(); i++) {
            WeatherData weatherData = mWeatherDataArrayList.get(i);
            if (WeatherDateUtils.isNoonWeatherData(weatherData.timeOfCalculation)) {
                if (WeatherDateUtils.isTodayWeatherData(weatherData.timeOfCalculation)) {
                    todayIncluded = true;
                }
                displayWeather.add(weatherData);
            }
        }
        if (!todayIncluded) {
            displayWeather.add(0, mWeatherDataArrayList.get(0));
        }
        return displayWeather;
    }

    @Override
    public void onWeatherCardItemClick(WeatherData weatherData) {
        final String twoPane = getResources().getString(R.string.tablet);

        Bundle bundle = getDetailsBundle(weatherData, getWeatherDataFromSameDay(weatherData));

        if (twoPane.equals("true")) {
            displayWeatherDetailsFragment(bundle);
        } else {
            Intent intent = new Intent(getContext(), WeatherItemDetailActivity.class);
            intent.putExtras(bundle);
            getContext().startActivity(intent);
        }
    }

    @Override
    public ArrayList<WeatherData> getWeatherDataFromSameDay(WeatherData day) {
        ArrayList<WeatherData> dataArrayList = new ArrayList<>();
        for (WeatherData weatherData : mWeatherDataArrayList) {
            if (WeatherDateUtils.fromSameDay(day.timeOfCalculation, weatherData.timeOfCalculation)) {
                dataArrayList.add(weatherData);
            }
        }
        return dataArrayList;
    }

    private Bundle getDetailsBundle(WeatherData weatherData, ArrayList<WeatherData> weatherDataListFromSameDay) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(WeatherItemDetailActivity.KEY_DETAILS_WEATHER, weatherData);
        bundle.putParcelableArrayList(WeatherItemDetailActivity.KEY_DETAILS_WEATHER_GRAPH_LIST, weatherDataListFromSameDay);
        return bundle;
    }

    private void displayWeatherDetailsFragment(Bundle bundle) {
        WeatherItemDetailFragment detailsFragment = new WeatherItemDetailFragment();
        detailsFragment.setArguments(bundle);

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.wil_fl_weather_item_detail_container, detailsFragment);
        transaction.commit();
    }
}
