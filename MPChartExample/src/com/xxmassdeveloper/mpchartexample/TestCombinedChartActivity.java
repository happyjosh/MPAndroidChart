
package com.xxmassdeveloper.mpchartexample;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

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
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import java.util.ArrayList;
import java.util.List;

public class TestCombinedChartActivity extends DemoBase {

    private static final String TAG = "TestCombinedChart";

    private static final int X_AXIS_LABEL_COUNT = 6;
    private static final int DATA_COUNT_ONE_GRID = 5;
    private static final int DATA_COUNT_ONE_GRID_MIN = 3;
    private static final int DATA_COUNT_ONE_GRID_MAX = 10;

    private CombinedChart mChart1;
    private CombinedChart mChart2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_combine);

        mChart1 = (CombinedChart) findViewById(R.id.chart1);
        mChart2 = (CombinedChart) findViewById(R.id.chart2);


        configChart1();
//        configChart2();

        initData();

        mChart1.setOnChartGestureListener(new TestOnChartGestureListener(mChart1, mChart2));

//        linkMove();
//
//        linkSelect();

        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMore();
            }
        });

        findViewById(R.id.zoom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChart1.getXAxis().setAxisMinimum(-0.5f);
            }
        });
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
        float lineLeft = mChart1.getViewPortHandler().offsetLeft();
        float barLeft = mChart2.getViewPortHandler().offsetLeft();
        float lineRight = mChart1.getViewPortHandler().offsetRight();
        float barRight = mChart2.getViewPortHandler().offsetRight();
        float offsetLeft, offsetRight;
 /*注：setExtraLeft...函数是针对图表相对位置计算，比如A表offLeftA=20dp,B表offLeftB=30dp,则A.setExtraLeftOffset(10),并不是30，还有注意单位转换*/
        if (barLeft < lineLeft) {
            offsetLeft = Utils.convertPixelsToDp(lineLeft - barLeft);
            mChart2.setExtraLeftOffset(offsetLeft);
        } else {
            offsetLeft = Utils.convertPixelsToDp(barLeft - lineLeft);
            mChart1.setExtraLeftOffset(offsetLeft);
        }
  /*注：setExtraRight...函数是针对图表绝对位置计算，比如A表offRightA=20dp,B表offRightB=30dp,则A.setExtraLeftOffset(30),并不是10，还有注意单位转换*/
        if (barRight < lineRight) {
            offsetRight = Utils.convertPixelsToDp(lineRight - barRight);
            mChart2.setExtraRightOffset(offsetRight);
        } else {
            offsetRight = Utils.convertPixelsToDp(barRight - lineRight);
            mChart1.setExtraRightOffset(offsetRight);
        }

        mChart1.setOnChartGestureListener(new TestOnChartGestureListener(mChart1, mChart2));
        mChart2.setOnChartGestureListener(new TestOnChartGestureListener(mChart2, mChart1));
    }

    /**
     * 选中状态联动
     */
    private void linkSelect() {
        mChart1.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                float x = h.getX();
                float y = h.getY();
                float touchY = h.getTouchY();
                int dataIndex = h.getDataIndex();
                int dataSetIndex = h.getDataSetIndex();
                float manualYOffset = mChart1.getHeight() + Utils.convertDpToPixel(0);//表2和表1Y方向的layout偏差

                syncHighlightChart(mChart2, x, y, touchY, dataIndex,
                        dataSetIndex, manualYOffset);
            }

            @Override
            public void onNothingSelected() {
                mChart2.highlightValue(null);
            }
        });
        mChart2.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                float x = h.getX();
                float y = h.getY();
                float touchY = h.getTouchY();
                int dataIndex = h.getDataIndex();
                int dataSetIndex = h.getDataSetIndex();
                float manualYOffset = -(mChart1.getHeight() + Utils.convertDpToPixel(0));//表2和表1Y方向的layout偏差

                syncHighlightChart(mChart1, x, y, touchY, dataIndex,
                        dataSetIndex, manualYOffset);
            }

            @Override
            public void onNothingSelected() {
                mChart1.highlightValue(null);
            }
        });

    }

    /**
     * 根据其他关联图表的选中高亮信息来手动高亮选中
     *
     * @param chart         需要操作的图表
     * @param x             关联图表的高亮信息Hightlight.x
     * @param y             关联图表的高亮信息Hightlight.u
     * @param touchY        关联图表的高亮信息Hightlight.touchY
     * @param dataIndex     关联图表的高亮信息Hightlight.dataIndex
     * @param dataSetIndex  关联图表的高亮信息Hightlight.dataSetIndex
     * @param manualYOffset 当前图表和关联图表的Y坐标差值
     */
    private void syncHighlightChart(Chart chart, float x, float y, float touchY, int dataIndex,
                                    int dataSetIndex, float manualYOffset) {
        float newTouchY = touchY - manualYOffset;
        Highlight highlight = new Highlight(x, y, dataSetIndex);
        highlight.setDataIndex(dataIndex);
        Highlight h1 = chart.getHighlightByTouchPoint(x, newTouchY);
        highlight.setTouchY(newTouchY);
        if (null == h1) {
            highlight.setTouchYValue(0);
        } else {
            highlight.setTouchYValue(h1.getTouchYValue());
        }
        chart.highlightValues(new Highlight[]{highlight});
    }

    private void setSelectMark(CombinedChart chart, boolean showBottom) {
        FloatLabel rightSelectLabel = new FloatLabel(getApplicationContext());
        rightSelectLabel.getLabelText().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
        chart.setRightSelectFloatLabel(rightSelectLabel);

        if (showBottom) {
            FloatLabel bottomSelectLabel = new FloatLabel(getApplicationContext());
            bottomSelectLabel.setPadding(20, 20, 20, 20);
            bottomSelectLabel.getLabelText().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
            chart.setBottomSelectFloatLabel(bottomSelectLabel);
        }

        chart.setHighlightPerDragEnabled(true);
    }

    private void configChart1() {
        mChart1.setBackgroundColor(Color.WHITE);

        mChart1.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart1.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart1.setPinchZoom(false);

        mChart1.setDrawGridBackground(false);

        //TODO test
//        mChart1.setScaleMinima(0.5f, 0.5f);
//        mChart1.setScaleMaxima(5f, 5f);
        mChart1.setFloatYValue(100);
        mChart1.setAutoScaleMinMaxEnabled(true);
        FloatLabel yLabel = new FloatLabel(getApplicationContext());
        yLabel.getLabelText().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
        mChart1.setRightFloatYLabel(yLabel);

        setSelectMark(mChart1, true);

        XAxis xAxis = mChart1.getXAxis();
        xAxis.setLabelCount(X_AXIS_LABEL_COUNT);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setSpaceMin(0.5f);
        xAxis.setSpaceMax(0.5f);

        YAxis rightAxis = mChart1.getAxisRight();
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

        YAxis leftAxis = mChart1.getAxisLeft();
        leftAxis.setEnabled(false);
//        rightAxis.setStartAtZero(false);

        mChart1.getLegend().setEnabled(false);
    }

    private void initData() {
        List<CandleEntry> chart1List = new ArrayList<CandleEntry>();
        List<Entry> chart2List = new ArrayList<Entry>();
        List<BarEntry> barList = new ArrayList<>();

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
            chart2List.add(new Entry(i, close));
            barList.add(new BarEntry(i, close));
        }

        initChart1Data(chart1List);
