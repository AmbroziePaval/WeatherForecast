package com.tapptitude.weatherforecast.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tapptitude.weatherforecast.R;
import com.tapptitude.weatherforecast.json.owm_forecast.list.WeatherData;
import com.tapptitude.weatherforecast.retrofit.WeatherApiClient;
import com.tapptitude.weatherforecast.utils.TemperatureColorPicker;
import com.tapptitude.weatherforecast.utils.WeatherConditionPicturePicker;
import com.tapptitude.weatherforecast.custom_views.GraphView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ambroziepaval on 10/6/16.
 */
public class WeatherItemDetailActivity extends Activity {
    private WeatherData mWeatherData;
    private ArrayList<WeatherData> mWeatherGraphDataList;
    private LinearLayout mllMain;
    private TextView mDay;
    private TextView mTime;
    private TextView mTemp;
    private TextView mTempMin;
    private TextView mTempMax;
    private TextView mDescription;
    private TextView mHumidity;
    private TextView mCloudiness;
    private TextView mWindSpeed;
    private TextView mWindDegrees;
    private TextView mPressure;
    private TextView mGroundPressure;
    private TextView mSeaPressure;
    private TextView mCalcTime;
    private ImageView mWeatherConditionIV;
    private GraphView mWeatherGraphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_details);

        Bundle bundle = getIntent().getExtras();
        mWeatherData = bundle.getParcelable("weather");
        mWeatherGraphDataList = bundle.getParcelableArrayList("weather_graph_list");

        mllMain = (LinearLayout) findViewById(R.id.awd_ll_main);

        mDay = (TextView) findViewById(R.id.awd_tv_day);
        mTime = (TextView) findViewById(R.id.awd_tv_time);
        mTemp = (TextView) findViewById(R.id.awd_tv_temp);
        mTempMin = (TextView) findViewById(R.id.awd_tv_temp_min_value);
        mTempMax = (TextView) findViewById(R.id.awd_tv_temp_max_value);
        mDescription = (TextView) findViewById(R.id.awd_tv_description_value);
        mHumidity = (TextView) findViewById(R.id.awd_tv_humidity_value);
        mCloudiness = (TextView) findViewById(R.id.awd_tv_cloudiness_value);
        mWindSpeed = (TextView) findViewById(R.id.awd_tv_wind_speed_value);
        mWindDegrees = (TextView) findViewById(R.id.awd_tv_wind_deg_value);
        mPressure = (TextView) findViewById(R.id.awd_tv_pressure_value);
        mGroundPressure = (TextView) findViewById(R.id.awd_tv_ground_pressure_value);
        mSeaPressure = (TextView) findViewById(R.id.awd_tv_sea_pressure_value);
        mCalcTime = (TextView) findViewById(R.id.awd_tv_sea_time_calc_value);
        mWeatherConditionIV = (ImageView) findViewById(R.id.awd_iv_image);
        mWeatherGraphView = (GraphView) findViewById(R.id.awd_gv_graph);

        mWeatherGraphView.setMinimalisticInfo(false);
        mWeatherGraphView.setMWeatherDataList(mWeatherGraphDataList);
        mWeatherGraphView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                GraphView gv = (GraphView) v;
                gv.onTouchEvent(event);
                mWeatherData = gv.getLastClickedGraphItem();
                loadDetails();
                return true;
            }
        });

        loadDetails();
    }


    private void loadDetails() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        try {
            Date date = simpleDateFormat.parse(mWeatherData.timeOfCalculation);
            mDay.setText(dayFormat.format(date));
            mTime.setText(timeFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mllMain.setBackground(TemperatureColorPicker.getTemperatureColorGradientInverted((int) Math.round(mWeatherData.main.temp)));
        mTemp.setText(String.valueOf(Math.round(mWeatherData.main.temp)) + "°C");
        mTempMin.setText(String.valueOf(Math.round(mWeatherData.main.tempMin)) + "°C");
        mTempMax.setText(String.valueOf(Math.round(mWeatherData.main.tempMax)) + "°C");
        mDescription.setText(mWeatherData.weather.get(0).description);
        mHumidity.setText(String.valueOf(mWeatherData.main.humidity) + "%");
        mCloudiness.setText(String.valueOf(mWeatherData.clouds.all) + "%");
        mWindSpeed.setText(String.valueOf((int) mWeatherData.wind.speed) + "m/s");
        mWindDegrees.setText(String.valueOf((int) mWeatherData.wind.degrees));
        mPressure.setText(String.valueOf((int) mWeatherData.main.pressure) + "hPa");
        mGroundPressure.setText(String.valueOf((int) mWeatherData.main.groundLevelPressure) + "hPa");
        mSeaPressure.setText(String.valueOf((int) mWeatherData.main.seaLevelPressure) + "hPa");
        mCalcTime.setText(mWeatherData.timeOfCalculation);

        Glide.with(getApplicationContext()).load(WeatherApiClient.getImageUrl(mWeatherData.weather.get(0).icon))
                .into(mWeatherConditionIV);
    }
}
