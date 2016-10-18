package com.tapptitude.weatherforecast.custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tapptitude.weatherforecast.json.owm_forecast.list.WeatherData;
import com.tapptitude.weatherforecast.utils.WeatherDateUtils;
import com.tapptitude.weatherforecast.utils.TemperatureColorPicker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ambroziepaval on 10/10/16.
 */
public class GraphView extends View {
    private Context mContext;
    private float mDensityPixel;
    public List<WeatherData> mWeatherDataList;
    public List<GraphItem> mGraphItemList;
    private WeatherData mLastClickedGraphItem;
    private Paint mDotPaint;
    private Paint mLinePaint;
    private Paint mTempPaint;
    private Paint mTimePaint;
    private int nrVerticalSpaces;
    private int mItemVerticalSpace;
    private int mViewWidth;
    private int mViewHeight;
    private boolean minimalisticInfo;

    public GraphView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public GraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        setWillNotDraw(false);
        minimalisticInfo = false;
        mGraphItemList = new ArrayList<>();
        mDensityPixel = mContext.getResources().getDisplayMetrics().density;

        mDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDotPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mDotPaint.setColor(Color.BLACK);
        mDotPaint.setStrokeCap(Paint.Cap.ROUND);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(1 * mDensityPixel);
        mLinePaint.setColor(Color.BLACK);

        mTempPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTempPaint.setColor(Color.BLACK);
        mTempPaint.setStyle(Paint.Style.FILL);
        mTempPaint.setTextAlign(Paint.Align.CENTER);
        mTempPaint.setTextSize(12 * mDensityPixel);

        mTimePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimePaint.setColor(Color.BLACK);
        mTimePaint.setStyle(Paint.Style.FILL);
        mTimePaint.setTextAlign(Paint.Align.CENTER);
        mTimePaint.setTextSize(10 * mDensityPixel);

        if (this.isInEditMode()) {
            mGraphItemList = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                GraphItem graphItem = new GraphItem();
                graphItem.temp = (int) (i * (Math.pow(-1, i)));
                graphItem.color = TemperatureColorPicker.getTemperatureColor(graphItem.temp);
                graphItem.time = "xx:xx";
                mGraphItemList.add(graphItem);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mViewWidth = this.getWidth();
        mViewHeight = this.getHeight();

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        updateDimensionValues();

        drawLines(canvas);
        drawPoints(canvas);
        drawTemps(canvas);
        if (!minimalisticInfo) {
            drawTime(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                final float x = event.getX();
                final float y = event.getY();

                int clickedGraphItemPosition = getClickedGraphItemPosition(x, y);
                if (clickedGraphItemPosition != -1) {
                    mLastClickedGraphItem = mWeatherDataList.get(clickedGraphItemPosition);
                }

                break;
            }
        }
        return true;
    }

    private int getClickedGraphItemPosition(float x, float y) {
        double min = 20 * mDensityPixel;
        int poz = -1;
        for (int i = 0; i < mGraphItemList.size(); i++) {
            GraphItem g = mGraphItemList.get(i);
            double diff = Math.sqrt(Math.pow((g.x - x), 2) + Math.pow((g.y - y), 2));
            if (diff < min) {
                min = diff;
                poz = i;
            }
        }
        return poz;
    }

    private void drawPoints(Canvas canvas) {
        for (GraphItem graphItem : mGraphItemList) {
            mDotPaint.setColor(graphItem.color);
            canvas.drawCircle(graphItem.x, graphItem.y, 5 * mDensityPixel, mDotPaint);
        }
    }

    private void drawLines(Canvas canvas) {
        for (int i = 0; i < mGraphItemList.size() - 1; i++) {
            GraphItem graphItem1 = mGraphItemList.get(i);
            GraphItem graphItem2 = mGraphItemList.get(i + 1);
            canvas.drawLine(graphItem1.x, graphItem1.y, graphItem2.x, graphItem2.y, mLinePaint);
        }
    }

    private void drawTemps(Canvas canvas) {
        for (GraphItem graphItem : mGraphItemList) {
//            if (!minimalisticInfo) {
//                mTempPaint.setColor(graphItem.color);
//            } else {
                mTempPaint.setColor(Color.BLACK);
//            }
            canvas.drawText(String.valueOf(graphItem.temp) + "°C", graphItem.x, graphItem.y - 10 * mDensityPixel, mTempPaint);
        }
    }

    private void drawTime(Canvas canvas) {
        for (GraphItem graphItem : mGraphItemList) {
            canvas.drawText(graphItem.time, graphItem.x, nrVerticalSpaces * mItemVerticalSpace, mTimePaint);
        }
    }

    private void updateDimensionValues() {
        int mItemCount = mGraphItemList.size();
        int mItemMinimumValue = getMinimumItemValue();
        int mItemMaximumValue = getMaximumItemValue();
        int mItemHorizontalSpace = mViewWidth / (mItemCount + 1);
        nrVerticalSpaces = mItemMaximumValue - mItemMinimumValue + 4;
        mItemVerticalSpace = mViewHeight / nrVerticalSpaces;

        int itemCount = 1;
        for (GraphItem graphItem : mGraphItemList) {
            mDotPaint.setColor(graphItem.color);
            graphItem.x = itemCount * mItemHorizontalSpace;
            graphItem.y = (mItemMaximumValue - graphItem.temp + 3) * mItemVerticalSpace;
            itemCount++;
        }
    }

    private int getMaximumItemValue() {
        int max = -100;
        for (GraphItem graphItem : mGraphItemList) {
            if (graphItem.temp > max) {
                max = graphItem.temp;
            }
        }
        return max;
    }

    private int getMinimumItemValue() {
        int min = 100;
        for (GraphItem graphItem : mGraphItemList) {
            if (graphItem.temp < min) {
                min = graphItem.temp;
            }
        }
        return min;
    }

    public void setMWeatherDataList(List<WeatherData> mWeatherDataList) {
        this.mWeatherDataList = mWeatherDataList;
        mGraphItemList = new ArrayList<>();
        for (WeatherData weatherData : mWeatherDataList) {
            GraphItem graphItem = new GraphItem();
            graphItem.temp = (int) Math.round(weatherData.main.temp);
            graphItem.color = TemperatureColorPicker.getTemperatureColor(graphItem.temp);
            graphItem.time = WeatherDateUtils.getTimeStringFromWeatherData(weatherData.timeOfCalculation);
            mGraphItemList.add(graphItem);
        }
    }

    public void setMinimalisticInfo(boolean minimalistic) {
        this.minimalisticInfo = minimalistic;
        if (minimalistic) {
            mTempPaint.setTextSize(9 * mDensityPixel);
        }
    }

    public WeatherData getLastClickedGraphItem() {
        return mLastClickedGraphItem;
    }

    public static class GraphItem {
        public int x;
        public int y;
        public int color;
        public int temp;
        public String time;
    }
}
