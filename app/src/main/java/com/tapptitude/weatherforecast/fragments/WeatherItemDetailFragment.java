package com.tapptitude.weatherforecast.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tapptitude.weatherforecast.R;
import com.tapptitude.weatherforecast.json.owm_forecast.list.WeatherData;
import com.tapptitude.weatherforecast.retrofit.WeatherApiClient;
import com.tapptitude.weatherforecast.utils.TemperatureColorPicker;
import com.tapptitude.weatherforecast.custom_views.GraphView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ambroziepaval on 10/6/16.
 */
public class WeatherItemDetailFragment extends Fragment {
    private WeatherData mWeatherData;
    private ArrayList<WeatherData> mWeatherGraphDataList;
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
    private LinearLayout mllMain;

    public WeatherItemDetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        mWeatherData = bundle.getParcelable("weather");
        mWeatherGraphDataList = bundle.getParcelableArrayList("weather_graph_list");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.weather_item_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mllMain = (LinearLayout) view.findViewById(R.id.wid_ll_main);

        mDay = (TextView) view.findViewById(R.id.wid_tv_day);
        mTime = (TextView) view.findViewById(R.id.wid_tv_time);
        mTemp = (TextView) view.findViewById(R.id.wid_tv_temp);
        mTempMin = (TextView) view.findViewById(R.id.wid_tv_temp_min_value);
        mTempMax = (TextView) view.findViewById(R.id.wid_tv_temp_max_value);
        mDescription = (TextView) view.findViewById(R.id.wid_tv_description_value);
        mHumidity = (TextView) view.findViewById(R.id.wid_tv_humidity_value);
        mCloudiness = (TextView) view.findViewById(R.id.wid_tv_cloudiness_value);
        mWindSpeed = (TextView) view.findViewById(R.id.wid_tv_wind_speed_value);
        mWindDegrees = (TextView) view.findViewById(R.id.wid_tv_wind_deg_value);
        mPressure = (TextView) view.findViewById(R.id.wid_tv_pressure_value);
        mGroundPressure = (TextView) view.findViewById(R.id.wid_tv_ground_pressure_value);
        mSeaPressure = (TextView) view.findViewById(R.id.wid_tv_sea_pressure_value);
        mCalcTime = (TextView) view.findViewById(R.id.wid_tv_sea_time_calc_value);
        mWeatherConditionIV = (ImageView) view.findViewById(R.id.wid_iv_image);
        mWeatherGraphView = (GraphView) view.findViewById(R.id.wid_gv_graph);

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

        Glide.with(getContext()).load(WeatherApiClient.getImageUrl(mWeatherData.weather.get(0).icon))
                .into(mWeatherConditionIV);
    }
}
