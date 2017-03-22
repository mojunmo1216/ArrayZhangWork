package com.haloai.hud.hudendpoint.mappath;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

import com.haloai.hud.utils.HaloLogger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by wangshengxing on 16/5/27.
 */
public class RectUtils {
    private static final String TAG = RectUtils.class.getSimpleName();
    private static final String WSX = "wangshengxing";
    public static class RectMapPara
    {
        private Rect rect;
        private Point refPoint;
        private double scalefactor;
        private int widthMove;
        private int heightMove;

        public Rect getRect() {
            return rect;
        }
    }

    public static Rect keepRectInside(Rect rect, float x, float y, Rect size){
        Rect result = null;

        return result;
    }
    public static List<Point> rectRemap(List<Point> points, RectMapPara rectMapPara){
        Point refPoint = rectMapPara.refPoint;
        double scalefactor = rectMapPara.scalefactor;
        int widthMove = rectMapPara.widthMove;
        int heightMove = rectMapPara.heightMove;
        List<Point> newPointList = new ArrayList<>(points.size());
        int newX ,newY;
        for(Point point: points){
            newX = (int)((point.x-refPoint.x)/scalefactor)+widthMove;
            newY = (int)((point.y-refPoint.y)/scalefactor)+heightMove;
            Point newPoint = new Point(newX,newY);
            newPointList.add(newPoint);
        }
        return newPointList;
    }

    public static void rectRemap(List<Point> newPointList,List<Point> points, RectMapPara rectMapPara){
        Point refPoint = rectMapPara.refPoint;
        double scalefactor = rectMapPara.scalefactor;
        int widthMove = rectMapPara.widthMove;
        int heightMove = rectMapPara.heightMove;
        int newX ,newY;
        for(Point point: points){
            newX = (int)((point.x-refPoint.x)/scalefactor)+widthMove;
            newY = (int)((point.y-refPoint.y)/scalefactor)+heightMove;
            Point newPoint = new Point(newX,newY);
            newPointList.add(newPoint);
        }
    }

    public static Point rectRemapPoint(Point srcPoint,RectMapPara rectMapPara){
        Point refPoint = rectMapPara.refPoint;
        int newX ,newY;
        newX = (int)((srcPoint.x-refPoint.x)/rectMapPara.scalefactor)+rectMapPara.widthMove;
        newY = (int)((srcPoint.y-refPoint.y)/rectMapPara.scalefactor)+rectMapPara.heightMove;
        Point newPoint = new Point(newX,newY);
        return newPoint;
    }


