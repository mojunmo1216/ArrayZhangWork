package com.haloai.hud.hudendpoint.mappath;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;

import com.haloai.hud.utils.HaloLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by wangshengxing on 16/5/26.
 */
public class PathRectManager {

    private static final String TAG = "PathRectManager";
    private static final String WSX = "wangshengxing";

    private static final boolean POINTS_REGION_UNDRAW = true;

    private static  float[] mLeftDegreeFromTo = new float[]{135,225};
    private static  float[] mTopDegreeFromTo = new float[]{225,315};
    private static  float[] mRightDegreeFromTo = new float[]{315,405};
    private static  float[] mBottomDegreeFromTo = new float[]{45,135};
    private static  float[] mCenterDegreeFromTo = new float[]{0,0};

    private boolean mTextAwayPath = false;
    private boolean mIsRotateDetect = true;
    private Canvas mCanvas;
    private Paint mOutlinePaint;

    private List<Path> mUndrawRegions;
    private List<Path> mBasicUndrawRegions;
    private List<RectResponse> mRectResponseList;
    private List<Point> mRectPoint; //矩形左上点的集合
    private List<Point> mPathPoints; //路径的点集合
    private List<RectLayoutStatus> mRectLayoutStatusList;
    private Rect mPointsRegion;
    private Rect mRegionRect; //总区域的大小

    private Path mRoutePath;
    private class RectLayoutStatus{
        private boolean done = false;
        private RectOrientation orientation;

    }
    private class PointsRectStatus{
        private boolean isVertical; //是否为纵向
        private boolean isHorizontal; //是否横向
        private boolean verticalEnough;
        private boolean horizontalEnough;

        public PointsRectStatus() {
            defaultInit();
        }

        public void defaultInit(){
            isVertical = false;
            isHorizontal = false;
            verticalEnough = false;
            horizontalEnough = false;
        }
    }

    private PointsRectStatus mPreLineStatus = new PointsRectStatus();
    private PointsRectStatus mNextLineStatus = new PointsRectStatus();


    public PathRectManager() {
        mUndrawRegions = new ArrayList<>();
        mRectResponseList = new ArrayList<>(20);
        mRectPoint = new ArrayList<>();
        mRectLayoutStatusList = new ArrayList<>();
        mBasicUndrawRegions = new ArrayList<>();
        mPathPoints = new LinkedList<>();

        mTextAwayPath = false;

        defaultInit();
    }

    private void defaultInit(){
        mOutlinePaint = new Paint();
        Paint testPaint = mOutlinePaint;
        testPaint = new Paint();
        testPaint.setStrokeWidth(1);
        testPaint.setColor(Color.RED);
        testPaint.setStyle(Paint.Style.STROKE);
    }
    public enum RectType{
        TextRect,
    }
    public enum RectOrientation{
        LETT,
        UP,
        RIGHT,
        BOTTOM,
        CENTER,
    }

    private class CheckRectRequest{
        private Point refPoint;
        private Rect size;
        private RectOrientation orientation;
        private int range;
        private int margin;
        private List<Point> unDrawPoints;

        public CheckRectRequest(Point refPoint, Rect size, RectOrientation orientation, int range, int margin, List<Point> unDrawPoints) {
            this.refPoint = refPoint;
            this.size = size;
            this.orientation = orientation;
            this.range = range;
            this.margin = margin;
            this.unDrawPoints = unDrawPoints;
        }
    }

    public void setCanvas(Canvas canvas) {
        this.mCanvas = canvas;
    }

    private class CheckRectResponse{
        private Rect rect;
        private RectOrientation orientation;

        public CheckRectResponse(Rect rect, RectOrientation orientation) {
            this.rect = rect;
            this.orientation = orientation;
        }
    }
    public static class RectResponse{
        private Rect rect;
        private int index;

        public Rect getRect() {
            return rect;
        }

        public int getIndex() {
            return index;
        }
    }
    public static class RectRequest{
        public RectRequest() {
            type = RectType.TextRect;
        }
        private RectType type;
        private Point point;
        private int direction;
        private Rect minRect;
        private int marginHorizontal;
        private int marginVertical;

