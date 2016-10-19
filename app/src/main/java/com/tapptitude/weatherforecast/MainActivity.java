package com.tapptitude.weatherforecast;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tapptitude.weatherforecast.activities.LocationPickerActivity;
import com.tapptitude.weatherforecast.fragments.WeatherItemListFragment;
import com.tapptitude.weatherforecast.json.owm_current_weather.CurrentWeatherData;
import com.tapptitude.weatherforecast.json.owm_forecast.ForecastWeatherData;
import com.tapptitude.weatherforecast.json.owm_forecast.list.WeatherData;
import com.tapptitude.weatherforecast.retrofit.WeatherApiClient;
import com.tapptitude.weatherforecast.retrofit.WeatherApiInterface;
import com.tapptitude.weatherforecast.sql.MyLocation;
import com.tapptitude.weatherforecast.sql.PreviousLocationDbHelper;
import com.tapptitude.weatherforecast.utils.WeatherDateUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PICK_LOCATION_CODE = 1;
    private static final int NOTIFICATION_ID = 2;
    static final String STATE_LATITUDE = "latitude";
    static final String STATE_LONGITUDE = "longitude";
    private CurrentWeatherData mCurrentWeatherData;
    private ForecastWeatherData mForecastWeatherData;
    private WeatherItemListFragment mWeatherItemListFragment;
    private PreviousLocationDbHelper mPreviousLocationDbHelper;
    private String mLongitude = "23.6006";
    private String mLatitude = "46.7595";

    @BindView(R.id.am_b_refresh)
    Button mRefreshButton;
    @BindView(R.id.am_b_previous_location)
    Button mPreviousLocationButton;
    @BindView(R.id.am_fab)
    FloatingActionButton mChooseLocationFAB;

    @BindView(R.id.am_tv_temp)
    TextView mTempTV;
    @BindView(R.id.am_tv_tempMinValue)
    TextView mTempMinValueTV;
    @BindView(R.id.am_tv_tempMaxValue)
    TextView mTempMaxValueTV;
    @BindView(R.id.am_tv_city)
    TextView mCityTV;
    @BindView(R.id.am_tv_country)
    TextView mCountryTV;
    @BindView(R.id.am_iv_weather_icon)
    ImageView mWeatherIconIV;
    @BindView(R.id.am_tv_humidity)
    TextView mHumidityTV;
    @BindView(R.id.am_tv_pressure)
    TextView mPressureTV;
    @BindView(R.id.am_tv_wind_speed)
    TextView mWindSpeedTV;
    @BindView(R.id.am_tv_wind_deg)
    TextView mWindDegTV;

    private PopupMenu mLocationPopupMenu;
    private BroadcastReceiver mWeatherBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setOnClickListeners();

        if (savedInstanceState != null) {
            mLongitude = savedInstanceState.getString(STATE_LONGITUDE);
            mLatitude = savedInstanceState.getString(STATE_LATITUDE);
        }

        loadOpenWeatherMapData();
        mPreviousLocationDbHelper = new PreviousLocationDbHelper(this);

        setAutomaticWeatherUpdater();
    }

    private void setAutomaticWeatherUpdater() {
        UpdateWeatherBR.startUpdater(this);
        registerReceiver(mWeatherBroadcastReceiver, new IntentFilter("UPDATE_WEATHER"));
        mWeatherBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadOpenWeatherMapData();
                Toast.makeText(context, getString(R.string.main_weather_updated), Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    protected void onDestroy() {
        UpdateWeatherBR.cancelUpdater(this);
        unregisterReceiver(mWeatherBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_LATITUDE, mLatitude);
        outState.putString(STATE_LONGITUDE, mLongitude);
        super.onSaveInstanceState(outState);
    }

    protected void loadOpenWeatherMapData() {
        WeatherApiInterface weatherApiInterface = WeatherApiClient.getClient().create(WeatherApiInterface.class);
        Call<ForecastWeatherData> call = weatherApiInterface.getOpenWeatherData(Double.parseDouble(mLatitude), Double.parseDouble(mLongitude), "metric", getResources().getString(R.string.API_KEY));
        call.enqueue(new Callback<ForecastWeatherData>() {
            @Override
            public void onResponse(Call<ForecastWeatherData> call, Response<ForecastWeatherData> response) {
                mForecastWeatherData = response.body();
                loadPresentWeatherData();
                mPreviousLocationDbHelper.insertLocation(mForecastWeatherData.city.name, mLongitude, mLatitude);

                mWeatherItemListFragment = new WeatherItemListFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(WeatherItemListFragment.KEY_ALL_WEATHER_DATA, (ArrayList<WeatherData>) mForecastWeatherData.list);
                bundle.putParcelableArrayList(WeatherItemListFragment.KEY_DISPLAY_WEATHER_DATA, getDisplayWeatherData());
                mWeatherItemListFragment.setArguments(bundle);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.am_fl_frameLayout, mWeatherItemListFragment);
                transaction.commitAllowingStateLoss();

                Log.i(TAG, "OpenWeatherMap data received");
            }

            @Override
            public void onFailure(Call<ForecastWeatherData> call, Throwable t) {
                Log.e(TAG, "OpenWeatherMap data collection failed");
            }

            private ArrayList<WeatherData> getDisplayWeatherData() {
                boolean todayIncluded = false;
                ArrayList<WeatherData> displayWeather = new ArrayList<>();
                for (int i = 1; i < mForecastWeatherData.list.size(); i++) {
                    WeatherData weatherData = mForecastWeatherData.list.get(i);
                    if (WeatherDateUtils.isNoonWeatherData(weatherData.timeOfCalculation)) {
                        if (WeatherDateUtils.isTodayWeatherData(weatherData.timeOfCalculation)) {
                            todayIncluded = true;
                        }
                        displayWeather.add(weatherData);
                    }
                }
                if (!todayIncluded) {
                    displayWeather.add(0, mForecastWeatherData.list.get(0));
                }
                return displayWeather;
            }
        });
    }

    private void loadNotificationWeatherData() {
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(this)
                .setContentTitle(mForecastWeatherData.city.name + " " + (int) mCurrentWeatherData.main.temp + "°C")
                .setContentText(mCurrentWeatherData.weather.get(0).description)
                .setSmallIcon(R.drawable.weather_small_icon)
                .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0))
                .setAutoCancel(true)
                .setOngoing(true)
                .build();

        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void loadPresentWeatherData() {
        WeatherApiInterface weatherApiInterface = WeatherApiClient.getClient().create(WeatherApiInterface.class);
        Call<CurrentWeatherData> call = weatherApiInterface.getCurrentWeatherData(Double.parseDouble(mLatitude), Double.parseDouble(mLongitude), "metric", getResources().getString(R.string.API_KEY));
        call.enqueue(new Callback<CurrentWeatherData>() {
            @Override
            public void onResponse(Call<CurrentWeatherData> call, Response<CurrentWeatherData> response) {
                mCurrentWeatherData = response.body();
                loadNotificationWeatherData();

                mTempTV.setText(getResources().getString(R.string.details_temperature_x, (int) Math.round(mCurrentWeatherData.main.temp)));
                mTempMinValueTV.setText(getResources().getString(R.string.details_temperature_x, Math.round(mCurrentWeatherData.main.tempMin)));
                mTempMaxValueTV.setText(getResources().getString(R.string.details_temperature_x, Math.round(mCurrentWeatherData.main.tempMax)));
                mCityTV.setText(mForecastWeatherData.city.name);
                mCountryTV.setText(mForecastWeatherData.city.country);
                mHumidityTV.setText(getResources().getString(R.string.details_humidity_x, mCurrentWeatherData.main.humidity));
                mPressureTV.setText(getResources().getString(R.string.details_pressure_x, mCurrentWeatherData.main.pressure));
                mWindSpeedTV.setText(getResources().getString(R.string.details_wind_speed_x, mCurrentWeatherData.wind.speed));
                mWindDegTV.setText(String.valueOf(mCurrentWeatherData.wind.degrees));

                Glide.with(getApplicationContext()).load(WeatherApiClient.getImageUrl(mCurrentWeatherData.weather.get(0).icon))
                        .into(mWeatherIconIV);
            }

            @Override
            public void onFailure(Call<CurrentWeatherData> call, Throwable t) {
                Log.e(TAG, "OpenWeatherMap current data collection failed");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_LOCATION_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                double latitude = data.getDoubleExtra(LocationPickerActivity.KEY_LOCATION_LATITUDE, 0);
                double longitude = data.getDoubleExtra(LocationPickerActivity.KEY_LOCATION_LONGITUDE, 0);
                this.mLatitude = String.valueOf(latitude);
                this.mLongitude = String.valueOf(longitude);

                loadOpenWeatherMapData();

                String toastText = getResources().getString(R.string.main_coordinates_info_x, latitude, longitude);
                Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.main_location_selection_failed), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setOnClickListeners() {
        mChooseLocationFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(LocationPickerActivity.KEY_LOCATION_LONGITUDE, mLongitude);
                bundle.putString(LocationPickerActivity.KEY_LOCATION_LATITUDE, mLatitude);

                Intent pickLocationIntent = new Intent(v.getContext(), LocationPickerActivity.class);
                pickLocationIntent.putExtras(bundle);

                startActivityForResult(pickLocationIntent, PICK_LOCATION_CODE);
            }
        });


        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadOpenWeatherMapData();
                Toast.makeText(v.getContext(), getString(R.string.main_weather_updated), Toast.LENGTH_SHORT).show();
            }
        });

        mPreviousLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationPopupMenu = new PopupMenu(v.getContext(), mPreviousLocationButton);
                mLocationPopupMenu.getMenuInflater().inflate(R.menu.popup_menu_location, mLocationPopupMenu.getMenu());
                mLocationPopupMenu.getMenu().clear();

                final List<MyLocation> previousLocations = mPreviousLocationDbHelper.getPreviousLocations();
                for (MyLocation myLocation : previousLocations) {
                    mLocationPopupMenu.getMenu().add(Menu.NONE, (int) myLocation.id, Menu.NONE, myLocation.name);
                }

                mLocationPopupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                    @Override
                    public void onDismiss(PopupMenu menu) {

                    }
                });

                mLocationPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(MainActivity.this, getString(R.string.main_selected_location_x, item.getTitle()), Toast.LENGTH_SHORT).show();
                        for (MyLocation myLocation : previousLocations) {
                            if (myLocation.name.equals(item.getTitle())) {
                                mLatitude = myLocation.latitude;
                                mLongitude = myLocation.longitude;
                                loadOpenWeatherMapData();
                            }
                        }
                        return true;
                    }
                });
                mLocationPopupMenu.show();
            }
        });
    }
}