    public static RectMapPara measureRect(int width, int height, List<Point> points, Rect margin){
        if (points == null || width ==0 || height ==0) {
            return null;
        }
        Rect maxRect = points2Rect(points);

        int widthMove,heightMove;
        double widthScalefactor = 1, heightScalefactor =1,scalefactor =1;
        Point startPoint = points.get(0);
        Point endPoint = points.get(points.size()-1);
        Point refPoint = new Point(maxRect.left,maxRect.top);//
        widthScalefactor = (maxRect.width()*1.0)/(width- margin.left-margin.right);
        heightScalefactor = (maxRect.height()*1.0)/(height-margin.top-margin.bottom);
        if(widthScalefactor==0 ||widthScalefactor==0){
            return null;
        }
        //缩放比例要大的一个，说明缩放还几乎满屏，另一个则要居中处理
        if(widthScalefactor>heightScalefactor){
            scalefactor = widthScalefactor;
            widthMove = 0;
            heightMove = ((height-margin.left-margin.right)-((int)(maxRect.height()/scalefactor)))/2;
        }else{
            scalefactor = heightScalefactor;
            widthMove = ((width-margin.left-margin.right)-((int)(maxRect.width()/scalefactor)))/2;
            heightMove = 0;
        }
        widthMove = widthMove+margin.left;
        heightMove = heightMove+margin.top;
        RectMapPara rectMapPara = new RectMapPara();
        rectMapPara.refPoint=refPoint;
        rectMapPara.scalefactor=scalefactor;
        rectMapPara.heightMove =heightMove;
        rectMapPara.widthMove=widthMove;
        rectMapPara.rect = maxRect;
        return  rectMapPara;
    }
    /**
     * 缩放矩形
     */
    public static void marginRect(Rect rect, Rect margin ){
        if (rect != null) {
            rect.left += margin.left;
            rect.right -= margin.right;
            rect.top += margin.top;
            rect.bottom -= margin.top;
        }
    }
    /**
     * 找出点所构成的最大矩形
     */
    public static Rect points2Rect(List<Point> points){
        Rect result = null;
        if (points == null || points.size()<=0) {
            return null;
        }
        int maxLeft,maxTop,maxRight,maxBottom;//取名风格以左下为参照
        maxLeft = Integer.MAX_VALUE;
        maxRight = points.get(0).x;
        maxTop = Integer.MAX_VALUE;
        maxBottom = points.get(0).y;
        for (Point point: points) {
            maxLeft = Math.min(point.x,maxLeft);
            maxRight = Math.max(point.x,maxRight);
            maxBottom = Math.max(point.y,maxBottom);
            maxTop = Math.min(point.y,maxTop);
        }
        result = new Rect(maxLeft,maxTop,maxRight,maxBottom);
        HaloLogger.logI(WSX,"points2Rect ,得到的矩形为"+result);
        return result;
    }
    /**
     * 确定一系列点，与矩形的分布方向
     * @return int 0x00000001 是否为右 0x00000020 是否为上
     *
     */
    public static int pointsOrientation(Rect rect , List<Point> pointList){
        if (rect == null || pointList == null || pointList.size()<=0) {
            return -1;
        }
        int centerHorizontal = (rect.left+rect.right)/2;
        int centerVertical  = (rect.top+rect.bottom)/2;
        int leftCnt,topCnt,rightCnt,bottomCnt;
        leftCnt=topCnt=rightCnt=bottomCnt=0;
        for (Point point:pointList) {
            if(point.x<centerHorizontal){
                leftCnt++;
            }else if(point.x>centerHorizontal){
                rightCnt++;
            }
            if(point.y<centerVertical){
                topCnt++;
            }else if(point.y>centerVertical){
                bottomCnt++;
            }
        }
        int result = 0;
        if(leftCnt<rightCnt){
            result |= Orientation.RIGHT_ALIGN;
        }
        if (topCnt<bottomCnt){
            result |= Orientation.BOTTOM_ALIGN;
        }
        return result;
    }
    /**
     * 以某个方向，在一堆点里面找到与该点相邻的相反方向的最大的矩形
     */
    public static Rect getNearMaxRect(List<Point> pointList, Point refPoint, Rect rectSize, Orientation.Basic orientation) {
        Rect result = null;
        if (pointList == null || pointList.size()<=0) {
            return new Rect(rectSize);
        }
        Point pre=null, next=null;
        int val = 0;
        int left=0, top=0, right=0, bottom=0;
        final int cnt = pointList.size();
        //比较出区域值
        Point point = null;
        for (int i = 0; i < cnt; i++) {
            point = pointList.get(i);
            if (refPoint.equals(point)) {
                val = i - 1;
                if(val >= 0 && val < pointList.size()){
                    pre = pointList.get(val);
                }else {
                    pre = point;
                }

                val = i + 1;
                if((val >= 0) && (val < pointList.size())) {
                    next = pointList.get(val);
                }else{
                    next = point;
                }
                break;
            }
        }
        HaloLogger.logI(WSX,"getNearMaxRect,all point is "+pointList);
        HaloLogger.logI(WSX,"getNearMaxRect,pre is "+pre+",next is "+next+",now is "+point);
        if (orientation == Orientation.Basic.Horizontal) {
            top = Math.min(next.y,pre.y);
            bottom = Math.max(next.y,pre.y);
            left = Math.min(rectSize.left,rectSize.right);
            right = Math.max(rectSize.left,rectSize.right);
        } else if (orientation == Orientation.Basic.Vertical) {
            left = Math.min(next.x,pre.x);
            right = Math.max(next.x,pre.x);
            top = Math.min(rectSize.top,rectSize.bottom);
            bottom = Math.max(rectSize.top,rectSize.bottom);
        }
        result = new Rect(left, top, right, bottom);
        return result;
    }

    /**
     * 以某个方向，在一堆点里面找到与该点相反方向的最大的矩形
     */
    public static Rect getMaxRect(List<Point> pointList, Point refPoint, Rect rectSize, Orientation.Basic orientation) {
        Rect result = null;
        int baseX = refPoint.x, baseY = refPoint.y;
        int left = baseX, right = baseX;
        int top = baseY, bottom = baseY;
        int minToRight = Integer.MAX_VALUE, minToBottom = Integer.MAX_VALUE;
        final int cnt = pointList.size();
        int diffX = 0, diffY = 0;
        //比较出区域值
        for (int i = 0; i < cnt; i++) {
            Point point = pointList.get(i);
            if (orientation == Orientation.Basic.Horizontal) {
                if (baseY > point.y) {
                    top = Math.max(top, point.y);
                } else {
                    diffY = point.y - baseY;
                    if (minToBottom < diffY) {
                        bottom = point.y;
                    }
                }
            } else if (orientation == Orientation.Basic.Vertical) {
                if (baseX > point.x) {
                    left = Math.max(left, point.x);
                } else {
                    diffX = point.x - baseX;
                    if (minToRight < diffX) {
                        right = point.x;
                    }
                }
            }
        }
        if (orientation == Orientation.Basic.Horizontal) {
            result = new Rect(rectSize.left, top, rectSize.right, bottom);
        } else if (orientation == Orientation.Basic.Vertical) {
            result = new Rect(left, rectSize.top, right, rectSize.bottom);
        }
        return result;
    }