        public RectType getType() {
            marginHorizontal = 0;
            marginVertical = 0;
            return type;
        }

        public void setType(RectType type) {
            this.type = type;
        }

        public Point getPoint() {
            return point;
        }

        public void setPoint(Point point) {
            this.point = point;
        }

        public int getDirection() {
            return direction;
        }

        public void setDirection(int direction) {
            this.direction = direction;
        }

        public Rect getMinRect() {
            return minRect;
        }

        public void setMinRect(Rect minRect) {
            this.minRect = minRect;
        }
    }
    public void setAwayPath(boolean isAway){
        this.mTextAwayPath = isAway;
    }

    public List<RectResponse> findRect(List<RectRequest> rectRequests,int counter,int step){
        mRectResponseList.clear();
        mRectPoint.clear();
        mUndrawRegions.clear();

        for (RectRequest request:rectRequests){
            Point point = request.getPoint();
            mRectPoint.add(new Point(point));
        }
        HaloLogger.logI(TAG,"所有点为："+mRectPoint);
        mUndrawRegions.addAll(mBasicUndrawRegions);
        layoutAllRectAwayPath(rectRequests,counter,step);
        return mRectResponseList;
    }
    /**
     *  在区域中找出想着的矩形区域
     *  步骤：
     *  1、
     *
     */

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public List<RectResponse> findRect(List<RectRequest> rectRequests){
        mRectResponseList.clear();
        mRectPoint.clear();
        mUndrawRegions.clear();

        for (RectRequest request:rectRequests){
            Point point = request.getPoint();
            mRectPoint.add(new Point(point));
        }
        HaloLogger.logI(TAG,"所有点为："+mRectPoint);
        mUndrawRegions.addAll(mBasicUndrawRegions);
        if (mTextAwayPath){
            layoutAllRectAwayPath(rectRequests);
        }else {
            layoutAllRect(rectRequests);
        }
//        mPointsRegion = measurePoints(mRectPoint);
        for (int i = 0; i < 0; i++) {//rectRequests.size()
            Path intersectPath = new Path();
            RectRequest rectRequest = rectRequests.get(i);

            //寻找合适矩形的策略
            Rect fitRect = rCalculateRect(rectRequest,i);
            Path currentPath = new Path();
            currentPath.addRect(new RectF(fitRect), Path.Direction.CCW);

            boolean calculateOk = false;
            for (int pathIndext = 0; pathIndext < mUndrawRegions.size(); pathIndext++){
                Path path = mUndrawRegions.get(pathIndext);
                if (intersectPath.op(currentPath,path, Path.Op.INTERSECT)){//计算成功
                    if(intersectPath.isEmpty()) {//未相交  path.op(currentPath,Path.Op.INTERSECT)
                        if(pathIndext == (mUndrawRegions.size()-1)){
                            calculateOk = true;
                        }
                    }else {
                        // HaloLogger.logI(TAG,"绘制文本与PATH相交");
                        calculateOk = false;
                        break;
                    }
                }else{
                    //HaloLogger.logI(TAG,"相交计算不成功");
                }
            }
            if(mUndrawRegions.size()<=0){
                calculateOk = true;
            }
            if (calculateOk){//测量成功
                HaloLogger.logI(TAG,"完成绘制文本："+i);
                RectResponse rectResponse = new RectResponse();
                rectResponse.rect = fitRect;
                rectResponse.index = i;
                mRectResponseList.add(rectResponse);
                mUndrawRegions.add(currentPath);
//                break;
            }
        }
        return mRectResponseList;
    }
    /**
     *  每次根据当前区域的使用情况，取出一个最符合要求的矩形
     *  递归依据：路径形状，取矩形点分布，所有需要摆放矩形块
     *  步骤：
     *  1、根据点所处的路径方向处理：横向则优先上下选择矩形，纵则优先左右选择矩形
     *  2、如果与上一个点之间能取则取，不行则往下一个点走，再不行则放弃
     *      与上一个点关系：
     *          纵向：向左能生长则优先向左，向上能生长则向上
     *          横向：向上能生长则向上，向左能生长则优先向左，
     *      以上都生长不了，则取与下一个点的空间
     *          下一个点的生长情况：
     *
     *  3、困定生长方案：每次取区域：如果纵向空间够，则按向左或向右生长，横向也则上下生长
     *  4、变长生长方案：取定生成方向后，取一定范围值的矩形,一旦成功后，需要检测大体顺序关系，即点和矩形对应的横纵关系要统一
     *
     *  返回：为空则说明取不出矩形
     *
     */

