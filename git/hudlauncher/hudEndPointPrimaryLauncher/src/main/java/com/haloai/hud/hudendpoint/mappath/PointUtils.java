package com.haloai.hud.hudendpoint.mappath;

import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;

import com.haloai.hud.utils.HaloLogger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wangshengxing on 16/5/27.
 */
public class PointUtils {

    private static final String TAG = PointUtils.class.getName();
    private static final String WSX = "wangshengxing";

    /**
     * 找到在矩形中的点
     */

    public static List<Point> insideRect(Rect rect, List<Point> points){

        if (points == null || points.size()<=0) {
            return null;
        }
        List<Point> result = new ArrayList<>();
        for (Point point:points){
            if(rInsideRect(rect,point)){
                result.add(new Point(point));
            }
        }
        return result;
    }

    /**
     * 判断点在矩形中
     */
    public static boolean rInsideRect(Rect rect, Point point){
        return  ( (point.x>=rect.left && point.x<=rect.right)&& (point.y>=rect.top && point.y<=rect.bottom));
    }
    public static boolean insideRect(Rect rect, Point point){
        if (rect == null || point == null) {
            return false;
        }
        return  ( (point.x>=rect.left && point.x<=rect.right)&& (point.y>=rect.top && point.y<=rect.bottom));
    }
    /**
     * 过滤相临点小于一定距离的点
     */
    public static List<Point> filterPoint(List<Point> srcPoints, int radius){
        if (srcPoints == null || srcPoints.size()<=0) {
            return null;
        }
        Point lastPoint = srcPoints.get(0);
        return filterPoint(lastPoint,srcPoints,radius);
    }

    /**
     * 以step的模式过滤点
     * 每个step的最后一个点加入上一个step的第一个点
     * @param srcPoints
     * @param radius
     * @return
     */
    public static List<List<Point>> filterStepPoint(List<List<Point>> srcPoints, int radius){
        if (srcPoints == null || srcPoints.size()<=0) {
            return null;
        }
        final int critical = radius*radius;
        int stepSize = srcPoints.size();
        int perStepSize = 0;
        List<List<Point>> filterStepPoints = new ArrayList<>(stepSize);
        for (int i = 0; i < stepSize; i++) {
            List<Point> stepPoints = srcPoints.get(i);
            List<Point> nextStepPoints = null;
            if(stepSize>(i+1)){
                nextStepPoints = srcPoints.get(i+1);
            }
            perStepSize = stepPoints.size();
            HaloLogger.logE("filterStepPoint",String.format("0 step size is %s",stepPoints.size()));
            if(perStepSize>0){
                Point startPoint = stepPoints.get(0);
                Point lastPoint = new Point(startPoint);
                List<Point> filterPoints = new ArrayList<>();
                filterPoints.add(startPoint);//加入起点
                for (int j = 0; j < perStepSize ; j++) {
                    HaloLogger.logE("filterStepPoint",String.format("1 step size is %s",stepPoints.size()));
                    Point point = stepPoints.get(j);
                    if(powDistance(lastPoint,point)>=critical){
                        filterPoints.add(point);
                        lastPoint = point;
                    }
                }
                if (nextStepPoints != null && nextStepPoints.size()>0) {
                    filterPoints.add(nextStepPoints.get(0));//加入起点
                }
                filterStepPoints.add(filterPoints);
            }


        }
        return filterStepPoints;
    }

    /**
     * 过滤相临点小于一定距离的点
     */
    public static List<Point> filterPoint(Point startPoint,List<Point> srcPoints, int radius){
        if (srcPoints == null || srcPoints.size()<=0) {
            return null;
        }
        final int initSize = srcPoints.size()/radius;
        List<Point> filterPoints = null;
        if (initSize>0){
            filterPoints  = new ArrayList<>(initSize);
        }else {
            filterPoints  = new ArrayList<>();
        }
        Point lastPoint = new Point(startPoint);
        filterPoints.add(startPoint);
        final int critical = radius*radius;
        for (int i = 0; i <srcPoints.size() ; i++) {
            Point point = srcPoints.get(i);
            if(powDistance(lastPoint,point)>=critical){
                filterPoints.add(point);
                lastPoint = point;
            }
        }
        return filterPoints;
    }

