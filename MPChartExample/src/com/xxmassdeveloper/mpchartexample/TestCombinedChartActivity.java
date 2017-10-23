
package com.xxmassdeveloper.mpchartexample;

import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.FloatLabel;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.Utils;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import java.util.ArrayList;
import java.util.List;

public class TestCombinedChartActivity extends DemoBase {

    private CombinedChart mChart;
    private CombinedChart mChart2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_test_combine);

        mChart = (CombinedChart) findViewById(R.id.chart1);
        mChart2 = (CombinedChart) findViewById(R.id.chart2);


        configChart1();
        configChart2();

        initData();

        linkMove();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.candle, menu);
        return true;
    }

    /**
     * 设置两个图表联动
     */
    private void linkMove() {
        float lineLeft = mChart.getViewPortHandler().offsetLeft();
        float barLeft = mChart2.getViewPortHandler().offsetLeft();
        float lineRight = mChart.getViewPortHandler().offsetRight();
        float barRight = mChart2.getViewPortHandler().offsetRight();
        float offsetLeft, offsetRight;
 /*注：setExtraLeft...函数是针对图表相对位置计算，比如A表offLeftA=20dp,B表offLeftB=30dp,则A.setExtraLeftOffset(10),并不是30，还有注意单位转换*/
        if (barLeft < lineLeft) {
            offsetLeft = Utils.convertPixelsToDp(lineLeft - barLeft);
            mChart2.setExtraLeftOffset(offsetLeft);
        } else {
            offsetLeft = Utils.convertPixelsToDp(barLeft - lineLeft);
            mChart.setExtraLeftOffset(offsetLeft);
        }
  /*注：setExtraRight...函数是针对图表绝对位置计算，比如A表offRightA=20dp,B表offRightB=30dp,则A.setExtraLeftOffset(30),并不是10，还有注意单位转换*/
        if (barRight < lineRight) {
            offsetRight = Utils.convertPixelsToDp(lineRight - barRight);
            mChart2.setExtraRightOffset(offsetRight);
        } else {
            offsetRight = Utils.convertPixelsToDp(barRight - lineRight);
            mChart.setExtraRightOffset(offsetRight);
        }

        mChart.setOnChartGestureListener(new TestOnChartGestureListener(mChart, mChart2));
        mChart2.setOnChartGestureListener(new TestOnChartGestureListener(mChart2, mChart));
    }

    private void configChart1() {
        mChart.setBackgroundColor(Color.WHITE);

        mChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);

        //TODO test
//        mChart.setScaleMinima(0.5f, 0.5f);
//        mChart.setScaleMaxima(5f, 5f);
        mChart.setFloatYValue(100);
        FloatLabel yLabel = new FloatLabel(getApplicationContext());
        yLabel.getLabelText().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
        mChart.setRightFloatYLabel(yLabel);

        //TODO test markView
        FloatLabel leftLabel = new FloatLabel(getApplicationContext());
        leftLabel.getLabelText().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
        mChart.setLeftMarkerView(leftLabel);
        FloatLabel bottomLabel = new FloatLabel(getApplicationContext());
        bottomLabel.getLabelText().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
        mChart.setBottomMarkerView(bottomLabel);

        mChart.setHighlightPerDragEnabled(true);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis rightAxis = mChart.getAxisRight();