    private Rect rCalculateRect(final RectRequest rectRequest, final int index){
        Point refPoint = rectRequest.point;
        Rect rectSize = rectRequest.minRect;
        Rect rect = null;
        boolean inflict = true;
        //困定生长方案
        calculatePointRelation(rectRequest,index);
        for (int i = 0; i <4 ; i++) { //N策略个数
            //判断与上一个点的关系
            /*if(mPreLineStatus.isVertical ){ //纵向
                if(mPreLineStatus.isVertical){//上面空间够,向上生长
                    rect = getRectByOrientation(refPoint,rectSize,RectOrientation.UP,0);
                    inflict = isRectInflict(rect);
                    if (inflict){//与其它区域出现了冲突，
                        mPreLineStatus.isVertical = false;
                    }
                }else {//只能向下生长
                    rect = getRectByOrientation(refPoint,rectSize,RectOrientation.BOTTOM,0);
                    inflict = isRectInflict(rect);
                    if (inflict){//与其它区域出现了冲突，
                        mPreLineStatus.isVertical = false;
                    }
                }
            }else if(mPreLineStatus.isHorizontal) {

            }*/
            /*if (mNextLineStatus.isVertical){
                if(mNextLineStatus.isVertical){//上面空间够,向上生长
                    rect = getRectByOrientation(refPoint,rectSize,RectOrientation.UP,0);
                    inflict = isRectInflict(rect);
                    if (inflict){//与其它区域出现了冲突，
                        mNextLineStatus.isVertical = false;
                    }
                }else  {//只能向下生长

                }
            }else if(mNextLineStatus.isHorizontal){

            }*/

            if (!inflict){
                break;
            }
        }
        //固定方案不成功，浮动检测
        if (inflict){
            calculatePointRelation(rectRequest,index);
//            circularGetRect(refPoint,rectSize,RectOrientation.UP,0);
        }

        if (inflict){
            return null;
        }
        return rect;
    }

    /**
     *
     *  得到的矩形会与路相交
     *  步骤：
     *  1、获取地图的方向
     *  2、前当前方向内取出可取的矩形
     *  3、在交叉方向取出可取矩形
     *  4、以伸展方向取出剩下矩形
     *
     */

    public void layoutAllRect(final List<RectRequest> rectRequests){
        mRectLayoutStatusList.clear();
        for (RectRequest request:rectRequests){
            mRectLayoutStatusList.add(new RectLayoutStatus());

        }
        Rect pointsRect =  measurePoints(mRectPoint);
        if(pointsRect.height()>pointsRect.width()){
            layoutRect(rectRequests,true,false);
            layoutRect(rectRequests,false,false);
        }else {
            layoutRect(rectRequests,false,false);
            layoutRect(rectRequests,true,false);
        }

    }
    /****
     *  得到所有的矩形，矩形不与path路径相交
     *   *  步骤：
     *  1、获取地图的方向
     *  2、以地图当前的方向内取出可取的矩形
     *      计算相邻两个点构成的最大矩形
     *      移动textRect，当 与path相交时，根据相交的方向，交替 完成layout计算
     *  3、以获取地图的方向的相反方向取出可取矩形，进行位置规划
     *
     *  4、以地图当前的方向，在整个方向内，取出中中心点最近的可取的矩形
     *
     *  5、以获取地图的方向的相反方向，在整个方向内，取出中中心点最近的可取的矩形
     *
     */
    public void layoutAllRectAwayPath(final List<RectRequest> rectRequests){
        mRectLayoutStatusList.clear();
        for (RectRequest request:rectRequests){
            mRectLayoutStatusList.add(new RectLayoutStatus());

        }
        Rect pointsRect =  measurePoints(mRectPoint);
        final boolean isVertical = pointsRect.height()>pointsRect.width();
        int range = 0;
        for (int i = 0; i < 10; i++) {
            layoutRectAwayPath(rectRequests,isVertical,range);
            layoutRectAwayPath(rectRequests,!isVertical,range);
            range += 20;
        }

    }

