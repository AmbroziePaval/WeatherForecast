package com.tapptitude.weatherforecast.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ambroziepaval on 10/10/16.
 */
public class WeatherDateUtils {
    public static String getTimeStringFromWeatherData(String weatherData) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat resultFormat = new SimpleDateFormat("HH:mm");
        String result = "";
        try {
            Date date = simpleDateFormat.parse(weatherData);
            result = resultFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isNoonWeatherData(String weatherData) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat resultFormat = new SimpleDateFormat("HH:mm");
        String result = "";
        try {
            Date date = simpleDateFormat.parse(weatherData);
            result = resultFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result.equals("12:00");
    }

    public static boolean isTodayWeatherData(String weatherData) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = simpleDateFormat.parse(weatherData);
            Calendar c1 = Calendar.getInstance();
            c1.setTime(date);
            if (c1.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                    || (c1.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 1 && c1.get(Calendar.HOUR_OF_DAY) == 0)) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean fromSameDay(String weatherData1, String weatherData2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date1 = simpleDateFormat.parse(weatherData1);
            Date date2 = simpleDateFormat.parse(weatherData2);
            Calendar c1 = Calendar.getInstance();
            c1.setTime(date1);
            Calendar c2 = Calendar.getInstance();
            c2.setTime(date2);
            if (c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)
                    || (c1.get(Calendar.DAY_OF_MONTH) + 1 == c2.get(Calendar.DAY_OF_MONTH) && c2.get(Calendar.HOUR_OF_DAY) == 0)) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getDateOfCalculation(String weatherData) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat finalFormat = new SimpleDateFormat("MMM dd ");
        String result = "";
        try {
            Date date = simpleDateFormat.parse(weatherData);
            result = finalFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getDayOfCalculation(String weatherData) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat finalFormat = new SimpleDateFormat("EEEE");
        String result = "";
        try {
            Date date = simpleDateFormat.parse(weatherData);
            result = finalFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}

