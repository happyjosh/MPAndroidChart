package com.github.mikephil.charting.formatter;

import com.github.mikephil.charting.components.AxisBase;

/**
 * Created by jph on 2017/11/13.
 */
public interface IPreviousAxisFormatter {


    /**
     * 得到格式后的坐标显示
     *
     * @param previousValue 上一个的值
     * @param value         当前值
     * @param axis
     * @return
     */
    String getFormattedValue(float previousValue, float value, AxisBase axis);
}