    public void layoutAllRectAwayPath(final List<RectRequest> rectRequests,int counter ,int step){
        mRectLayoutStatusList.clear();
        for (RectRequest request:rectRequests){
            mRectLayoutStatusList.add(new RectLayoutStatus());
        }
        Rect pointsRect =  measurePoints(mRectPoint);
        final boolean isVertical = pointsRect.height()>pointsRect.width();
        int range = 0;
        for (int i = 0; i < counter; i++) {
            layoutRectAwayPath(rectRequests,isVertical,range);
            layoutRectAwayPath(rectRequests,!isVertical,range);
            range += step;
        }

    }

    /****
     * 以某种方式，查找一次矩形
     *
     */
    private  void layoutRectAwayPath(final List<RectRequest> rectRequests, final boolean isVertival, final int range){
        List<CheckRectRequest> checkRectRequestList = new ArrayList<>();
        List<Point> mLayoutRectPoint = new ArrayList<>();
        final int cnt = mRectPoint.size();
        List<Point> unDrawPoints = new ArrayList<>();

        for (int i = 0; i < cnt; i++) {
            Point point = mRectPoint.get(i); //按距离处理
            boolean isOk = mRectLayoutStatusList.get(i).done;//是否已经分配矩形
            int index = findListEquals(mLayoutRectPoint, point);
            if (!isOk) {
                unDrawPoints.clear();
                for (int k = 0; k <mRectPoint.size() ; k++) {
                    if (k != i){
                        Point udrawPoint = mRectPoint.get(k);
                        unDrawPoints.add(udrawPoint);
                    }
                }
//                Rect nearMaxRect = RectUtils.getNearMaxRect(mRectPoint, point, mRegionRect, Orientation.Basic.Vertical);
//                Rect textRegion = new Rect();
//                Path regionPath = new Path();
//                PathUtils.addRect(regionPath,mRegionRect);
//                mCanvas.drawPath(regionPath,mOutlinePaint);

                HaloLogger.logI(TAG, "不允许画矩形的点："+unDrawPoints);
//                unDrawPoints.clear();
//                unDrawPoints.add(new Point(0,0));
                RectRequest request = rectRequests.get(i);
                //分配矩形策略
                checkRectRequestList.clear();
                Rect rectSize = new Rect(0,0,request.minRect.width()+request.marginHorizontal,
                        request.minRect.height()+request.marginVertical);
                if (isVertival){
                    HaloLogger.logI(TAG, "纵向获取矩形");
//                    checkRectRequestList.add(new CheckRectRequest(request.point, rectSize, RectOrientation.CENTER,range,4,unDrawPoints));
                    checkRectRequestList.add(new CheckRectRequest(request.point, rectSize, RectOrientation.LETT,range,4,unDrawPoints));
                    checkRectRequestList.add(new CheckRectRequest(request.point, rectSize, RectOrientation.RIGHT,range,4,unDrawPoints));
                }else{
                    HaloLogger.logI(TAG, "横向获取矩形");
                    checkRectRequestList.add(new CheckRectRequest(request.point, rectSize, RectOrientation.UP,range,4,unDrawPoints));
                    checkRectRequestList.add(new CheckRectRequest(request.point, rectSize, RectOrientation.BOTTOM,range,4,unDrawPoints));
//                    checkRectRequestList.add(new CheckRectRequest(request.point, rectSize, RectOrientation.CENTER,range,4,unDrawPoints));
                }
                CheckRectResponse checkRectResponse = checkRectByStrategys(checkRectRequestList);
                if (checkRectResponse != null) {
                    RectResponse rectResponse = new RectResponse();
                    rectResponse.rect = checkRectResponse.rect;
                    rectResponse.index = i;
                    mRectLayoutStatusList.get(i).done = (checkRectResponse.rect != null); //确认已经layout完成
                    mRectLayoutStatusList.get(i).orientation = checkRectResponse.orientation;
                    mRectResponseList.add(rectResponse);

//                    mRectResponseList.add(rect);//记录矩形
                    HaloLogger.logI(TAG, "成功获取矩形："+checkRectResponse.rect+",第"+i+",中心点" + point+" 策略为："+checkRectResponse.orientation);
//                        mRectPoint.remove(point);//下次则不用计算该点
                }
            }
            //不管是否已经分配区域，都放弃
            if (index >= 0 && index < mLayoutRectPoint.size()) {
                mLayoutRectPoint.remove(index);
            }
        }

    }


