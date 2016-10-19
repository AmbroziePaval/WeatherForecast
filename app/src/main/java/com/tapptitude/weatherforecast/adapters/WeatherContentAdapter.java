package com.tapptitude.weatherforecast.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tapptitude.weatherforecast.R;
import com.tapptitude.weatherforecast.activities.WeatherItemDetailActivity;
import com.tapptitude.weatherforecast.fragments.WeatherItemDetailFragment;
import com.tapptitude.weatherforecast.json.owm_forecast.list.WeatherData;
import com.tapptitude.weatherforecast.retrofit.WeatherApiClient;
import com.tapptitude.weatherforecast.utils.TemperatureColorPicker;
import com.tapptitude.weatherforecast.utils.WeatherDateUtils;
import com.tapptitude.weatherforecast.custom_views.GraphView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ambroziepaval on 10/5/16.
 */
public class WeatherContentAdapter extends RecyclerView.Adapter<WeatherContentAdapter.WeatherContentViewHolder> {
    private Context context;
    ArrayList<WeatherData> displayWeatherList;
    private MyAdapterListener mMyAdapterListener;

    public void setMyAdapterListener(MyAdapterListener myAdapterListener) {
        mMyAdapterListener = myAdapterListener;
    }

    public WeatherContentAdapter(Context context, ArrayList<WeatherData> displayWeatherList) {
        this.context = context;
        this.displayWeatherList = displayWeatherList;
    }

    @Override
    public WeatherContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View inflatedView = layoutInflater.inflate(R.layout.weather_item_content, parent, false);
        return new WeatherContentViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(final WeatherContentViewHolder holder, int position) {
        WeatherData weatherData = displayWeatherList.get(position);

        String tempText = context.getResources().getString(R.string.details_temperature_x, (int) Math.round(weatherData.main.temp));
        holder.mWeatherTempTV.setText(tempText);
        holder.mWeatherDayTV.setText(WeatherDateUtils.getDayOfCalculation(weatherData.timeOfCalculation));
        holder.mWeatherTimeTV.setText(getTimeText(weatherData));

        holder.mGraphView.setMWeatherDataList(mMyAdapterListener.getWeatherDataFromSameDay(displayWeatherList.get(position)));

        holder.mWeatherCardCV.setBackground(TemperatureColorPicker.getTemperatureColorGradient270Deg((int) Math.round(weatherData.main.temp)));
        holder.mWeatherCardCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMyAdapterListener.onWeatherCardItemClick(displayWeatherList.get(holder.getAdapterPosition()));
            }
        });

        Glide.with(context).load(WeatherApiClient.getImageUrl(weatherData.weather.get(0).icon)).into(holder.mWeatherConditionImageIV);
    }

    @NonNull
    private String getTimeText(WeatherData weatherData) {
        String timeText;
        if (WeatherDateUtils.isTodayWeatherData(weatherData.timeOfCalculation)) {
            timeText = "Today " + WeatherDateUtils.getTimeStringFromWeatherData(weatherData.timeOfCalculation);
        } else {
            timeText = WeatherDateUtils.getDateOfCalculation(weatherData.timeOfCalculation) + ", Noon";
        }
        return timeText;
    }

    @Override
    public int getItemCount() {
        return displayWeatherList != null ? displayWeatherList.size() : 0;
    }

    public interface MyAdapterListener {
        void onWeatherCardItemClick(WeatherData weatherData);
        ArrayList<WeatherData> getWeatherDataFromSameDay(WeatherData weatherData);
    }

    class WeatherContentViewHolder extends RecyclerView.ViewHolder {
        private final CardView mWeatherCardCV;
        private final TextView mWeatherTempTV;
        private final TextView mWeatherTimeTV;
        private final TextView mWeatherDayTV;
        private final ImageView mWeatherConditionImageIV;
        private final GraphView mGraphView;

        public WeatherContentViewHolder(View itemView) {
            super(itemView);

            mWeatherCardCV = (CardView) itemView.findViewById(R.id.wic_cv_weather);
            mWeatherTempTV = (TextView) itemView.findViewById(R.id.wic_tv_weather_temp);
            mWeatherTimeTV = (TextView) itemView.findViewById(R.id.wic_tv_weather_time);
            mWeatherDayTV = (TextView) itemView.findViewById(R.id.wic_tv_weather_day);
            mWeatherConditionImageIV = (ImageView) itemView.findViewById(R.id.wic_iv_weather_condition);
            mGraphView = (GraphView) itemView.findViewById(R.id.wic_gv_graph);
            mGraphView.setMinimalisticInfo(true);
        }
    }
}
