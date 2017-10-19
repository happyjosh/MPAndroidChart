package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;

/**
 * 悬浮的Y轴label
 * Created by jph on 2017/10/9.
 */
public class FloatLabel extends MarkerView {

    private TextView mLabelTxt;

    private static final String LABEL_TAG = "label_tag";


    public FloatLabel(Context context) {
        super(context);
        mLabelTxt = createTextView(context);
        setupView(mLabelTxt);
    }

    public TextView getLabelText() {
        return mLabelTxt;
    }

    static TextView createTextView(Context context) {
        TextView txt = new TextView(context);
        txt.setTag(LABEL_TAG);
        txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        txt.setTextColor(Color.BLACK);
        return txt;
    }
}
