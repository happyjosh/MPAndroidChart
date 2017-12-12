package com.github.mikephil.charting.charts;

/**
 * 一直悬浮的线的配置信息
 * Created by jph on 2017/12/12.
 */
public class FloatLimitLineConfig {

    private int lineColor;
    private float lineWidth;
    private boolean enableDashLine;
    private float dashLineLength;
    private float dashSpaceLength;
    private float dashPhase;

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    public boolean isEnableDashLine() {
        return enableDashLine;
    }

    public void setEnableDashLine(boolean enableDashLine) {
        this.enableDashLine = enableDashLine;
    }

    public float getDashLineLength() {
        return dashLineLength;
    }

    public void setDashLineLength(float dashLineLength) {
        this.dashLineLength = dashLineLength;
    }

    public float getDashSpaceLength() {
        return dashSpaceLength;
    }

    public void setDashSpaceLength(float dashSpaceLength) {
        this.dashSpaceLength = dashSpaceLength;
    }

    public float getDashPhase() {
        return dashPhase;
    }

    public void setDashPhase(float dashPhase) {
        this.dashPhase = dashPhase;
    }
}