    /****
     * 以某种方式，查找一次矩形
     *
     */
    private  void layoutRect(final List<RectRequest> rectRequests, final boolean isVertival, final boolean isMove){
        List<CheckRectRequest> checkRectRequestList = new ArrayList<>();
        List<Point> mLayoutRectPoint = new ArrayList<>();
        for (RectRequest request:rectRequests){
            mLayoutRectPoint.add(new Point(request.point));
        }

        Collections.sort(mLayoutRectPoint, new Comparator<Point>() {
            @Override
            public int compare(Point lhs, Point rhs) {
                Integer compareA,compareB;
                if (isVertival){
                    compareA = new Integer(lhs.y);
                    compareB = new Integer(rhs.y);
                }else{
                    compareA = new Integer(lhs.x);
                    compareB = new Integer(rhs.x);
                }
                return 0;//compareA.compareTo(compareB)
            }
        });

        final int cnt = mRectPoint.size();
        List<Point> unDrawPoints = new ArrayList<>();
        for (int i = 0; i < cnt; i++) {
            Point point = mRectPoint.get(i); //按距离处理
            boolean isOk = mRectLayoutStatusList.get(i).done;//是否已经分配矩形
            int index = findListEquals(mLayoutRectPoint, point);
            if (!isOk) {
                unDrawPoints.clear();
                for (int k = 0; k <mRectPoint.size() ; k++) {
                    if (k != i){
                        Point udrawPoint = mRectPoint.get(k);
                        unDrawPoints.add(udrawPoint);
                    }
                }
                HaloLogger.logI(TAG, "不允许画矩形的点："+unDrawPoints);
//                unDrawPoints.clear();
//                unDrawPoints.add(new Point(0,0));
                RectRequest request = rectRequests.get(i);
                //分配矩形策略
                checkRectRequestList.clear();
                Rect rectSize = new Rect(0,0,request.minRect.width()+request.marginHorizontal,
                        request.minRect.height()+request.marginVertical);
                if (isVertival){
                    HaloLogger.logI(TAG, "纵向获取矩形");
                    checkRectRequestList.add(new CheckRectRequest(request.point, rectSize, RectOrientation.CENTER,0,4,unDrawPoints));
                    checkRectRequestList.add(new CheckRectRequest(request.point, rectSize, RectOrientation.LETT,0,4,unDrawPoints));
                    checkRectRequestList.add(new CheckRectRequest(request.point, rectSize, RectOrientation.RIGHT,0,4,unDrawPoints));
                }else{
                    HaloLogger.logI(TAG, "横向获取矩形");
                    checkRectRequestList.add(new CheckRectRequest(request.point, rectSize, RectOrientation.UP,0,4,unDrawPoints));
                    checkRectRequestList.add(new CheckRectRequest(request.point, rectSize, RectOrientation.BOTTOM,0,4,unDrawPoints));
                    checkRectRequestList.add(new CheckRectRequest(request.point, rectSize, RectOrientation.CENTER,0,4,unDrawPoints));
                }
                CheckRectResponse checkRectResponse = checkRectByStrategys(checkRectRequestList);
                if (checkRectResponse != null) {
                    RectResponse rectResponse = new RectResponse();
                    rectResponse.rect = checkRectResponse.rect;
                    rectResponse.index = i;
                    mRectLayoutStatusList.get(i).done = (checkRectResponse.rect != null); //确认已经layout完成
                    mRectLayoutStatusList.get(i).orientation = checkRectResponse.orientation;
                    mRectResponseList.add(rectResponse);

//                    mRectResponseList.add(rect);//记录矩形
                    HaloLogger.logI(TAG, "成功获取矩形："+checkRectResponse.rect+",第"+i+",中心点" + point+" 策略为："+checkRectResponse.orientation);
//                        mRectPoint.remove(point);//下次则不用计算该点
                }
            }
            //不管是否已经分配区域，都放弃
            if (index >= 0 && index < mLayoutRectPoint.size()) {
                mLayoutRectPoint.remove(index);
            }
        }

    }
    /**
     *
     * **/
    private CheckRectResponse checkRectByStrategys(List<CheckRectRequest> checkRectRequests){
        Rect rect = null;
        CheckRectResponse checkRectResponse = null;
        for (CheckRectRequest checkRectRequest:checkRectRequests){
            rect = getCheckRect(checkRectRequest.refPoint, checkRectRequest.size,
                    checkRectRequest.orientation, checkRectRequest.range,checkRectRequest.margin,checkRectRequest.unDrawPoints);
            if (rect != null) {
                checkRectResponse = new CheckRectResponse(rect,checkRectRequest.orientation);
                break;
            }
        }
        return checkRectResponse;
    }

