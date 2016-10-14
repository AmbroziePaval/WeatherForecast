package com.tapptitude.weatherforecast.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

/**
 * Created by ambroziepaval on 10/5/16.
 */
public class WeatherContentAdapter extends RecyclerView.Adapter<WeatherContentAdapter.WeatherContentViewHolder> {
    private Context context;
    private FragmentManager mFragmentManager;
    ArrayList<WeatherData> weatherList;
    ArrayList<WeatherData> displayWeatherList;

    public WeatherContentAdapter(Context context, FragmentManager fragmentManager, ArrayList<WeatherData> weatherDataList, ArrayList<WeatherData> displayWeatherList) {
        this.context = context;
        this.mFragmentManager = fragmentManager;
        this.weatherList = weatherDataList;
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

        String tempText = String.valueOf(Math.round(weatherData.main.temp)) + "°C";
        holder.mWeatherTempTV.setText(tempText);

        holder.mWeatherDayTV.setText(WeatherDateUtils.getDayOfCalculation(weatherData.timeOfCalculation));
        String timeText;
        if (WeatherDateUtils.isTodayWeatherData(weatherData.timeOfCalculation)) {
            timeText = "Today";
        } else {
            timeText = WeatherDateUtils.getDateOfCalculation(weatherData.timeOfCalculation) + ", Noon";
        }
        holder.mWeatherTimeTV.setText(timeText);

        Glide.with(context).load(WeatherApiClient.getImageUrl(weatherData.weather.get(0).icon))
                .into(holder.mWeatherConditionImageIV);

        holder.mGraphView.setMWeatherDataList(getWeatherDataFromSameDay(displayWeatherList.get(position)));

        holder.mWeatherCardCV.setBackground(TemperatureColorPicker.getTemperatureColorGradient270Deg((int) Math.round(weatherData.main.temp)));
//        holder.mWeatherCardCV.setCardBackgroundColor(TemperatureColorPicker.getTemperatureColor((int) weatherData.main.temp));
        holder.mWeatherCardCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String twoPane = v.getResources().getString(R.string.tablet);

                Bundle bundle = new Bundle();
                bundle.putParcelable("weather", displayWeatherList.get(holder.getAdapterPosition()));
                bundle.putParcelableArrayList("weather_graph_list", getWeatherDataFromSameDay(displayWeatherList.get(holder.getAdapterPosition())));

                if (twoPane.equals("true")) {
                    WeatherItemDetailFragment detailsFragment = new WeatherItemDetailFragment();
                    detailsFragment.setArguments(bundle);

                    FragmentTransaction transaction = mFragmentManager.beginTransaction();
                    transaction.replace(R.id.wil_fl_weather_item_detail_container, detailsFragment);
                    transaction.commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, WeatherItemDetailActivity.class);
                    intent.putExtras(bundle);

                    context.startActivity(intent);
                }
            }
        });
    }

    private ArrayList<WeatherData> getWeatherDataFromSameDay(WeatherData day) {
        ArrayList<WeatherData> dataArrayList = new ArrayList<>();
        for (WeatherData weatherData : weatherList) {
            if (WeatherDateUtils.fromSameDay(day.timeOfCalculation, weatherData.timeOfCalculation)) {
                dataArrayList.add(weatherData);
            }
        }
        return dataArrayList;
    }

    @Override
    public int getItemCount() {
        return displayWeatherList.size();
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

            mWeatherTempTV = (TextView) itemView.findViewById(R.id.wic_tv_weather_temp);
            mWeatherCardCV = (CardView) itemView.findViewById(R.id.wic_cv_weather);
            mWeatherTimeTV = (TextView) itemView.findViewById(R.id.wic_tv_weather_time);
            mWeatherDayTV = (TextView) itemView.findViewById(R.id.wic_tv_weather_day);
            mWeatherConditionImageIV = (ImageView) itemView.findViewById(R.id.wic_iv_weather_condition);
            mGraphView = (GraphView) itemView.findViewById(R.id.wic_gv_graph);
            mGraphView.setMinimalisticInfo(true);
        }
    }
}
