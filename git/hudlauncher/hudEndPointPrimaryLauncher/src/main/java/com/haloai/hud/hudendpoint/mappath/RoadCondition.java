package com.haloai.hud.hudendpoint.mappath;

/**
 * Created by wangshengxing on 16/6/6.
 */
public class RoadCondition {
    private int pathStep = 0;
    private String roadStatus = null;

    public RoadCondition() {

    }

    public RoadCondition(int pathStep, String roadStatus) {
        this.pathStep = pathStep;
        this.roadStatus = roadStatus;
    }

    public int getPathStep() {
        return pathStep;
    }
    public void setPathStep(int pathStep) {
        this.pathStep = pathStep;
    }

    public String getRoadStatus() {
        return roadStatus;
    }

    public void setRoadStatus(String roadStatus) {
        this.roadStatus = roadStatus;
    }
}