    private int findListEquals(List<Point> list , Point point){
        int result = -1;
        for (int i = 0; i < list.size(); i++) {
            Point dPoint = list.get(i);
            if (point.equals(dPoint)){
                result = i;
                break;
            }
        }
        return result;
    }

    /**
     * 检测点与点之间的关系
     * **/
    private void calculatePointRelation(final RectRequest rectRequest, int index){
        Point refPoint = rectRequest.point;
        Rect rectSize = rectRequest.minRect;
//        Rect triRegion = measurePoints(mRectPoint,index-1,index+1);
        Rect preLine = measurePoints(mRectPoint,index-1,index);
        Rect nextLine = measurePoints(mRectPoint,index,index+1);

        setPointRelation(rectRequest,preLine,mPreLineStatus);
        setPointRelation(rectRequest,nextLine,mNextLineStatus);

    }
    /**
     * 检测点与点之间的关系
     * **/
    private void setPointRelation(final RectRequest rectRequest, Rect lineSize, PointsRectStatus rectStatus){
        Rect rectSize = rectRequest.minRect;
        if (lineSize != null) {
            rectStatus.isVertical =(lineSize.height()>lineSize.width());
            rectStatus.isHorizontal =(lineSize.height()<lineSize.width());
            rectStatus.verticalEnough = (lineSize.height()>rectSize.height());
            rectStatus.horizontalEnough = (lineSize.width()>rectSize.width());
        }else{
            rectStatus.defaultInit();
        }
    }
    /**
     * 检测矩形大体顺序关系，即点和矩形对应的横纵关系是否统一
     * **/
    private boolean checkRectsOrderOk(Rect rect){
        boolean result = true;
        return result;
    }
    /**
     * 循环在一定的范围内获取矩形，并检测通过
     * **/
    private Rect circularGetRect(Point refPoint, Rect rectSize, RectOrientation orientation, int range){
        Rect rect = null;

        return rect;
    }

