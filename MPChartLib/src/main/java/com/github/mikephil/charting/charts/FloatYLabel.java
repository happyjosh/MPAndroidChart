package com.github.mikephil.charting.charts;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.R;
import com.github.mikephil.charting.components.MarkerView;

/**
 * 悬浮的Y轴label
 * Created by jph on 2017/10/9.
 */
public class FloatYLabel extends MarkerView {

    private TextView mLabelTxt;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     */
    public FloatYLabel(Context context) {
        super(context, R.layout.float_y_label);
        mLabelTxt = (TextView) findViewById(R.id.label_txt);
    }

    public void setText(String txt) {
        mLabelTxt.setText(txt);
    }

    public TextView getLabelText() {
        return mLabelTxt;
    }
}
