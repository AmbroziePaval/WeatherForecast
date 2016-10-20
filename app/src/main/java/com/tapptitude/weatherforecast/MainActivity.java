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
import com.tapptitude.weatherforecast.retrofit.WeatherApiClient;
import com.tapptitude.weatherforecast.retrofit.WeatherApiInterface;
import com.tapptitude.weatherforecast.sql.MyLocation;
import com.tapptitude.weatherforecast.sql.PreviousLocationDbHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PICK_LOCATION_RESULT_CODE = 1;
    private static final int NOTIFICATION_ID = 2;
    static final String STATE_LATITUDE = "latitude";
    static final String STATE_LONGITUDE = "longitude";
    private PreviousLocationDbHelper mPreviousLocationDbHelper;
    private double mLongitude = 23.6006;
    private double mLatitude = 46.7595;

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
            mLongitude = savedInstanceState.getDouble(STATE_LONGITUDE);
            mLatitude = savedInstanceState.getDouble(STATE_LATITUDE);
        }

        loadOpenWeatherMapData();
        loadPresentWeatherData();
        mPreviousLocationDbHelper = new PreviousLocationDbHelper(this);

        setAutomaticWeatherUpdater();
    }

    private void setAutomaticWeatherUpdater() {
        mWeatherBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadOpenWeatherMapData();
                Toast.makeText(context, getString(R.string.main_weather_updated), Toast.LENGTH_SHORT).show();
            }
        };
        registerReceiver(mWeatherBroadcastReceiver, new IntentFilter("UPDATE_WEATHER"));
        UpdateWeatherBR.startUpdater(this);
    }

    @Override
    protected void onDestroy() {
        UpdateWeatherBR.cancelUpdater(this);
        try {
            unregisterReceiver(mWeatherBroadcastReceiver);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Weather broadcast receiver not registered");
        }
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putDouble(STATE_LATITUDE, mLatitude);
        outState.putDouble(STATE_LONGITUDE, mLongitude);
        super.onSaveInstanceState(outState);
    }

    protected void loadOpenWeatherMapData() {
        WeatherItemListFragment weatherItemListFragment = new WeatherItemListFragment();

        Bundle bundle = new Bundle();
        bundle.putDouble(WeatherItemListFragment.KEY_LOCATION_LATITUDE, mLatitude);
        bundle.putDouble(WeatherItemListFragment.KEY_LOCATION_LONGITUDE, mLongitude);
        weatherItemListFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.am_fl_frameLayout, weatherItemListFragment);
        transaction.commitAllowingStateLoss();
    }

    private void loadPresentWeatherData() {
        WeatherApiInterface weatherApiInterface = WeatherApiClient.getAPI();
        Call<CurrentWeatherData> call = weatherApiInterface.getCurrentWeatherData(mLatitude, mLongitude, "metric", getResources().getString(R.string.API_KEY));
        call.enqueue(new Callback<CurrentWeatherData>() {
            @Override
            public void onResponse(Call<CurrentWeatherData> call, Response<CurrentWeatherData> response) {
                CurrentWeatherData currentWeatherData = response.body();
                mPreviousLocationDbHelper.insertLocation(currentWeatherData.cityName, String.valueOf(mLongitude), String.valueOf(mLatitude));
                loadNotificationWeatherData(currentWeatherData);

                mTempTV.setText(getResources().getString(R.string.details_temperature_x, (int) Math.round(currentWeatherData.main.temp)));
                mTempMinValueTV.setText(getResources().getString(R.string.details_temperature_x, Math.round(currentWeatherData.main.tempMin)));
                mTempMaxValueTV.setText(getResources().getString(R.string.details_temperature_x, Math.round(currentWeatherData.main.tempMax)));
                mCityTV.setText(currentWeatherData.cityName);
                mCountryTV.setText(currentWeatherData.sys.country);
                mHumidityTV.setText(getResources().getString(R.string.details_humidity_x, currentWeatherData.main.humidity));
                mPressureTV.setText(getResources().getString(R.string.details_pressure_x, currentWeatherData.main.pressure));
                mWindSpeedTV.setText(getResources().getString(R.string.details_wind_speed_x, currentWeatherData.wind.speed));
                mWindDegTV.setText(String.valueOf(currentWeatherData.wind.degrees));

                Glide.with(getApplicationContext()).load(WeatherApiClient.getImageUrl(currentWeatherData.weather.get(0).icon))
                        .into(mWeatherIconIV);
            }

            @Override
            public void onFailure(Call<CurrentWeatherData> call, Throwable t) {
                Log.e(TAG, "OpenWeatherMap current data collection failed");
            }
        });
    }

    private void loadNotificationWeatherData(CurrentWeatherData currentWeatherData) {
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(this)
                .setContentTitle(getResources().getString(R.string.main_Notification_title_x, currentWeatherData.cityName, (int) Math.round(currentWeatherData.main.temp)))
                .setContentText(currentWeatherData.weather.get(0).description)
                .setSmallIcon(R.drawable.weather_forecast_icon)
                .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0))
                .setAutoCancel(true)
                .setOngoing(false)
                .build();

        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_LOCATION_RESULT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                this.mLatitude = data.getDoubleExtra(LocationPickerActivity.KEY_LOCATION_LATITUDE, 0);
                this.mLongitude = data.getDoubleExtra(LocationPickerActivity.KEY_LOCATION_LONGITUDE, 0);

                loadOpenWeatherMapData();
                loadPresentWeatherData();

                String toastText = getResources().getString(R.string.main_coordinates_info_x, mLatitude, mLongitude);
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
                bundle.putDouble(LocationPickerActivity.KEY_LOCATION_LONGITUDE, mLongitude);
                bundle.putDouble(LocationPickerActivity.KEY_LOCATION_LATITUDE, mLatitude);

                Intent pickLocationIntent = new Intent(v.getContext(), LocationPickerActivity.class);
                pickLocationIntent.putExtras(bundle);

                startActivityForResult(pickLocationIntent, PICK_LOCATION_RESULT_CODE);
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
                                mLatitude = Double.parseDouble(myLocation.latitude);
                                mLongitude = Double.parseDouble(myLocation.longitude);
                                loadOpenWeatherMapData();
                                loadPresentWeatherData();
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
