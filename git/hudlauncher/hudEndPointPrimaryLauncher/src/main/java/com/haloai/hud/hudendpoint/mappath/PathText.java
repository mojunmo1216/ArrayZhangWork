package com.haloai.hud.hudendpoint.mappath;

import android.graphics.Point;

/**
 * Created by wangshengxing on 16/6/7.
 */
public class PathText {
    private String text;
    private Point position;
    private boolean cuttable;

    public PathText(String text, Point position, boolean cuttable) {
        this.text = text;
        this.position = position;
        this.cuttable = cuttable;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public boolean isCuttable() {
        return cuttable;
    }

    public void setCuttable(boolean cuttable) {
        this.cuttable = cuttable;
    }
}
