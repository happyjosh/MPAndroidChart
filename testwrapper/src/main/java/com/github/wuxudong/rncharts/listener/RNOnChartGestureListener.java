package com.github.wuxudong.rncharts.listener;

import android.view.MotionEvent;
import android.graphics.Matrix;

import com.github.mikephil.charting.charts.Chart;

import java.lang.ref.WeakReference;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.uimanager.PixelUtil;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.ChartTouchListener;

/**
 * 实现了手势监听，调用JS（暂时未全部实现）
 * Created by jph on 2017/9/27.
 */
public class RNOnChartGestureListener implements OnChartGestureListener {

    private WeakReference<Chart> mWeakChart;

    public RNOnChartGestureListener(Chart chart) {
        mWeakChart = new WeakReference<>(chart);
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        onMatrixChange();
//        if (mWeakChart != null) {
//            Chart chart = mWeakChart.get();
//            WritableMap event = Arguments.createMap();
//            event.putString("type", "onChartScale");
//
//            WritableMap values = Arguments.createMap();
//            values.putDouble("aaa", scaleX);
//            values.putDouble("bbb", scaleY);
//            //中心点位置
//            values.putDouble("ccc", PixelUtil.toDIPFromPixel(me.getX()));
//            values.putDouble("ddd", PixelUtil.toDIPFromPixel(me.getY()));
//            event.putMap("values", values);
//
//            ReactContext reactContext = (ReactContext) chart.getContext();
//            reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
//                    chart.getId(),
//                    "topChange",
//                    event);
//        }
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        onMatrixChange();
//        if (mWeakChart != null) {
//            Chart chart = mWeakChart.get();
//            WritableMap event = Arguments.createMap();
//            event.putString("type", "onChartTranslate");
//
//            WritableMap values = Arguments.createMap();
//            values.putDouble("distanceX", PixelUtil.toDIPFromPixel(dX));
//            values.putDouble("distanceY", PixelUtil.toDIPFromPixel(dY));
//            event.putMap("values", values);
//
//            ReactContext reactContext = (ReactContext) chart.getContext();
//            reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
//                    chart.getId(),
//                    "topChange",
//                    event);
//        }
    }

    public void onMatrixChange() {
        if (mWeakChart == null) {
            return;
        }

        Chart chart = mWeakChart.get();

        Matrix srcMatrix;
        float[] srcVals = new float[9];

        srcMatrix = chart.getViewPortHandler().getMatrixTouch();
        srcMatrix.getValues(srcVals);

        WritableArray dstVals = Arguments.createArray();
        for (int i = 0; i < srcVals.length; i++) {
            dstVals.pushDouble(srcVals[i]);
        }

        WritableMap event = Arguments.createMap();
        event.putArray("matrix", dstVals);
        ReactContext reactContext = (ReactContext) chart.getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                chart.getId(),
                "topChange",
                event);
    }
}
