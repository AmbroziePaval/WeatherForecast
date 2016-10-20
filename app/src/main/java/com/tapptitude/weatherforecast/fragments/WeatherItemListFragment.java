package com.tapptitude.weatherforecast.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tapptitude.weatherforecast.R;
import com.tapptitude.weatherforecast.activities.WeatherItemDetailActivity;
import com.tapptitude.weatherforecast.adapters.WeatherContentAdapter;
import com.tapptitude.weatherforecast.json.owm_forecast.ForecastWeatherData;
import com.tapptitude.weatherforecast.json.owm_forecast.list.WeatherData;
import com.tapptitude.weatherforecast.retrofit.WeatherApiClient;
import com.tapptitude.weatherforecast.retrofit.WeatherApiInterface;
import com.tapptitude.weatherforecast.utils.WeatherDateUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ambroziepaval on 10/5/16.
 */
public class WeatherItemListFragment extends Fragment implements WeatherContentAdapter.MyAdapterListener {
    private static final String TAG = WeatherItemListFragment.class.getSimpleName();
    @BindView(R.id.wil_rv_weatherItemList)
    RecyclerView mWeatherForecastRV;

    public static final String KEY_LOCATION_LATITUDE = "KEY_LOCATION_LATITUDE";
    public static final String KEY_LOCATION_LONGITUDE = "KEY_LOCATION_LONGITUDE";

    private List<WeatherData> mWeatherDataArrayList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_item_list, container, false);

        readBundleData();
        ButterKnife.bind(this, view);
        return view;
    }

    private void readBundleData() {
        Bundle bundle = getArguments();
        double latitude = bundle.getDouble(KEY_LOCATION_LATITUDE);
        double longitude = bundle.getDouble(KEY_LOCATION_LONGITUDE);
        loadOpenWeatherMapData(latitude, longitude);
    }

    private void loadOpenWeatherMapData(double latitude, double longitude){
        WeatherApiInterface weatherApiInterface = WeatherApiClient.getAPI();
        Call<ForecastWeatherData> call = weatherApiInterface.getOpenWeatherData(latitude, longitude, "metric", getResources().getString(R.string.API_KEY));
        call.enqueue(new Callback<ForecastWeatherData>() {
            @Override
            public void onResponse(Call<ForecastWeatherData> call, Response<ForecastWeatherData> response) {
                ForecastWeatherData forecastWeatherData = response.body();

                mWeatherDataArrayList = forecastWeatherData.list;
                setUpRVContentAdapter(getDisplayWeatherData(mWeatherDataArrayList));
                Log.i(TAG, "OpenWeatherMap data received");
            }

            @Override
            public void onFailure(Call<ForecastWeatherData> call, Throwable t) {
                setUpRVContentAdapter(new ArrayList<WeatherData>());
                Log.e(TAG, "OpenWeatherMap data collection failed");
            }
        });
    }

    private void setUpRVContentAdapter(ArrayList<WeatherData> displayWeatherList) {
        WeatherContentAdapter weatherAdapter = new WeatherContentAdapter(getContext(), displayWeatherList);
        weatherAdapter.setMyAdapterListener(this);
        mWeatherForecastRV.setAdapter(weatherAdapter);
        mWeatherForecastRV.setLayoutManager(new LinearLayoutManager(getContext()));
        mWeatherForecastRV.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onWeatherCardItemClick(WeatherData weatherData) {
        Bundle bundle = getDetailsBundle(weatherData, getWeatherDataFromSameDay(weatherData));

        final int twoPaneLayout = getResources().getInteger(R.integer.is_tablet);
        if (twoPaneLayout == 1) {
            displayWeatherDetailsFragment(bundle);
        } else {
            Intent intent = new Intent(getContext(), WeatherItemDetailActivity.class);
            intent.putExtras(bundle);
            getContext().startActivity(intent);
        }
    }

    private void displayWeatherDetailsFragment(Bundle bundle) {
        WeatherItemDetailFragment detailsFragment = new WeatherItemDetailFragment();
        detailsFragment.setArguments(bundle);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.wil_fl_weather_item_detail_container, detailsFragment);
        transaction.commit();
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

    private ArrayList<WeatherData> getDisplayWeatherData(List<WeatherData> weatherDataArrayList) {
        boolean todayIncluded = false;
        ArrayList<WeatherData> displayWeather = new ArrayList<>();
        for (int i = 1; i < weatherDataArrayList.size(); i++) {
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
}
