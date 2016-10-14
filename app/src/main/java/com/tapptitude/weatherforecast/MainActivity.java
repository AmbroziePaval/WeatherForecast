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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PICK_LOCATION_CODE = 1;
    private static final int NOTIFICATION_ID = 2;
    static final String STATE_LATITUDE = "latitude";
    static final String STATE_LONGITUDE = "longitude";
    private WeatherItemListFragment mWeatherItemListFragment;
    private CurrentWeatherData mCurrentWeatherData;
    private ForecastWeatherData mForecastWeatherData;
    private FloatingActionButton mChooseLocationFAB;
    private String mLongitude = "23.6006";
    private String mLatitude = "46.7595";
    private Button mRefreshButton;
    private Button mPreviousLocationButton;
    private PopupMenu mLocationPopupMenu;
    private PreviousLocationDbHelper mPreviousLocationDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChooseLocationFAB = (FloatingActionButton) findViewById(R.id.am_fab);
        mRefreshButton = (Button) findViewById(R.id.am_b_refresh);
        mPreviousLocationButton = (Button) findViewById(R.id.am_b_previous_location);
        setOnClickListeners();

        if (savedInstanceState != null) {
            mLongitude = savedInstanceState.getString(STATE_LONGITUDE);
            mLatitude = savedInstanceState.getString(STATE_LATITUDE);
        }

        loadOpenWeatherMapData();
        mPreviousLocationDbHelper = new PreviousLocationDbHelper(this);

        UpdateWeatherBR.startUpdater(this);
        registerReceiver(broadcastReceiver, new IntentFilter("UPDATE_WEATHER"));
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadOpenWeatherMapData();
            Toast.makeText(context, "Weather updated!", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onDestroy() {
        UpdateWeatherBR.cancelUpdater(this);
        unregisterReceiver(broadcastReceiver);
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
                bundle.putParcelableArrayList("all_weather_data", (ArrayList<WeatherData>) mForecastWeatherData.list);
                bundle.putParcelableArrayList("display_weather", getDisplayWeatherData());
                mWeatherItemListFragment.setArguments(bundle);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.am_fl_frameLayout, mWeatherItemListFragment);
                transaction.commitAllowingStateLoss();

                Log.d(TAG, "OpenWeatherMap data received");
            }

            @Override
            public void onFailure(Call<ForecastWeatherData> call, Throwable t) {
                Log.d(TAG, "OpenWeatherMap data collection failed");
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
        final TextView temp = (TextView) findViewById(R.id.am_tv_temp);
        final TextView tempMinValue = (TextView) findViewById(R.id.am_tv_tempMinValue);
        final TextView tempMaxValue = (TextView) findViewById(R.id.am_tv_tempMaxValue);
        final TextView city = (TextView) findViewById(R.id.am_tv_city);
        final TextView country = (TextView) findViewById(R.id.am_tv_country);
        final ImageView weatherIcon = (ImageView) findViewById(R.id.am_iv_weather_icon);
        final TextView humidity = (TextView) findViewById(R.id.am_tv_humidity);
        final TextView pressure = (TextView) findViewById(R.id.am_tv_pressure);
        final TextView windSpeed = (TextView) findViewById(R.id.am_tv_wind_speed);
        final TextView windDeg = (TextView) findViewById(R.id.am_tv_wind_deg);

        WeatherApiInterface weatherApiInterface = WeatherApiClient.getClient().create(WeatherApiInterface.class);
        Call<CurrentWeatherData> call = weatherApiInterface.getCurrentWeatherData(Double.parseDouble(mLatitude), Double.parseDouble(mLongitude), "metric", getResources().getString(R.string.API_KEY));
        call.enqueue(new Callback<CurrentWeatherData>() {
            @Override
            public void onResponse(Call<CurrentWeatherData> call, Response<CurrentWeatherData> response) {
                mCurrentWeatherData = response.body();
                loadNotificationWeatherData();

                temp.setText(String.valueOf(Math.round(mCurrentWeatherData.main.temp)) + "°C");
                tempMinValue.setText(String.valueOf(Math.round(mCurrentWeatherData.main.tempMin)) + "°C");
                tempMaxValue.setText(String.valueOf(Math.round(mCurrentWeatherData.main.tempMax)) + "°C");
                city.setText(mForecastWeatherData.city.name);
                country.setText(mForecastWeatherData.city.country);
                humidity.setText(String.valueOf(mCurrentWeatherData.main.humidity) + "%");
                pressure.setText(String.valueOf(mCurrentWeatherData.main.pressure) + "hPa");
                windSpeed.setText(String.valueOf(mCurrentWeatherData.wind.speed) + "m/s");
                windDeg.setText(String.valueOf(mCurrentWeatherData.wind.degrees));

                Glide.with(getApplicationContext()).load(WeatherApiClient.getImageUrl(mCurrentWeatherData.weather.get(0).icon))
                        .into(weatherIcon);
            }

            @Override
            public void onFailure(Call<CurrentWeatherData> call, Throwable t) {
                Log.d(TAG, "OpenWeatherMap current data collection failed");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_LOCATION_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                this.mLatitude = String.valueOf(latitude);
                this.mLongitude = String.valueOf(longitude);

                loadOpenWeatherMapData();

                String twoDecimalFormat = "%.2f";
                String toastText = "Latitude: " + String.format(twoDecimalFormat, latitude) + " Longitude: " + String.format(twoDecimalFormat, longitude);
                Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Location selection failed!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setOnClickListeners() {
        mChooseLocationFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("longitude", mLongitude);
                bundle.putString("latitude", mLatitude);

                Intent pickLocationIntent = new Intent(v.getContext(), LocationPickerActivity.class);
                pickLocationIntent.putExtras(bundle);

                startActivityForResult(pickLocationIntent, PICK_LOCATION_CODE);
            }
        });


        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadOpenWeatherMapData();
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
                        Toast.makeText(MainActivity.this, "Location selected : " + item.getTitle(), Toast.LENGTH_SHORT).show();
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
