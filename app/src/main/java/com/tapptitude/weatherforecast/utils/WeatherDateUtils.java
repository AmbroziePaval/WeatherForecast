package com.tapptitude.weatherforecast.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ambroziepaval on 10/10/16.
 */
public class WeatherDateUtils {
    public static String getTimeStringFromWeatherData(String weatherData) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        SimpleDateFormat resultFormat = new SimpleDateFormat("HH:mm", Locale.US);
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        SimpleDateFormat resultFormat = new SimpleDateFormat("HH:mm", Locale.US);
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        try {
            Date date = simpleDateFormat.parse(weatherData);
            Calendar c1 = Calendar.getInstance();
            c1.setTime(date);
            int currentDayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            int weatherDateDayOfMonth = c1.get(Calendar.DAY_OF_MONTH);
            int weatherDateHourOfDay = c1.get(Calendar.HOUR_OF_DAY);

            if (weatherDateDayOfMonth == currentDayOfMonth || (weatherDateDayOfMonth == currentDayOfMonth + 1 && weatherDateHourOfDay == 0)) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean fromSameDay(String weatherData1, String weatherData2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        try {
            Date date1 = simpleDateFormat.parse(weatherData1);
            Date date2 = simpleDateFormat.parse(weatherData2);
            Calendar c1 = Calendar.getInstance();
            c1.setTime(date1);
            Calendar c2 = Calendar.getInstance();
            c2.setTime(date2);

            int weather1DateDayOfMonth = c1.get(Calendar.DAY_OF_MONTH);
            int weather2DateDayOfMonth = c2.get(Calendar.DAY_OF_MONTH);
            int weather2DateHourOfDay = c2.get(Calendar.HOUR_OF_DAY);

            if (weather1DateDayOfMonth == weather2DateDayOfMonth || (weather1DateDayOfMonth + 1 == weather2DateDayOfMonth) && weather2DateHourOfDay == 0) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getDateOfCalculation(String weatherData) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        SimpleDateFormat finalFormat = new SimpleDateFormat("MMM dd ", Locale.US);
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        SimpleDateFormat finalFormat = new SimpleDateFormat("EEEE", Locale.US);
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

