package com.haloai.hud.hudendpoint.mappath;

import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.List;

/**
 * Created by wangshengxing on 16/5/24.
 */
public class PathUtils {

    /***
     * 加入点集合
     */

    public static void  addPoints(Path path, List<Point> points){
        if (points == null || points.size()<=0 || path == null) {
            return ;
        }
        for (Point point:points){
            path.addCircle(point.x,point.y,1, Path.Direction.CCW);
        }
    }

    /***
     * 加入Rect
     */
    public static void addRect(Path path, Rect rect) {
        if (path != null || rect == null) {
            path.addRect((new RectF(rect)), Path.Direction.CCW);
        }
    }

}