    public boolean checkPointsIntersect(final Path path, List<Point> unDrawPoints){
        boolean result = false;
        if (POINTS_REGION_UNDRAW&& unDrawPoints != null && unDrawPoints.size()>0){
            List<Path> unDrawPaths = new ArrayList<>();
            for (Point point:unDrawPoints){
                Path unDrawPath = new Path();
                unDrawPath.addCircle(point.x,point.y,0.01F, Path.Direction.CCW);
                unDrawPaths.add(unDrawPath);
            }
            result = checkPathIntersect(path,unDrawPaths);
        }
        return result;
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public boolean checkPathIntersect(final Path path, final List<Path> regions){
        boolean result = true;
        if (regions != null && regions.size()>0) {
            Path intersectPath = new Path();
            for (int pathIndext = 0; pathIndext < regions.size(); pathIndext++){
                intersectPath.reset();
                Path checkPath = regions.get(pathIndext);
                if (intersectPath.op(checkPath,path, Path.Op.INTERSECT)){//计算成功
                    if(intersectPath.isEmpty()) {//未相交  path.op(currentPath,Path.Op.INTERSECT)
                        if(pathIndext == (regions.size()-1)){
                            result = false;
                        }
                    }else {
                        HaloLogger.logI(TAG,"绘制文本与PATH相交");
                        result = true;
                        break;
                    }
                }else{
                    HaloLogger.logI(TAG,"相交计算不成功");
                }
            }
        }else{
            result = false;
        }
        return result;
    }

    /**
     * 以方向和父矩形来获取可用矩形
     * **/
    public Rect getVerticalCheckRect(Rect region, Rect rectSize, int step, int start){
        Rect rect =null;
        int cnt = rectSize.height()/step;
        for (int i = 0; i <cnt ; i++) {

        }

        return rect;
    }



    private float[] getFromToDegree(RectOrientation orientation){
        float[] result = null;
        switch (orientation){
            case LETT:
                result = mLeftDegreeFromTo;
                break;
            case UP:
                result = mTopDegreeFromTo;
                break;
            case RIGHT:
                result = mRightDegreeFromTo;
                break;
            case BOTTOM:
                result = mBottomDegreeFromTo;
                break;
            case CENTER:
                result = mCenterDegreeFromTo;
                break;
        }
        return result;
    }

    /**
     * 以方向和偏移值来获取可用矩形
     * **/
    public Rect getCheckRect(Point refPoint, Rect rectSize, RectOrientation orientation, int range, int margin, List<Point> unDrawPoints){

        Rect rect =null;
        Path path = new Path();
        if (mIsRotateDetect){
            Point rectLeftTop = new Point(refPoint.x+range+margin,refPoint.y);
            Rect srcRect = new Rect(rectLeftTop.x,rectLeftTop.y,rectLeftTop.x+rectSize.width(),rectLeftTop.y+rectSize.height());
            float[] degreeFromTo = getFromToDegree(orientation);
            float midDegree = (degreeFromTo[0]+degreeFromTo[1])/2;
            float degreeStep = 10;
            float degree = midDegree;
            for (int i = 0; degree>degreeFromTo[0] && degree<degreeFromTo[1] ; i++) {
                degree = (i%2==0) ? midDegree+((i+1)/2)*degreeStep : midDegree-((i+1)/2)*degreeStep;
                rect = RectUtils.rotateHorizontalRect(degree,refPoint,srcRect);
                if (rect != null) {
                    path.reset();
                    path.addRect(new RectF(rect), Path.Direction.CCW);
                    if(checkPathIntersect(path, mUndrawRegions)||checkPointsIntersect(path,unDrawPoints)){
                        rect = null;
                    }else {
                        mUndrawRegions.add(path);//未相交则加入
                        break;
                    }
                }
            }
        }else {
            final int stepPixel = 2;
            final int cnt = (range==0)?1:range/stepPixel;
            int moveVal = 0;
            for (int i = 0; i < cnt; i++) {
                rect =getRectByOrientation(refPoint,rectSize,orientation,moveVal+margin);
                if (rect != null) {
                    path.reset();
                    path.addRect(new RectF(rect), Path.Direction.CCW);
                    if(checkPathIntersect(path, mUndrawRegions)||checkPointsIntersect(path,unDrawPoints)){
                        rect = null;
                    }else {
                        mUndrawRegions.add(path);//未相交则加入
                        break;
                    }
                }
                moveVal += stepPixel;
            }
        }
        return rect;
    }
    public Rect getRectByOrientation(Point refPoint, Rect rectSize, RectOrientation orientation, int move){
        Rect rect = null;
        final int TEXT_MARGIN_WIDTH = 15;
        final int TEXT_MARGIN_HEIGHT = 15;
        int moveH =0,moveV=0;

        Point point = new Point(refPoint);
        point.x -= rectSize.width()/2;
        point.y -= rectSize.height()/2;
        switch (orientation){
            case LETT:
                moveH = -rectSize.width()/2-move;
                break;
            case UP:
                moveV =  -rectSize.height()/2-move;
                break;
            case RIGHT:
                moveH = +rectSize.width()/2+move;
                break;
            case BOTTOM:
                moveV =  +rectSize.height()/2+move;
                break;
            case CENTER:
                moveH=0;
                moveV=0;
                break;
            default:
                break;

        }
        point.x +=moveH;
        point.y +=moveV;
        rect = new Rect(point.x,point.y,point.x+rectSize.width(),point.y+rectSize.height());
        return rect;
    }
    /**
     * 判断与当前矩形是否撞在一起；
     * **/
    private boolean isRectInflict(Rect rect){
        boolean inflict = true;
        return inflict;
    }
    /**
     *  计算点内最大的矩形
     *  已经做了防止越界处理
     *  @return 如果长宽都为0返回null
     *
     */
    public static Rect measurePoints(List<Point> points, int start, int end){
        boolean hasData = false;
        int maxLeft,maxTop,maxRight,maxBottom;//取名风格以左下为参照
        maxLeft = points.get(0).x;
        maxRight = maxLeft;
        maxTop = points.get(0).y;
        maxBottom = maxTop;
        start = start>=0?start:0;
        end = end>=0?end:0;
        final int cnt = Math.min(points.size()-1,end);
        HaloLogger.logI("measurePoints","取点的内容为：start = "+start+",cnt:"+cnt);
        for (int i = start; i <=cnt ; i++) {
            Point point = points.get(i);
            maxLeft = Math.min(point.x,maxLeft);
            maxRight = Math.max(point.x,maxRight);
            maxBottom = Math.max(point.y,maxBottom);
            maxTop = Math.min(point.y,maxTop);
            hasData = true;
        }
        Rect region;
        if (hasData){
            region = new Rect(maxLeft,maxTop,maxRight,maxBottom);
            HaloLogger.logI("measurePoints","点分布的矩形为："+region+",长宽为:"+region.width()+","+region.height());
        }else {
            region = null;//new Rect(0,0,0,0)
        }
//        HaloLogger.logI("measurePoints","点分布的矩形为:左上右下"+maxLeft+","+maxTop+","+maxRight+","+maxBottom);

        return region;
    }
    public static Rect measurePoints(List<Point> points){
        return measurePoints(points,0,points.size());
    }

    /**
     * 设置路径和文本的规划区域
     * **/

    public void setRegion(int width, int height){
        mRegionRect = new Rect(0,0,width,height);
        addUndrawRegion(width,height);
    }
    /**
     * 以矩形外为path
     * **/

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public List<Path> addUndrawRegion(int width, int height){
        int margin = 500;
        //计算不能标注的位置
        Path path1 = new Path();
        path1.addRect(0,0,width,height, Path.Direction.CCW);
        Path windowPath = new Path();
        windowPath.addRect(-margin,-margin,width+margin,height+margin, Path.Direction.CCW);
        windowPath.op(path1, Path.Op.DIFFERENCE);
//        windowPath.addRect(width-230,height-40,width,height, Path.Direction.CCW);//开始导航的框
        mBasicUndrawRegions.add(windowPath);
        return mBasicUndrawRegions;
    }
    public void clearUndrawRegion(){
        mBasicUndrawRegions.clear();
    }

    /**
     * 加入路径的Path 点
     * **/
    public List<Path> setFilterPathPoints(List<Point> points){
        mPathPoints.clear();
        mPathPoints.addAll(points);
        mRoutePath = PointUtils.points2ClosePath(points, 1, 1);
        return mBasicUndrawRegions;
    }

    /**
     * 加入不能画文本的path
     * **/
    public List<Path> addUndrawRegion(Path path){
        mBasicUndrawRegions.add(path);
        return mBasicUndrawRegions;
    }
    /**
     * 加入不能画文本的矩形
     * **/
    public List<Path> addUndrawRegion(Point refPoint, int width, int heigth){
        Path path = new Path();
        path.addRect(new RectF(refPoint.x,refPoint.y,refPoint.x+width,refPoint.y+heigth), Path.Direction.CCW);
        return addUndrawRegion(path);
    }

    public static Rect getTextRect(String content, Paint textPaint){
        Rect rect = new Rect();
        textPaint.getTextBounds(content,0,content.length(),rect);
        return rect;
    }

    public void drawUndrawRegion(Canvas canvas, Paint paint){
        for (int i = 0; i < mBasicUndrawRegions.size(); i++) {
            Path path = mBasicUndrawRegions.get(i);
            canvas.drawPath(path,paint);
        }
    }

}