//        rightAxis.setEnabled(false);
        rightAxis.setLabelCount(7, false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawAxisLine(false);
        //TODO test
        LimitLine limitLine = new LimitLine(120);
        limitLine.setLineColor(ContextCompat.getColor(getApplicationContext(), android.R.color.black));
        limitLine.setLineWidth(2);
        limitLine.enableDashedLine(20, 40, 0);
        rightAxis.addLimitLine(limitLine);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setEnabled(false);
//        rightAxis.setStartAtZero(false);

        mChart.getLegend().setEnabled(false);
    }

    private void initData() {
        List<CandleEntry> chart1List = new ArrayList<CandleEntry>();
        List<BarEntry> chart2List = new ArrayList<BarEntry>();

        for (int i = 0; i < 100; i++) {
            float mult = (50 + 1);
            float val = (float) (Math.random() * 40) + mult;

            float high = (float) (Math.random() * 9) + 8f;
            float low = (float) (Math.random() * 9) + 8f;

            float open = (float) (Math.random() * 6) + 1f;
            float close = (float) (Math.random() * 6) + 1f;

            boolean even = i % 2 == 0;

            chart1List.add(new CandleEntry(
                    i, val + high,
                    val - low,
                    even ? val + open : val - open,
                    even ? val - close : val + close,
                    getResources().getDrawable(R.drawable.star)
            ));
            chart2List.add(new BarEntry(i, close));
        }

        initChart1Data(chart1List);
        initChart2Data(chart2List);
    }

    private void configChart2() {

        YAxis rightAxis = mChart2.getAxisRight();
//        rightAxis.setEnabled(false);
        rightAxis.setLabelCount(7, false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawAxisLine(false);
        //TODO test
        LimitLine limitLine = new LimitLine(120);
        limitLine.setLineColor(ContextCompat.getColor(getApplicationContext(), android.R.color.black));
        limitLine.setLineWidth(2);
        limitLine.enableDashedLine(20, 40, 0);
        rightAxis.addLimitLine(limitLine);

        YAxis leftAxis = mChart2.getAxisLeft();
        leftAxis.setEnabled(false);

    }

    private void initChart1Data(List<CandleEntry> chart1List) {


        CandleDataSet set1 = new CandleDataSet(chart1List, "CandleDataSet");

        set1.setDrawIcons(false);
        set1.setAxisDependency(YAxis.AxisDependency.RIGHT);
//        set1.setColor(Color.rgb(80, 80, 80));
        set1.setShadowColor(Color.DKGRAY);
        set1.setShadowWidth(0.7f);
        set1.setDecreasingColor(Color.RED);
        set1.setDecreasingPaintStyle(Paint.Style.FILL);
        set1.setIncreasingColor(Color.rgb(122, 242, 84));
        set1.setIncreasingPaintStyle(Paint.Style.STROKE);
        set1.setNeutralColor(Color.BLUE);
        //set1.setHighlightLineWidth(1f);

        //TODO
        set1.setHighlightEnabled(true);
        set1.setHighlightLineWidth(2);
//        set1.enableDashedHighlightLine(5, 5, 0);
//        set1.setDrawHighlightIndicators(true);
//        set1.setHighLightColor(ContextCompat.getColor(getApplicationContext(), android.R.color.black));

        CandleData data = new CandleData(set1);

        CombinedData combinedData = new CombinedData();
        combinedData.setData(data);

        mChart.setData(combinedData);
        mChart.invalidate();
    }

    private void initChart2Data(List<BarEntry> chart2List) {
        BarDataSet barDataSet = new BarDataSet(chart2List, "BarDataSet");

        BarData data = new BarData(barDataSet);

        CombinedData combinedData = new CombinedData();
        combinedData.setData(data);

        mChart2.setData(combinedData);
        mChart2.invalidate();
    }

    class TestOnChartGestureListener implements OnChartGestureListener {

        private Chart chart1, chart2;


        public TestOnChartGestureListener(Chart chart1, Chart chart2) {
            this.chart1 = chart1;
            this.chart2 = chart2;
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
        }

        @Override
        public void onChartTranslate(MotionEvent me, float dX, float dY) {
            onMatrixChange();
        }

        public void onMatrixChange() {


            Matrix srcMatrix;
            float[] srcVals = new float[9];

            srcMatrix = chart1.getViewPortHandler().getMatrixTouch();
            srcMatrix.getValues(srcVals);
            srcMatrix.getValues(srcVals);

            // apply X axis scaling and position to dst charts:
            Matrix dstMatrix;
            float[] dstVals = new float[9];
            dstMatrix = chart2.getViewPortHandler().getMatrixTouch();
            dstMatrix.getValues(dstVals);

            dstVals[Matrix.MSCALE_X] = srcVals[Matrix.MSCALE_X];
            dstVals[Matrix.MSKEW_X] = srcVals[Matrix.MSKEW_X];
            dstVals[Matrix.MTRANS_X] = srcVals[Matrix.MTRANS_X];
            dstVals[Matrix.MSKEW_Y] = srcVals[Matrix.MSKEW_Y];
            dstVals[Matrix.MSCALE_Y] = srcVals[Matrix.MSCALE_Y];
            dstVals[Matrix.MTRANS_Y] = srcVals[Matrix.MTRANS_Y];
            dstVals[Matrix.MPERSP_0] = srcVals[Matrix.MPERSP_0];
            dstVals[Matrix.MPERSP_1] = srcVals[Matrix.MPERSP_1];
            dstVals[Matrix.MPERSP_2] = srcVals[Matrix.MPERSP_2];

            dstMatrix.setValues(dstVals);
            chart2.getViewPortHandler().refresh(dstMatrix, chart2, true);
        }
    }


}
