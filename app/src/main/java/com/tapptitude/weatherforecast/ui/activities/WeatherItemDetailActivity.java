package com.tapptitude.weatherforecast.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tapptitude.weatherforecast.R;
import com.tapptitude.weatherforecast.model.json.owm_forecast.list.WeatherData;
import com.tapptitude.weatherforecast.retrofit.WeatherApiClient;
import com.tapptitude.weatherforecast.utils.TemperatureColorPicker;
import com.tapptitude.weatherforecast.ui.custom_views.GraphView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ambroziepaval on 10/6/16.
 */
public class WeatherItemDetailActivity extends Activity implements GraphView.MyGraphViewListener{
    public static final String KEY_DETAILS_WEATHER = "KEY_DETAILS_WEATHER";
    public static final String KEY_DETAILS_WEATHER_GRAPH_LIST = "KEY_DETAILS_WEATHER_GRAPH_LIST";
    private WeatherData mWeatherData;
    private ArrayList<WeatherData> mWeatherGraphDataList;

    @BindView(R.id.awd_ll_main)
    LinearLayout mllMain;
    @BindView(R.id.awd_tv_day)
    TextView mDay;
    @BindView(R.id.awd_tv_time)
    TextView mTime;
    @BindView(R.id.awd_tv_temp)
    TextView mTemp;
    @BindView(R.id.awd_tv_temp_min_value)
    TextView mTempMin;
    @BindView(R.id.awd_tv_temp_max_value)
    TextView mTempMax;
    @BindView(R.id.awd_tv_description_value)
    TextView mDescription;
    @BindView(R.id.awd_tv_humidity_value)
    TextView mHumidity;
    @BindView(R.id.awd_tv_cloudiness_value)
    TextView mCloudiness;
    @BindView(R.id.awd_tv_wind_speed_value)
    TextView mWindSpeed;
    @BindView(R.id.awd_tv_wind_deg_value)
    TextView mWindDegrees;
    @BindView(R.id.awd_tv_pressure_value)
    TextView mPressure;
    @BindView(R.id.awd_tv_ground_pressure_value)
    TextView mGroundPressure;
    @BindView(R.id.awd_tv_sea_pressure_value)
    TextView mSeaPressure;
    @BindView(R.id.awd_tv_time_calc_value)
    TextView mCalcTime;
    @BindView(R.id.awd_iv_image)
    ImageView mWeatherConditionIV;
    @BindView(R.id.awd_gv_graph)
    GraphView mWeatherGraphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_details);

        readBundleData();

        ButterKnife.bind(this);

        setDetailsGraph();

        loadWeatherDetails();
    }

    private void setDetailsGraph() {
        mWeatherGraphView.setMinimalisticInfo(false);
        mWeatherGraphView.setMWeatherDataList(mWeatherGraphDataList);
        mWeatherGraphView.setMyGraphViewListener(this);
    }

    @Override
    public void onGraphItemClickListener(WeatherData weatherData) {
        mWeatherData = weatherData;
        loadWeatherDetails();
    }

    private void readBundleData() {
        Bundle bundle = getIntent().getExtras();
        mWeatherData = bundle.getParcelable(KEY_DETAILS_WEATHER);
        mWeatherGraphDataList = bundle.getParcelableArrayList(KEY_DETAILS_WEATHER_GRAPH_LIST);
    }


    private void loadWeatherDetails() {
        if (mWeatherData == null && mWeatherData.timeOfCalculation != null) return;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
        try {
            Date date = simpleDateFormat.parse(mWeatherData.timeOfCalculation);
            mDay.setText(dayFormat.format(date));
            mTime.setText(timeFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mllMain.setBackground(TemperatureColorPicker.getTemperatureColorGradientInverted((int) Math.round(mWeatherData.main.temp)));
        mTemp.setText(getResources().getString(R.string.details_temperature_x, Math.round(mWeatherData.main.temp)));
        mTempMin.setText(getResources().getString(R.string.details_temperature_x, Math.round(mWeatherData.main.tempMin)));
        mTempMax.setText(getResources().getString(R.string.details_temperature_x, Math.round(mWeatherData.main.tempMax)));
        mDescription.setText(mWeatherData.weather.get(0).description);
        mHumidity.setText(getResources().getString(R.string.details_humidity_x, mWeatherData.main.humidity));
        mCloudiness.setText(getResources().getString(R.string.details_clouds_x, mWeatherData.clouds.all));
        mWindSpeed.setText(getResources().getString(R.string.details_wind_speed_x, mWeatherData.wind.speed));
        mWindDegrees.setText(String.valueOf((int) mWeatherData.wind.degrees));
        mPressure.setText(getResources().getString(R.string.details_pressure_x, mWeatherData.main.pressure));
        mGroundPressure.setText(getResources().getString(R.string.details_pressure_x, mWeatherData.main.groundLevelPressure));
        mSeaPressure.setText(getResources().getString(R.string.details_pressure_x, mWeatherData.main.seaLevelPressure));
        mCalcTime.setText(mWeatherData.timeOfCalculation);

        Glide.with(this).load(WeatherApiClient.getImageUrl(mWeatherData.weather.get(0).icon))
                .into(mWeatherConditionIV);
    }
}
