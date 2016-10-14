package com.tapptitude.weatherforecast.utils;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;

/**
 * Created by ambroziepaval on 9/27/16.
 */
public class TemperatureColorPicker {

    public static int getTemperatureColor(int mTemp) {
        if (mTemp <= 5) {
            return getColdTemperatureColor(mTemp);
        } else if (mTemp > 5 && mTemp <= 10) {
            return getMediumTemperatureColor(mTemp);
        } else if (mTemp > 10 && mTemp <= 15) {
            return getWarmTemperatureColor(mTemp);
        } else {
            return getHotTemperatureColor(mTemp);
        }
    }

    /**
     * Get a color with blue 255, red 0 and green value variable(10temp=250)
     * 10 temp values included above 0
     *
     * @return Color value
     */
    private static int getColdTemperatureColor(int mTemp) {
        if (mTemp < 0)
            return Color.rgb(0, 0, 255);
        else {
            int greenValue = 51 * mTemp;
            return Color.rgb(0, greenValue, 255);
        }
    }

    /**
     * Get a color with green 255, red 0 and blue value variable(15temp=0)
     * 5 temp values included
     *
     * @return Color value
     */
    private static int getMediumTemperatureColor(int mTemp) {
        int blueValue = 255 - (mTemp - 5) * 51;
        return Color.rgb(0, 255, blueValue);
    }

    /**
     * Get a color with red 255, blue 0 and green value variable(40temp=255)
     * 20 temp values included
     *
     * @return Color value
     */
    private static int getWarmTemperatureColor(int mTemp) {
        int redValue = (mTemp - 10) * 51;
        return Color.rgb(redValue, 255, 0);
    }

    private static int getHotTemperatureColor(int mTemp) {
        if (mTemp > 40) {
            return Color.rgb(255, 0, 0);
        } else {
            int greenValue = 255 - (mTemp - 15) * 10;
            return Color.rgb(255, greenValue, 0);
        }
    }

    public static GradientDrawable getTemperatureColorGradient90Deg(int mTemp) {
        return getGradientDrawable(getTemperatureColor(mTemp), GradientDrawable.Orientation.RIGHT_LEFT);
    }

    public static GradientDrawable getTemperatureColorGradient270Deg(int mTemp) {
        return getGradientDrawable(getTemperatureColor(mTemp), GradientDrawable.Orientation.LEFT_RIGHT);
    }

    public static GradientDrawable getTemperatureColorGradientInverted(int mTemp) {
        return getGradientDrawable(getTemperatureColor(mTemp), GradientDrawable.Orientation.BOTTOM_TOP);
    }

    @NonNull
    private static GradientDrawable getGradientDrawable(int color, GradientDrawable.Orientation orientation) {
        GradientDrawable gd = new GradientDrawable( orientation,
                new int[]{color, Color.parseColor("#00FFFFFF")});
        gd.setCornerRadius(0f);
        return gd;
    }
}