    /**
     * 两点距离平方和
     */
    public static int powDistance(Point a, Point b){
        int diffX = Math.abs(a.x-b.x);
        int diffY = Math.abs(a.y-b.y);
        return diffX*diffX+diffY*diffY;
    }
    /**
     * 得到点之间的path
     */
    public static Path points2Path(List<Point> points){
        if (points == null || points.size()<=0) {
            return null;
        }
        Path pointsPath = new Path();
        Point moveTo = points.get(0);
        pointsPath.moveTo(moveTo.x,moveTo.y);
        for (int j = 0; j <points.size() ; j=j+1) {
            Point toPoint = points.get(j);
            pointsPath.lineTo(toPoint.x,toPoint.y);
        }
        return pointsPath;
    }
    /**
     * 得到点之间的path
     */
    public static Path points2MovePath(List<Point> points, float moveH, float moveV){
        if (points == null || points.size()<=0) {
            return null;
        }
        Path pointsPath = new Path();
        Point moveTo = points.get(0);
        pointsPath.moveTo(moveTo.x,moveTo.y);
        for (int j = 0; j <points.size() ; j=j+1) {
            Point toPoint = points.get(j);
            pointsPath.lineTo(toPoint.x+moveH,toPoint.y+moveV);
        }
        return pointsPath;
    }


    /**
     * 得到点之间的path
     */
    public static void points2MovePath(Path pointsPath,List<Point> points, float moveH, float moveV){
        if (points == null || points.size()<=0) {
            return;
        }
        Point moveTo = points.get(0);
        pointsPath.moveTo(moveTo.x,moveTo.y);
        for (int j = 0; j <points.size() ; j=j+1) {
            Point toPoint = points.get(j);
            pointsPath.lineTo(toPoint.x+moveH,toPoint.y+moveV);
        }
    }

    /**
     * 得到点之间的path,没有moveto操作
     */
    public static void points2LinePath(Path pointsPath,List<Point> points, float moveH, float moveV){
        if (points == null || points.size()<=0) {
            return;
        }
        for (int j = 0; j <points.size() ; j=j+1) {
            Point toPoint = points.get(j);
            pointsPath.lineTo(toPoint.x+moveH,toPoint.y+moveV);
        }
    }



    /**
     * 得到点之间的闭区间的path
     */
    public static Path points2ClosePath(List<Point> points, float moveH, float moveV){
        if (points == null || points.size()<=0) {
            return null;
        }
        Path closePath = points2MovePath(points,-moveH,-moveV);
        for (int j = points.size()-1; j >=0 ; j--) {
            Point toPoint = points.get(j);
            closePath.lineTo(toPoint.x+moveH,toPoint.y+moveV);
        }
        return closePath;
    }

    /**
     * 得到两点之间的角度 以逆时针方向，0-360
     */
    public static double degree(Point p1,Point p2){
        double angle = 0;
        int diffX,diffY;
        diffX = p2.x - p1.x;
        diffY = p2.y - p1.y;
        if (diffX==0){
            if (diffY>0){
                return 90;
            }else {
                return -90;
            }
        }else if(diffY==0){
            if (diffX>=0){
                return 0;
            }else {
                return 180;
            }
        }else {
            double k = (1.0f*diffY)/diffX;   //斜率
            double rad  = Math.atan(k); //注意这个角度的范围是 [-pi/2..pi/2], 不是0到90°
            angle = (rad*180)/Math.PI;
            if(diffY>0 && diffX<0){
                angle += 180;
            }else if(diffY<0 && diffX<0){
                angle += 180;
            }else if(diffY<0 && diffX>0){
                angle += 360;
            }
        }
        return  angle;
    }

    public static void main(String args[]) {
        double angle = 0;
        angle = degree(new Point(0,0),new Point(1,1));
        System.out.println("角度为: " +angle);
    }
}
