package com.haloai.hud.hudendpoint.mappath;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wangshengxing on 16/5/24.
 */
public class HudPathStep {
    public static class RoadStatus{
        private int distance;
        private String status;

        public RoadStatus() {
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = Integer.parseInt(distance);
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return distance+","+status;
        }
    }
    private List<Point> points;
    private String road;
    private Point roadPoint;
    private List<RoadStatus> roadStatuses;

    private int pathStep = 0;

    public HudPathStep() {
//        points = new LinkedList<>();
//        roadStatuses = new ArrayList<>();
    }

    public int getPathStep() {
        return pathStep;
    }

    public void setPathStep(int pathStep) {
        this.pathStep = pathStep;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public Point getRoadPoint() {
        return roadPoint;
    }

    public void setRoadPoint(Point roadPoint) {
        this.roadPoint = roadPoint;
    }

    public List<RoadStatus> getRoadStatuses() {
        return roadStatuses;
    }

    public void setRoadStatuses(List<RoadStatus> roadStatuses) {
        this.roadStatuses = roadStatuses;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{ ");
        if (roadStatuses != null) {
            stringBuilder.append("状态点："+roadStatuses.size());
            stringBuilder.append(roadStatuses.toString());
        }
        if (points != null) {
            stringBuilder.append("["+"经过点"+points.size()+"]");
        }
        stringBuilder.append("},");
        return stringBuilder.toString();

    }
}

