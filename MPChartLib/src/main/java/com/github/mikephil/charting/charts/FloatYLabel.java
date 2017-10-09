package com.github.mikephil.charting.charts;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;

/**
 * 悬浮的Y轴label
 * Created by jph on 2017/10/9.
 */
public abstract class FloatYLabel extends MarkerView {

//    private TextView mLabelTxt;


    public FloatYLabel(Context context, int layoutResource) {
        super(context, layoutResource);
    }

    public abstract TextView getLabelText();
}