//        initChart2Data(chart2List, barList);
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

        setSelectMark(mChart2, false);

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

        mChart1.setData(combinedData);

        final int count = chart1List.size();

        zoom(count, count);

        mChart1.postDelayed(new Runnable() {
            @Override
            public void run() {
                float minScale = (float) count / (X_AXIS_LABEL_COUNT * DATA_COUNT_ONE_GRID_MAX);
                float maxScale = (float) count / (X_AXIS_LABEL_COUNT * DATA_COUNT_ONE_GRID_MIN);
                mChart1.setScaleMinima(minScale, 1);
                mChart1.setScaleMaxima(maxScale, 1);
            }
        }, 500);

//        mChart1.moveViewTo(0, 0, YAxis.AxisDependency.RIGHT);
//        mChart1.invalidate();

        String[] values = new String[chart1List.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = "x" + (values.length - i);
        }
        mChart1.getXAxis().setValueFormatter(new IndexAxisValueFormatter(values));
    }

    private void initChart2Data(List<Entry> chart2List, List<BarEntry> barList) {
        LineDataSet dataset = new LineDataSet(chart2List, "LineDataSet");
        //TODO
        dataset.setHighlightEnabled(true);
        dataset.setHighlightLineWidth(2);

        LineData data = new LineData(dataset);

        BarDataSet barDataSet = new BarDataSet(barList, "BarDataSet");
        barDataSet.setHighlightEnabled(false);
        BarData barData = new BarData(barDataSet);

        CombinedData combinedData = new CombinedData();
        combinedData.setData(data);
        combinedData.setData(barData);

        mChart2.setData(combinedData);
        mChart2.invalidate();
    }

    private void zoom(int count, float moveIndex) {

        float zoom = (float) count / (X_AXIS_LABEL_COUNT * DATA_COUNT_ONE_GRID);
//        float xZoom = ((float) count) / (6 * 10);
        Log.i("zoom-------", "" + zoom);
        mChart1.zoom(zoom, 0, moveIndex, 0, YAxis.AxisDependency.RIGHT);
//        mChart1.moveViewTo(count, 1f, YAxis.AxisDependency.RIGHT);
    }

    Handler mHandler = new Handler();

    private void delayLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMore();
            }
        }, 600);
    }

    private void loadMore() {

//        float oldMin = mChart1.getXAxis().getAxisMinimum();
//        float oldMinX = oldMin + mChart1.getXAxis().getSpaceMin();

        mChart1.stopDeceleration();
        mChart1.clearAllViewportJobs();

        CombinedData combinedData = mChart1.getData();
        CandleData candleData = combinedData.getCandleData();
        DataSet candleDataSet = (DataSet) candleData.getDataSetByIndex(0);

        float oldCount = candleDataSet.getEntryCount();

        float oldScaleMinX = mChart1.getViewPortHandler().getMinScaleX();
        float oldScaleMaxX = mChart1.getViewPortHandler().getMaxScaleX();
        float oldScaleX = mChart1.getScaleX();

        List<CandleEntry> oldList = new ArrayList<>(candleDataSet.getValues());
        List<CandleEntry> newList = new ArrayList<>();
        for (float i = 0; i < 100; i++) {
            float mult = (50 + 1);
            float val = (float) (Math.random() * 40) + mult;

            float high = (float) (Math.random() * 9) + 8f;
            float low = (float) (Math.random() * 9) + 8f;

            float open = (float) (Math.random() * 6) + 1f;
            float close = (float) (Math.random() * 6) + 1f;

            boolean even = i % 2 == 0;
            float x = i;

            newList.add(new CandleEntry(
                    x, val + high,
                    val - low,
                    even ? val + open : val - open,
                    even ? val - close : val + close,
                    getResources().getDrawable(R.drawable.star)));
        }

//        for (CandleEntry entry :
//                oldList) {
//            candleDataSet.addEntry(entry);
//        }

        //重新设置原有数据的x值
        int newCount = newList.size();
        for (int i = 0; i < oldList.size(); i++) {
            oldList.get(i).setX(newCount + i);
        }

        candleDataSet.getValues().clear();
        candleDataSet.getValues().addAll(newList);
        candleDataSet.getValues().addAll(oldList);

        candleDataSet.notifyDataSetChanged();
        candleData.notifyDataChanged();
        combinedData.notifyDataChanged();
        mChart1.notifyDataSetChanged();


        //通过前后数据的比例，计算出新的缩放参数
        float ratio = (float) candleDataSet.getEntryCount() / oldCount;

        float newScaleMinX = ratio * oldScaleMinX;
        float newScaleMaxX = ratio * oldScaleMaxX;
        float newScaleX = ratio * oldScaleX;

        mChart1.setScaleMinima(newScaleX, 1);//避免数据修改后改变缩放表现
        mChart1.moveViewTo(newCount - mChart1.getXAxis().getSpaceMin(), 0, YAxis.AxisDependency.RIGHT);

        mChart1.setScaleMinima(newScaleMinX, 1);
        mChart1.setScaleMaxima(newScaleMaxX, 1);
//        zoom(candleDataSet.getEntryCount(), oldMin);

        String[] values = new String[candleDataSet.getEntryCount()];
        for (int i = 0; i < values.length; i++) {
            values[i] = "x" + (values.length - i);
        }
        mChart1.getXAxis().setValueFormatter(new IndexAxisValueFormatter(values));
    }

    private int iii = 5;

    private void change() {
        CombinedData combinedData = mChart1.getData();
        CandleData candleData = combinedData.getCandleData();
        ICandleDataSet candleDataSet = candleData.getDataSetByIndex(0);
        CandleEntry entry = candleDataSet.getEntriesForXValue(99).get(0);
        entry.setOpen(entry.getOpen() + iii);
        iii = -iii;
        candleData.notifyDataChanged();
        combinedData.notifyDataChanged();
        mChart1.notifyDataSetChanged();
        mChart1.invalidate();
    }

    class TestOnChartGestureListener implements OnChartGestureListener {
        private boolean mIsCanLoad = false;

        private Chart chart1, chart2;


        public TestOnChartGestureListener(Chart chart1, Chart chart2) {
            this.chart1 = chart1;
            this.chart2 = chart2;
        }

        @Override
        public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
            mIsCanLoad = false;
        }

        @Override
        public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
            if (!CombinedChart.class.isInstance(chart1)) {
                return;
            }

            CombinedChart loadChart = (CombinedChart) chart1;

            float leftX = loadChart.getLowestVisibleX();    //获取可视区域中，显示在x轴最右边的index

            if (lastPerformedGesture == ChartTouchListener.ChartGesture.DRAG) {
                mIsCanLoad = true;
                if (leftX <= loadChart.getXAxis().getAxisMinimum()) {
                    mIsCanLoad = false;
                    //加载更多数据的操作
                    Log.i(TAG, "onChartGestureEnd: loadloadloadload1" + leftX);
                    loadMore();
                }
            }
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

            if (!CombinedChart.class.isInstance(chart1)) {
                return;
            }

            CombinedChart loadChart = (CombinedChart) chart1;
            if (mIsCanLoad) {
                float leftX = loadChart.getLowestVisibleX();     //获取可视区域中，显示在x轴最右边的index
                if (leftX <= loadChart.getXAxis().getAxisMinimum()) {
                    mIsCanLoad = false;
                    //加载更多数据的操作
                    Log.i(TAG, "onChartGestureEnd: loadloadloadload2" + leftX);
                    loadMore();
                }
            }
        }

        public void onMatrixChange() {


//            Matrix srcMatrix;
//            float[] srcVals = new float[9];
//
//            srcMatrix = chart1.getViewPortHandler().getMatrixTouch();
//            srcMatrix.getValues(srcVals);
//            srcMatrix.getValues(srcVals);
//
//            // apply X axis scaling and position to dst charts:
//            Matrix dstMatrix;
//            float[] dstVals = new float[9];
//            dstMatrix = chart2.getViewPortHandler().getMatrixTouch();
//            dstMatrix.getValues(dstVals);
//
//            dstVals[Matrix.MSCALE_X] = srcVals[Matrix.MSCALE_X];
//            dstVals[Matrix.MSKEW_X] = srcVals[Matrix.MSKEW_X];
//            dstVals[Matrix.MTRANS_X] = srcVals[Matrix.MTRANS_X];
//            dstVals[Matrix.MSKEW_Y] = srcVals[Matrix.MSKEW_Y];
//            dstVals[Matrix.MSCALE_Y] = srcVals[Matrix.MSCALE_Y];
//            dstVals[Matrix.MTRANS_Y] = srcVals[Matrix.MTRANS_Y];
//            dstVals[Matrix.MPERSP_0] = srcVals[Matrix.MPERSP_0];
//            dstVals[Matrix.MPERSP_1] = srcVals[Matrix.MPERSP_1];
//            dstVals[Matrix.MPERSP_2] = srcVals[Matrix.MPERSP_2];
//
//            dstMatrix.setValues(dstVals);
//            chart2.getViewPortHandler().refresh(dstMatrix, chart2, true);
        }
    }
}