    /**
     * 在一堆点里面与某个不包括最近点最大的矩形
     */
    public static Rect getMaxRect(List<Point> pointList, Point refPoint) {
        int baseX = refPoint.x, baseY = refPoint.y;
        int left = baseX, right = baseX;
        int top = baseY, bottom = baseY;
        int minToRight = Integer.MAX_VALUE, minToBottom = Integer.MAX_VALUE;
        final int cnt = pointList.size();
        int diffX, diffY ;
        //比较出区域值
        for (int i = 0; i < cnt; i++) {
            Point point = pointList.get(i);
            if (baseX > point.x) {
                left = Math.max(left, point.x);
            } else {
                diffX = point.x - baseX;
                if (minToRight < diffX) {
                    right = point.x;
                }

            }
            if (baseY > point.y) {
                top = Math.max(top, point.y);
            } else {
                diffY = point.y - baseY;
                if (minToBottom < diffY) {
                    bottom = point.y;
                }

            }
        }
        Rect result = new Rect(left, top, right, bottom);
        return result;
    }

    /**
     * 判断点是否在一个矩形内
     */

    public static boolean isIncludePoint(Rect rect, Point point) {
        boolean result = false;

        return result;
    }

    /**
     * 在当前第一矩形中取出与第二个相交的部分
     */
    public static Rect intersect(Rect firstRect, Rect secondRect) {
        int left, top, right, bottom;
        left = Math.max(firstRect.left, secondRect.left);
        top = Math.max(firstRect.top, secondRect.top);
        right = Math.min(firstRect.right, secondRect.right);
        bottom = Math.min(firstRect.bottom, secondRect.bottom);
        Rect result = new Rect(left, top, right, bottom);
        return result;
    }

    /***
     * 与path相关的状态
     */
    public static void intersectState(Rect rect, Path path) {

    }

    /***
     * 按方向移动矩形
     */
    public static void moveRect(Rect rect, int moveH, int moveV) {
        if (rect != null) {
            rect.left += moveH;
            rect.right += moveH;
            rect.top += moveV;
            rect.bottom += moveV;
        }
    }

    /**
     * 转换成Rect
     * **/

    public static Rect toRect( RectF rectF){
        Rect result = new Rect();
        result.left = (int)rectF.left;
        result.top = (int)rectF.top;
        result.right = (int)rectF.right;
        result.bottom = (int)rectF.bottom;
        return result;
    }

    /**
     * 旋转一个点
     * **/
    public static Point rotatePoint(float degree,Point refPoint,Point point) {
        Point rotatePoint = new Point();
        Matrix matrix = new Matrix();
        matrix.postRotate(degree,refPoint.x,refPoint.y);
        float[] xy = new float[2];
        matrix.mapPoints(xy,new float[]{point.x,point.y});
        rotatePoint.x = (int) xy[0];
        rotatePoint.y = (int) xy[1];
        return rotatePoint;
    }

    /**
     * 以某个对照点旋转一个矩形，
     * **/
    public static RectF rotateRect(float degree,Point refPoint,RectF rect) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree,refPoint.x,refPoint.y);
        RectF resultF = new RectF();
        matrix.mapRect(resultF,new RectF(rect));
        return resultF;
    }
    /**
     * 以某个对照点旋转一个矩形，最后仍水平展现
     * **/
    public static Rect rotateHorizontalRect(float degree,Point refPoint,Rect rect) {
        Point srcPoint = new Point();
        srcPoint.x = rect.left;
        srcPoint.y = rect.top;
        Point rotatePoint = rotatePoint(degree,refPoint,srcPoint);
        Rect result;
        if (degree >= 90 && degree <= 270) {
            result = new Rect(rotatePoint.x-rect.width(),rotatePoint.y,rotatePoint.x,rotatePoint.y+rect.height());
        } else {
            result = new Rect(rotatePoint.x,rotatePoint.y,rotatePoint.x+rect.width(),rotatePoint.y+rect.height());
        }
        return result;

    }
}
