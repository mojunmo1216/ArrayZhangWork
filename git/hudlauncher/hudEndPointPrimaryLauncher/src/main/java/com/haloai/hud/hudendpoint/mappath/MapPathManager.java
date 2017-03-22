package com.haloai.hud.hudendpoint.mappath;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Handler;
import android.text.TextPaint;
import android.widget.ImageView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveStep;
import com.haloai.hud.hudendpoint.HudEndPointConstants;
import com.haloai.hud.hudendpoint.arwaylib.utils.ARWayProjection;
import com.haloai.hud.hudendpoint.arwaylib.utils.jni_data.LatLngOutSide;
import com.haloai.hud.hudendpoint.primarylauncher.R;
import com.haloai.hud.hudendpoint.utils.DisplayUtil;
import com.haloai.hud.hudendpoint.utils.FileUtils;
import com.haloai.hud.utils.HaloLogger;
import com.haloai.huduniversalio.utils.JsonTool;

import org.json.JSONArray;
import org.json.JSONObject;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wangshengxing on 16/5/31.
 */
public class MapPathManager {

    private static final String TAG = "MapPathManager";
    private static final double SCREEN_SCALE_LEVEL = 19;
    private static final boolean IS_PATH_STEP_DRAW     = false;

    private ImageView mStrategyRouteView;

    private ARWayProjection mAMapProjection = new ARWayProjection();

    private static final float MAX_DP_WIDTH_STRATGE_PATH = 294.66f;
    private static final float MAX_DP_HEIGHT_STRATGE_PATH = 154.66f;
    private static final int MAX_DISPLAY_ROAD = 3;
    private static final int MAX_DISPLAY_TEXT_LENGTH = 15;

    private static final boolean PATH_DEBUG_MODE = false;
    private static final boolean CONDITION_DEBUG_MODE = true;

    private RectUtils.RectMapPara mRectMapPara;
    private float mDesplayDesity;
    private PathRectManager mPathRectManager = new PathRectManager();


    private Bitmap mRoutePathBitmap;
    private Canvas mCanvas;

    private Paint mTextPaint;

    private final static int MAX_POINT_NUMBER = 1000;
    List<List<Point>> mStepsFilterPoints = new LinkedList<>();
    private List<NaviLatLng> mPathLatLngs = new ArrayList<NaviLatLng>();
    private List<RoadCondition> mStepRoadConditions = new LinkedList<>();
    private List<Point> mPathPoints;
    private List<Integer> mValidStep;
    private List<List<Point>> mPathStepPoints;
    private List<Point> mRemapPathPoints;
    private List<List<Point>> mRemapPathStepPoints;
    private List<Point> mFilterPathPoints;
    private List<List<Point>> mFilterPathStepPoints;
    private List<HudPathStep> mHudPathSteps;
    private Paint mPathPaint = new Paint(Color.YELLOW);
    private DrivePath mCurrentDrivePath;

    public static final int MESSAGE_SRATEGY_ROAD_CONDITION = 1;
    public static final int MESSAGE_SRATEGY_ROAD_PATH = 2;
    public static final int MESSAGE_SRATEGY_ROAD_TEXT = 3;

    private Context mContext;

    private Point pathStartPoint;
    private float startMarkAngle;
    private Point startMarkPoint;
    private Point endMarkPoint;
    private Bitmap endMark;
    private Bitmap startMark;

    private Point mStartPoint;
    private Point mEndPoint;

    //线程更新内容
    private List<PathRectManager.RectResponse> mTextRectResponses;
    private List<PathText> mPathTexts;

    public MapPathManager(Context context, ImageView view) {
        mContext = context;
        mStrategyRouteView = view;
        initStrategyPath();
        initAmapWebKey();
        initPaint();
    }

    public void initPaint() {
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setStrokeWidth(DisplayUtil.dip2px(mContext, 8f));
        mTextPaint.setTextSize(DisplayUtil.dip2px(mContext, 14f));
        mTextPaint.setColor(Color.WHITE);
    }

    private void initAmapWebKey() {
        ApplicationInfo appInfo = null;
        try {
            appInfo = mContext.getPackageManager()
                    .getApplicationInfo(mContext.getPackageName(),
                            PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void initStrategyPath() {
        mDesplayDesity = mContext.getResources().getDisplayMetrics().density;
        if (mRoutePathBitmap == null) {
            mRoutePathBitmap = Bitmap.createBitmap((int) (MAX_DP_WIDTH_STRATGE_PATH * mDesplayDesity),
                    (int) (MAX_DP_HEIGHT_STRATGE_PATH * mDesplayDesity), Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mRoutePathBitmap);
            mPathRectManager.setCanvas(mCanvas);
        }
//        endMark = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.route_strategy_map_end);
//        startMark = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.route_strategy_map_start);
        endMark = FileUtils.zoomImg(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.route_strategy_map_end), DisplayUtil.dip2px(mContext, HudEndPointConstants.MAPPATH_END_ICON_WIDTH), DisplayUtil.dip2px(mContext, HudEndPointConstants.MAPPATH_END_ICON_HEIGHT));
        startMark = FileUtils.zoomImg(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.route_strategy_map_start), DisplayUtil.dip2px(mContext, HudEndPointConstants.MAPPATH_START_ICON_WIDTH), DisplayUtil.dip2px(mContext, HudEndPointConstants.MAPPATH_START_ICON_HEIGHT));
    }

    /*public void setAMapProjection(Projection projection) {
        mAMapProjection = projection;
    }*/

    private final Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {

                case MESSAGE_SRATEGY_ROAD_CONDITION:
                     updateRoadCondition();
                    break;
                case MESSAGE_SRATEGY_ROAD_PATH:
                    if (mOnCreateStrategyBitmaListener != null) {
                        mOnCreateStrategyBitmaListener.onCreateStrategyBitmap(mRoutePathBitmap);
                    }
                    //mStrategyRouteView.setImageBitmap(mRoutePathBitmap);
                    break;
                case MESSAGE_SRATEGY_ROAD_TEXT:
//                    drawPathTextResult();
                    break;
            }
        }
    };

    public void setDefaultView() {
        mCanvas.drawColor(Color.BLACK);
        mStrategyRouteView.setImageBitmap(mRoutePathBitmap);
    }

    /**
     * displayRoutePath
     * 根据搜索服务中的结果来绘路径,在子线程中运行
     *
     * @param drivePath
     */
    private void rDisplayRoutePath(final DrivePath drivePath, final String destination) {
        if(drivePath == null || drivePath.getSteps() == null){
            return;
        }
        mCurrentDrivePath = drivePath;
        List<RoadPath> roadPathList = new LinkedList<>();
        List<DriveStep> driveSteps = new LinkedList<>();
        //计算相同路径
        String lastRoad = "";
        int roadDistance = 0, linkIndex = 0;
        List<LatLonPoint> roadLatLonPoints = new LinkedList<>();
        int pointsSize = 0;
        int pathSetpSize = drivePath.getSteps().size();
        //计算对象的大小
        for (DriveStep driveStep : drivePath.getSteps()) {
            if (driveStep != null) {
                pointsSize += driveStep.getPolyline().size();
            }
        }
        final int maxPointsLength = Math.min(MAX_POINT_NUMBER, pointsSize);
        final int JUMP_CNT = pointsSize / maxPointsLength;
        int jumpCnt = 0;
        HaloLogger.logI(TAG, "对应策略的总距离为" + drivePath.getDistance() + ",step个数:" + pathSetpSize + ",经过点个数:" + pointsSize);

        if (mPathPoints != null && mPathPoints.size() > 0) {
            mPathPoints.clear();
            mRemapPathPoints.clear();
        }
        mPathStepPoints = new ArrayList<>(pathSetpSize);
        mPathPoints = new ArrayList<>(pointsSize);
        mRemapPathPoints = new ArrayList<>(pointsSize);
        mValidStep = new ArrayList<>();
        // TODO: 16/5/25 最后一个step未记录

        for (int stepCnt = 0; stepCnt < pathSetpSize ; stepCnt++) {
            DriveStep driveStep = drivePath.getSteps().get(stepCnt);
            if (driveStep != null) {
                List<Point> stepPoints = new ArrayList<>();
                for (LatLonPoint polyline : driveStep.getPolyline()) {
                    if ((++jumpCnt >= JUMP_CNT)) {
                        jumpCnt = 0;
                        LatLngOutSide latLng = new LatLngOutSide(polyline.getLatitude(),polyline.getLongitude());
                        if (latLng != null && mAMapProjection != null) {
                            // TODO: 16/9/27
                            Point point = mAMapProjection.toScreenLocation(latLng,SCREEN_SCALE_LEVEL);
//                            Point point = mAMapProjection.toScreenLocation(latLng,19);
                            mPathPoints.add(point);
                            stepPoints.add(point);
//                            HaloLogger.logI(TAG, "增加一个点");
                            //// TODO: 16/10/10  1.保存step 的list 
                            
                        }
                    }
                }
                if(stepPoints.size()>0){
                    mPathStepPoints.add(stepPoints);
                    mValidStep.add(new Integer(stepCnt));
                }
                driveSteps.add(driveStep);
                //计算相同路径
                String str = driveStep.getRoad();
                String roadName = (str == null || str.equals("")) ? "无名路" : str;
                if (0 == linkIndex) {//第一个step不直接记录，只备忘
                    lastRoad = roadName;//保证不为null
                    roadDistance = (int) driveStep.getDistance();//记录当前路距离
                    roadLatLonPoints.addAll(driveStep.getPolyline());
                } else {
                    //路名相同，叠加路径长度，
                    if (lastRoad.equals(roadName) && roadName != null) {
                        roadDistance = roadDistance + (int) driveStep.getDistance();
                        roadLatLonPoints.addAll(driveStep.getPolyline());
                    } else {
                        RoadPath roadPath = new RoadPath();
                        roadPath.distance = roadDistance;
                        roadPath.road = lastRoad;//记录上一个路名
                        //记录中间点
                        if (roadLatLonPoints.size() > 0) {
                            roadPath.latLonPoint = roadLatLonPoints.get(roadLatLonPoints.size() / 2);//roadLatLonPoints.size()/2
                            roadLatLonPoints.clear();
//                            HaloLogger.logI(TAG,"整段路，路名为："+roadPath.road+" 距离为："+roadPath.distance);
                        }
                        roadPathList.add(roadPath);
                        roadLatLonPoints.addAll(driveStep.getPolyline());
                        roadDistance = (int) driveStep.getDistance();//记录当前路距离
                        lastRoad = roadName;//保证不为null

                    }
                }
//				HaloLogger.logI(TAG,"step路名为："+driveStep.getRoad()+" 距离为："+driveStep.getDistance());
                linkIndex++;
            }
        }
        int width = DisplayUtil.dip2px(mContext, MAX_DP_WIDTH_STRATGE_PATH);
        int height = DisplayUtil.dip2px(mContext, MAX_DP_HEIGHT_STRATGE_PATH);
        Rect marginRect = new Rect(10, 30, 30, 10);
        mRectMapPara = RectUtils.measureRect(width, height, mPathPoints, marginRect);
        RectUtils.rectRemap(mRemapPathPoints, mPathPoints, mRectMapPara);
        // TODO: 16/10/10 2.重新映射每个step
        if(IS_PATH_STEP_DRAW) {
            int stepSize = mPathStepPoints.size();
            mRemapPathStepPoints = new ArrayList<>(stepSize);
            for (int i = 0; i < stepSize; i++) {
                List<Point> points = mPathStepPoints.get(i);
                if (points != null && points.size() > 0) {
                    List<Point> remapStepPoints = new ArrayList<>(points.size());
                    RectUtils.rectRemap(remapStepPoints, mPathStepPoints.get(i), mRectMapPara);
                    mRemapPathStepPoints.add(remapStepPoints);
                }
            }

            // TODO: 16/10/10 3.过滤点
            mFilterPathStepPoints = PointUtils.filterStepPoint(mRemapPathStepPoints,6);
        }
        mFilterPathPoints = PointUtils.filterPoint(mRemapPathPoints, 6);

        List<PathText> pathTexts = new ArrayList<>();
        RoadPath[] roadPathArray = new RoadPath[roadPathList.size()];
        for (int i = 0; i < roadPathList.size(); i++) {
            RoadPath tRoadPath = roadPathList.get(i);
            roadPathArray[i] = tRoadPath;
        }
        for (int i = 0; i < roadPathArray.length; i++) {
            for (int j = i; j < roadPathArray.length; j++) {
                if (roadPathArray[i].distance < roadPathArray[j].distance) {
                    RoadPath tRoadPath = roadPathArray[i];
                    roadPathArray[i] = roadPathArray[j];
                    roadPathArray[j] = tRoadPath;
                }
            }
            if (i >= MAX_DISPLAY_ROAD) {
                break;
            }
         //   LatLng latLng = AmapPathUtils.latLonPoint2LatLng(roadPathArray[i].latLonPoint);
            LatLngOutSide latLng = new LatLngOutSide(roadPathArray[i].latLonPoint.getLatitude(),roadPathArray[i].latLonPoint.getLongitude());
            Point remapTextPoint = RectUtils.rectRemapPoint(mAMapProjection.toScreenLocation(latLng,SCREEN_SCALE_LEVEL), mRectMapPara);
            PathText pathText = new PathText(roadPathArray[i].road, remapTextPoint, true);
            pathTexts.add(pathText);
        }

//        HaloLogger.logI(TAG,"经过的路为："+loadNames);
        HaloLogger.logI(TAG, "Path 过滤前的点个数: " + mPathPoints.size());
        HaloLogger.logI(TAG, "Path 过滤后的点个数: " + mFilterPathPoints.size());

        mStartPoint = mRemapPathPoints.get(0);
//        List<Point> endPoins = mHudPathSteps.get(mHudPathSteps.size()-1).getPoints();
        mEndPoint = mRemapPathPoints.get(mRemapPathPoints.size() - 1);


        HaloLogger.logI(TAG, "开始绘制");
        // TODO: 16/10/10 4.先获取路况数据，在绘制前
//        parseRouteCondition(drivePath.getSteps());

        Bitmap srcBitmap = mRoutePathBitmap;
        mCanvas.drawColor(Color.BLACK);
        // TODO: 16/10/10 5.重新绘制路径，包括路况 
        Bitmap bitmap = drawPathStep(mCanvas, mHudPathSteps, srcBitmap);
        drawRoadCondition(mCanvas,srcBitmap);
//        displayRouteCondition(drivePath);
        HaloLogger.logI(TAG, "完成路径绘制");
        drawDestination(destination, mRemapPathPoints, mCanvas);

        drawPathText(mCanvas, pathTexts, bitmap);
        HaloLogger.logI(TAG, "完成文本绘制");

        mHandler.sendEmptyMessage(MESSAGE_SRATEGY_ROAD_PATH);

        /*driveSteps.clear();
        roadPathList.clear();*/

    }



    /**
     * displayRoutePath
     * 根据搜索服务中的结果来绘路径
     *
     * @param drivePath
     */
    public void displayRoutePath(final DrivePath drivePath, String destination) {
     /* new Thread(new Runnable() {
            @Override
            public void run() {
                rDisplayRoutePath(drivePath);
            }
        }).start();*///异步绘制路径
        rDisplayRoutePath(drivePath, destination);
    }




    private void parseRouteCondition(List<DriveStep> driveSteps) {
        // TODO: 16/10/13 未实现
        /*if (driveSteps == null || driveSteps.size()<=0) {
            return;
        }
        int jam=0, slow=0, unknown=0, go=0;
        String roadStatus = null;
        mStepRoadConditions.clear();
        int tmcSize = 0;
        int stepSize = driveSteps.size();
        for(int i = 0; i < stepSize; i++){
            DriveStep driveStep = driveSteps.get(i);
            boolean setRoadStaus = false;
            if(driveStep.getTMCs() != null && driveStep.getTMCs().size()>0){
                List<TMC> tmc = driveStep.getTMCs();
                tmcSize = tmc.size();
                for (int j = 0; j <tmcSize ; j++) {
                    String status = tmc.get(j).getStatus();
                    switch (status) {
                        case AmapPathUtils.ROAD_STATUS_JAM:
                            jam++;
                            break;
                        case AmapPathUtils.ROAD_STATUS_SLOWLY:
                            slow++;
                            break;
                        case AmapPathUtils.ROAD_STATUS_UNKNOWN:
                            unknown++;
                            break;
                        case AmapPathUtils.ROAD_STATUS_GO:
                            go++;
                            break;
                        default:
                            break;

                    }
                }
            }
            if (jam > slow && jam > go) {
                setRoadStaus = true;
                roadStatus = AmapPathUtils.ROAD_STATUS_JAM;
            } else if (jam <= slow && slow > go) {
                setRoadStaus = true;
                roadStatus = AmapPathUtils.ROAD_STATUS_SLOWLY;
            }else {//其它情况当畅通处理
                if(IS_PATH_STEP_DRAW){
                    setRoadStaus = true;
                    roadStatus = AmapPathUtils.ROAD_STATUS_GO;
                }
            }
            if (setRoadStaus) {
                RoadCondition roadCondition = new RoadCondition(i, roadStatus);
                mStepRoadConditions.add(roadCondition);
                HaloLogger.logI(TAG, String.format("获取到交通状态，step = %s,roadStatus = %s\n",i,roadStatus));
            }

        }*/

    }
    /**
     * pathStepList 不自己new对象
     **/
    public void parseRouteCondition(List<HudPathStep> pathStepList, String jsonStr) {
        /*if (pathStepList == null || pathStepList.size()<=0) {
            return;
        }*/
        JSONObject joAll = JsonTool.parseToJSONObject(jsonStr);
        JSONObject joRoute = JsonTool.getJSONObject(joAll, "route");

        JSONArray jaPaths = JsonTool.getJsonArray(joRoute, "paths");
        JSONObject joPath = JsonTool.getJSONObject(jaPaths, 0);

        JSONArray jaSteps = JsonTool.getJsonArray(joPath, "steps");
        final int cnt = jaSteps.length();

        int jam, slow, unknown, go;
        String roadStatus = null;
        mStepRoadConditions.clear();
        for (int i = 0; i < cnt; i++) {
            JSONObject joStep = JsonTool.getJSONObject(jaSteps, i);
            JSONArray jaTmcs = JsonTool.getJsonArray(joStep, "tmcs");
            boolean setRoadStaus = false;
            jam = slow = unknown = go = 0;
            for (int j = 0; j < jaTmcs.length(); j++) {
                JSONObject joTmc = JsonTool.getJSONObject(jaTmcs, j);
//                String distance  = JsonTool.getJsonValue(joTmc,"distance");
                String status = JsonTool.getJsonValue(joTmc, "status");
                switch (status) {
                    case AmapPathUtils.ROAD_STATUS_JAM:
                        jam++;
                        break;
                    case AmapPathUtils.ROAD_STATUS_SLOWLY:
                        slow++;
                        break;
                    case AmapPathUtils.ROAD_STATUS_UNKNOWN:
                        unknown++;
                        break;
                    case AmapPathUtils.ROAD_STATUS_GO:
                        go++;
                        break;
                    default:
                        break;

                }
                /*HudPathStep.RoadStatus roadStatus = new HudPathStep.RoadStatus();
                roadStatus.setDistance(distance);
                roadStatus.setStatus(status);
                roadStatuseList.add(roadStatus);*/
            }
            if (jam > slow && jam > go) {
                setRoadStaus = true;
                roadStatus = AmapPathUtils.ROAD_STATUS_JAM;
            } else if (jam <= slow && slow > go) {
                setRoadStaus = true;
                roadStatus = AmapPathUtils.ROAD_STATUS_SLOWLY;

            }
            if (setRoadStaus) {
                RoadCondition roadCondition = new RoadCondition(i, roadStatus);
                mStepRoadConditions.add(roadCondition);
                HaloLogger.logI(TAG, "获取到交通状态，step = " + i);
            }

            /*if(pathStepList.size()>i){
                HudPathStep pathStep = pathStepList.get(i);
                pathStep.setRoadStatuses(roadStatuseList);
                pathStepList.add(pathStep);
            }*/

        }
        HaloLogger.logI(TAG, "okhttp 收到数据路况数据");

//        String strategy = JsonTool.getJsonValue(joPath,"strategy");
//		HaloLogger.logI(TAG,"okhttp 收到数据"+",策略："+strategy+"DriveStep为："+cnt+"，路况数据："+pathStepList);

    }

    private Bitmap drawRoadCondition(final Canvas canvas, final Bitmap bitmap) {
        if (mStepRoadConditions == null || mStepRoadConditions.size() <= 0) {
            return bitmap;
        }
        CornerPathEffect cornerPathEffect = new CornerPathEffect(80);
        Paint pathPaint = new Paint();
        pathPaint.setStrokeWidth(6);
        pathPaint.setColor(Color.BLUE);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setPathEffect(cornerPathEffect);
        pathPaint.setAntiAlias(true);
        pathPaint.setAlpha(150);
        Path path = new Path();

        Integer roadColor = null;
        List<Point> stepPoints = new ArrayList<>();
        for (int step = 0; step < mStepRoadConditions.size(); step++) {
            RoadCondition roadCondition = mStepRoadConditions.get(step);
            int stepIndex = Math.min(mCurrentDrivePath.getSteps().size() - 1, roadCondition.getPathStep());
            stepPoints.clear();
            DriveStep driveStep = mCurrentDrivePath.getSteps().get(stepIndex);
            for (LatLonPoint polyline : driveStep.getPolyline()) {
                LatLngOutSide latLng = new LatLngOutSide(polyline.getLatitude(),polyline.getLongitude());
                if (latLng != null) {
                    stepPoints.add(mAMapProjection.toScreenLocation(latLng,SCREEN_SCALE_LEVEL));
                }
            }
            List<Point> remapPoints = RectUtils.rectRemap(stepPoints, mRectMapPara);
            roadColor = roadConditionColor(roadCondition.getRoadStatus());
            if (roadColor != null) {
                path.reset();
                PointUtils.points2MovePath(path, remapPoints, 0, 0);
                pathPaint.setColor(roadColor);
                canvas.drawPath(path, pathPaint);
            }
            if (CONDITION_DEBUG_MODE) {
                HaloLogger.logI(TAG, "路况PATH的点为：" + remapPoints + ",step = " + step);
            }

        }

        drawBasicMark(canvas, pathPaint);
        return bitmap;

    }

    private Bitmap drawRoadCondition(final Canvas canvas, final List<HudPathStep> pathSteps, final Bitmap bitmap) {
        if (pathSteps == null || pathSteps.size() <= 0) {
            return bitmap;
        }
        CornerPathEffect cornerPathEffect = new CornerPathEffect(80);
        Paint pathPaint = new Paint();
        pathPaint.setStrokeWidth(6);
        pathPaint.setColor(Color.BLUE);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setPathEffect(cornerPathEffect);
        pathPaint.setAntiAlias(true);
        pathPaint.setAlpha(150);
        Path path = new Path();
        List<Point> stepPoints = new LinkedList<>();
        for (int step = 0; step < pathSteps.size(); step++) {
            HudPathStep hudPathStep = pathSteps.get(step);
            if (hudPathStep.getPoints() != null && hudPathStep.getRoadStatuses() != null) {
                Integer roadColor = getStatusColor(hudPathStep.getRoadStatuses());
                if (roadColor != null) {
//                    stepPoints.clear();
//                    for (Point point :hudPathStep.getPoints()){
//                        stepPoints.add(new Point(point));
//                    }
                    if (mStepsFilterPoints != null && step < mStepsFilterPoints.size()) {
                        stepPoints = mStepsFilterPoints.get(step);//从画路径中过得过滤点
                        if (stepPoints != null) {
                            PointUtils.points2MovePath(path, stepPoints, 0, 0);
                            pathPaint.setColor(roadColor);
                            canvas.drawPath(path, pathPaint);
                        }
                    }
                }
            }
        }

        drawBasicMark(canvas, pathPaint);
        return bitmap;

    }


    public void updateRoadCondition() {
//        Bitmap bitmap = drawRoadCondition(mCanvas, mRoutePathBitmap);


//        Bitmap bitmap = drawRoadCondition(mCanvas,mHudPathSteps,mRoutePathBitmap);
        //mStrategyRouteView.setImageBitmap(bitmap);
    }

    /*------------------将bitmap返回给fragment  start-------------------*/
    public interface OnCreateStrategyBitmapListener {
        void onCreateStrategyBitmap(Bitmap bitmap);
    }

    private OnCreateStrategyBitmapListener mOnCreateStrategyBitmaListener;

    public void setOnCreateStrategyBitmaListener(OnCreateStrategyBitmapListener listener) {
        this.mOnCreateStrategyBitmaListener = listener;
    }
    /*------------------将bitmap返回给fragment  end-------------------*/


    private class RoadPath {
        private String road;
        private int distance;
        private Point point;
        private LatLonPoint latLonPoint;

    }


    private void drawPathText(final Canvas canvas, List<PathText> pathTexts, final Bitmap bitmap) {
//		Canvas canvas = new Canvas(bitmap);
        Paint textPaint = mTextPaint;
        mTextPaint.setTextSize(DisplayUtil.dip2px(mContext, 14f));
        List<Path> drawPathList = new LinkedList<>();
        Point centerPoint = new Point(bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        int textHeight = (int) (Math.ceil(fm.descent - fm.ascent));
        Path intersectPath = new Path();

        Paint markPaint = new Paint();
        markPaint.setColor(Color.RED);
        markPaint.setStrokeWidth(3);
        markPaint.setStyle(Paint.Style.FILL);

        Paint testPaint = null;
        Path testPath = null;
        if (PATH_DEBUG_MODE) {
            testPath = new Path();
            testPaint = new Paint();
            testPaint.setStrokeWidth(1);
            testPaint.setColor(Color.RED);
            testPaint.setStyle(Paint.Style.STROKE);
        }

        if (PATH_DEBUG_MODE) {
//			mPathRectManager.drawUndrawRegion(canvas,textPaint);
        }
        List<PathRectManager.RectRequest> rectRequests = new ArrayList<>();
        //最长的路优先显示，优先取路的中间坐标点，多条路名左右交叉
        final int Cnt = pathTexts.size();
        for (int i = 0; i < Cnt; i++) {
            PathText pathText = pathTexts.get(i);
            String content = pathText.getText();
            if (content != null) {
                if (pathText.isCuttable() && content.length() > MAX_DISPLAY_TEXT_LENGTH) {
                    content = content.substring(0, 5) + "...";
                }
                Point point = pathText.getPosition();
                PathRectManager.RectRequest rectRequest = new PathRectManager.RectRequest();
                rectRequest.setType(PathRectManager.RectType.TextRect);
                rectRequest.setPoint(point);
                rectRequest.setMinRect(PathRectManager.getTextRect(content, textPaint));
                rectRequests.add(rectRequest);
                if (PATH_DEBUG_MODE) {
                    canvas.drawCircle(point.x, point.y, 2, markPaint);
                }
            }

        }
        mPathRectManager.setAwayPath(true);
        mTextRectResponses = mPathRectManager.findRect(rectRequests);
        mPathTexts = pathTexts;
//        mHandler.sendEmptyMessage(MESSAGE_SRATEGY_ROAD_TEXT);//发送消息更新文本
        drawPathTextResult();


    }

    /**
     * 根据计算的位置结果绘制文本
     */
    private void drawPathTextResult() {
        Canvas canvas = mCanvas;
        List<PathText> pathTexts = mPathTexts;
        Paint textPaint = mTextPaint;
        List<PathRectManager.RectResponse> textRectResponses = mTextRectResponses;
        Path textPath = new Path();
        for (int i = 0; i < textRectResponses.size(); i++) {
            PathRectManager.RectResponse rectResponse = textRectResponses.get(i);
            Rect textRect = rectResponse.getRect();
            PathText pathText = pathTexts.get(rectResponse.getIndex());
            String content = pathText.getText();
            if (pathText.isCuttable() && content.length() > MAX_DISPLAY_TEXT_LENGTH) {
                content = content.substring(0, 5) + "...";
            }
            if (textRect != null) {
                textPath.reset();
                textPath.moveTo(textRect.left, textRect.bottom);
                textPath.lineTo(textRect.right, textRect.bottom);
                canvas.drawTextOnPath(content, textPath, 0, 0, textPaint);
                /*if (PATH_DEBUG_MODE){
                    testPath.reset();
                    testPath.addRect(new RectF(textRect), Path.Direction.CCW);
                    canvas.drawPath(testPath, testPaint);
                }*/
            }
        }
    }


    /**
     * 取状态最多的来表示颜色
     **/
    private Integer getStatusColor(List<HudPathStep.RoadStatus> roadStatusList) {
        if (roadStatusList == null || roadStatusList.size() < 0) {
            return null;
        }
        Integer result = null;
        int slow = 0, unknown = 0, go = 0, jam = 0;
        for (HudPathStep.RoadStatus roadStatus : roadStatusList) {
            switch (roadStatus.getStatus()) {
                case AmapPathUtils.ROAD_STATUS_JAM:
                    jam++;
                    break;
                case AmapPathUtils.ROAD_STATUS_SLOWLY:
                    slow++;
                    break;
                case AmapPathUtils.ROAD_STATUS_UNKNOWN:
                    unknown++;
                    break;
                case AmapPathUtils.ROAD_STATUS_GO:
                    go++;
                    break;
                default:
                    break;

            }
        }
        if (jam > slow && jam > go) {
            result = roadConditionColor(AmapPathUtils.ROAD_STATUS_JAM);
        } else if (jam <= slow && slow > go) {
            result = roadConditionColor(AmapPathUtils.ROAD_STATUS_SLOWLY);
        }
        return result;
    }


    public Integer roadConditionColor(String status) {
        Integer result = null;
        switch (status) {
            case AmapPathUtils.ROAD_STATUS_JAM:
                result = Color.rgb(0xff, 0x00, 0x00);
                break;
            case AmapPathUtils.ROAD_STATUS_SLOWLY:
                result = Color.rgb(0xff, 0xb4, 0x00);
                break;
            default:
                if(IS_PATH_STEP_DRAW){
                    result = Color.rgb(0x00,0x7a,0xff);
                }
                break;
        }
        return result;
    }


    public Bitmap drawPathStep(final Canvas canvas, final List<HudPathStep> pathSteps, final Bitmap bitmap) {

        /*if(pathSteps == null || pathSteps.size()<=0){
            return bitmap;
        }*/

        Resources resources = mContext.getResources();
        int colorStart = resources.getColor(R.color.strategy_path_start);
        int colorEnd = resources.getColor(R.color.strategy_path_end);
        int colorshadow = resources.getColor(R.color.color_strategy_path_shadow);


        CornerPathEffect cornerPathEffect = new CornerPathEffect(80);
        Paint pathPaint = new Paint();
        pathPaint.setStrokeWidth(6);
//		pathPaint.setColor(Color.BLUE);
        pathPaint.setColor(Color.rgb(0x00, 0x7a, 0xff));
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setPathEffect(cornerPathEffect);
        pathPaint.setAntiAlias(true);
        LinearGradient linearGradient = new LinearGradient(mStartPoint.x, mStartPoint.y, mEndPoint.x, mEndPoint.y, new int[]{colorStart, colorEnd}, new float[]{0f, 0.4f}, Shader.TileMode.CLAMP);
        pathPaint.setShader(linearGradient);
        Paint textPaint = new TextPaint();
        textPaint.setTextSize(20);
        textPaint.setColor(Color.WHITE);

        Path path = new Path();
        if(IS_PATH_STEP_DRAW){
            for (int step = 0; step < mFilterPathStepPoints.size(); step++) {
                int validStep = mValidStep.get(step);
                List<Point> stepPoints = mFilterPathStepPoints.get(step);//从画路径中过得过滤点
                RoadCondition roadCondition = mStepRoadConditions.get(validStep);//取得真实的step
                if (stepPoints != null) {
                    Integer roadColor = roadConditionColor(roadCondition.getRoadStatus());
                    if (roadColor != null) {
                        path.reset();
                        PointUtils.points2MovePath(path, stepPoints, 0, 0);
                        pathPaint.setColor(roadColor);
                        canvas.drawPath(path, pathPaint);
                    }
                }
            }
        }else {
            //原绘制方法
            path.reset();
            PointUtils.points2MovePath(path, mFilterPathPoints, 0, 0);
            canvas.drawPath(path, pathPaint);
        }

        pathStartPoint = mStartPoint;
        startMarkAngle = (float) PointUtils.degree(mStartPoint, mEndPoint);
        startMarkPoint = new Point((int) (mStartPoint.x - HudEndPointConstants.MAPPATH_START_ICON_WIDTH), (int) (mStartPoint.y - HudEndPointConstants.MAPPATH_START_ICON_HEIGHT));
        endMarkPoint = new Point((int) (mEndPoint.x - HudEndPointConstants.MAPPATH_END_ICON_WIDTH), (int) (mEndPoint.y - HudEndPointConstants.MAPPATH_END_ICON_HEIGHT));

//        HaloLogger.logI(TAG,"起止点为："+mStartPoint+mEndPoint+"发出的方向为: "+startMarkAngle);
        drawBasicMark(canvas, textPaint);

        Path filterPointsPath = PointUtils.points2ClosePath(mFilterPathPoints, 1, 1);

        mPathRectManager.clearUndrawRegion();
        mPathRectManager.setRegion(bitmap.getWidth(), bitmap.getHeight());
        mPathRectManager.addUndrawRegion(filterPointsPath);
        mPathRectManager.addUndrawRegion(startMarkPoint, startMark.getWidth(), startMark.getHeight());
        mPathRectManager.addUndrawRegion(endMarkPoint, endMark.getWidth(), endMark.getHeight());

        return bitmap;

    }


    /**
     * 画基本的Mark点
     **/

    private void drawBasicMark(Canvas canvas, Paint paint) {
        canvas.rotate(startMarkAngle, pathStartPoint.x, pathStartPoint.y);
        canvas.drawBitmap(startMark, startMarkPoint.x, startMarkPoint.y, paint);
        canvas.rotate(-startMarkAngle, pathStartPoint.x, pathStartPoint.y);
        canvas.drawBitmap(endMark, endMarkPoint.x, endMarkPoint.y, paint);
        canvas.drawBitmap(endMark, endMarkPoint.x, endMarkPoint.y, paint);
    }

    private Bitmap drawPath(final List<Point> points, final Bitmap bitmap) {

        mPathPaint.setColor(Color.BLUE);
        mPathPaint.setStrokeWidth(10);
        mPathPaint.setStyle(Paint.Style.STROKE);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        canvas.drawColor(Color.BLACK);

        Point startPoint = points.get(0);
        Point endPoint = points.get(points.size() - 1);

        Paint textPaint = new TextPaint();
        textPaint.setTextSize(20);
        textPaint.setColor(Color.WHITE);

        int colorCnt = 0;
        String[] LOADS = new String[]{"深南大道", "南海大道", "学府路", "滨海大道", "无名路", "107车道"};
        int[] COLORS = new int[]{Color.BLUE, Color.RED, Color.BLUE, Color.BLUE, Color.RED, Color.BLUE};

        int step = points.size() / 1;//points.size()/20
        int index = 0;
        for (int j = 0; j < points.size() / step; j = j + 1) {
            Path path = new Path();
            Point moveTo = points.get(index);
            path.moveTo(moveTo.x, moveTo.y);
            for (int i = 0; i < step; i = i + 1) {

                if (++index >= points.size()) {
                    break;
                }
                Point toPoint = points.get(index);
                path.lineTo(toPoint.x, toPoint.y);
            }
            canvas.drawPath(path, mPathPaint);
            mPathPaint.setColor(Color.BLUE);
        }

//        paint.setColor(Color.GREEN);
//        paint.setStrokeWidth(3);
//        paint.setStyle(Paint.Style.FILL);
//        canvas.drawCircle(startPoint.x,startPoint.y,10,paint);
//        canvas.drawCircle(endPoint.x,endPoint.y,10,paint);
        //外圆
//        paint.setStyle(Paint.Style.STROKE);
//        canvas.drawCircle(startPoint.x,startPoint.y,10,paint);
//        canvas.drawCircle(endPoint.x,endPoint.y,10,paint);

        Point startMarkPoint = new Point(startPoint.x - 10, startPoint.y - 10);
        Point endMarkPoint = new Point(endPoint.x - 12, endPoint.y - 40);
        Bitmap startMark = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.route_strategy_map_start);
        canvas.drawBitmap(startMark, startMarkPoint.x, startMarkPoint.y, paint);
        Bitmap endMark = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.route_strategy_map_end);
        canvas.drawBitmap(endMark, endMarkPoint.x, endMarkPoint.y, paint);

        mPathRectManager.clearUndrawRegion();
        mPathRectManager.addUndrawRegion(bitmap.getWidth(), bitmap.getHeight());
        mPathRectManager.addUndrawRegion(startMarkPoint, startMark.getWidth(), startMark.getHeight());
        mPathRectManager.addUndrawRegion(endMarkPoint, endMark.getWidth(), endMark.getHeight());


        return bitmap;

    }


    /*private void  displayRoutePath(AMapNaviPath naviPath){
//		HaloLogger.logI(TAG,"导航路径长度为："+naviPath.getAllLength());
        List<Point> textPoints = new ArrayList<>();
        List<String> loadNames = new ArrayList<>();
        List<NaviLatLng> roadLatLngList = new ArrayList<>();
        List<RoadPath> roadPathList = new ArrayList<>();

        mPathLatLngs.clear();
        mPathLatLngs =  naviPath.getCoordList();
        String lastRoad = "";
        int roadDistance = 0,linkIndex = 0;
        //TODO 忽略了最后一个Link
        for (int step = 0; step < naviPath.getSteps().size(); step++){
            AMapNaviStep naviStep = naviPath.getSteps().get(step);
            for (int link = 0; link <naviStep.getLinks().size() ; link++){
                AMapNaviLink naviLink = naviStep.getLinks().get(link);
                String roadName = naviLink.getRoadName();
                //路名相同，叠加路径长度，
                if (lastRoad.equals(roadName)){
                    roadLatLngList.addAll(naviLink.getCoords());
                    roadDistance += naviLink.getLength();
//					continue;
                }else if((linkIndex>0) && (roadName!=null)) {//防止第一次进入
                    //路名不相同，记录上一条路的信息
                    RoadPath roadPath = new RoadPath();
                    roadPath.distance = roadDistance;
                    roadPath.road = roadName;
                    NaviLatLng naviLatLng = roadLatLngList.size()<=0 ? null :roadLatLngList.get(roadLatLngList.size() / 2);
                    if (naviLatLng != null) {
                        roadPath.point = mAMapProjection.toScreenLocation(AmapPathUtils.naviLatLng2LatLng(naviLatLng));
                        roadPathList.add(roadPath);
                    }
                    roadLatLngList.clear();
                    roadDistance = 0;
                    lastRoad = roadName;//保证不为null
                    HaloLogger.logI(TAG,"路名为："+roadName);
                }
//				HaloLogger.logI(TAG,"路名为 +step+link："+step+"   "+link+"   "+roadName);
                linkIndex++;
            }
        }
        mPathPoints = naviLatLngs2Points(mPathLatLngs);

        Collections.sort(roadPathList,new Comparator(){
            @Override
            public int compare(Object o, Object t1) {
                RoadPath guideO = (RoadPath)o;
                RoadPath guideT1 = (RoadPath)t1;

                return new Integer(guideT1.distance).compareTo(guideO.distance);
            }
        });

        textPoints.clear();
        loadNames.clear();
        int maxRoad = Math.min(MAX_DISPLAY_ROAD,roadPathList.size());
        for (int roadIndex = 0; roadIndex <maxRoad ; roadIndex++) {
            RoadPath roadPath = roadPathList.get(roadIndex);
            textPoints.add(roadPath.point);
            loadNames.add(roadPath.road);
        }

        HaloLogger.logI(TAG,"经过的路为："+loadNames);
        int width = DisplayUtil.dip2px(mContext,MAX_DP_WIDTH_STRATGE_PATH);
        int height = DisplayUtil.dip2px(mContext, MAX_DP_HEIGHT_STRATGE_PATH);

        HaloLogger.logI(TAG,"图片背景的长宽为“："+width+", "+height);
        Rect marginRect = new Rect(10,10,50,10);
        mRectMapPara = RectUtils.measureRect(width,height,mPathPoints,marginRect);
        List<Point> newPointList = RectUtils.rectRemap(mPathPoints,mRectMapPara);
        Bitmap srcBitmap = mRoutePathBitmap;
        Bitmap bitmap = drawPath(newPointList,srcBitmap);
        drawPathText(mCanvas,RectUtils.rectRemap(textPoints,mRectMapPara),loadNames,bitmap);
        mStrategyRouteView.setImageBitmap(bitmap);

        List<Object> newObjects = new ArrayList<>();
        int moveScale;

        moveScale= mPathPoints.size()/5;
        newObjects.clear();
        for (int i = 0; i <mPathPoints.size() ; i=i+moveScale) {
            newObjects.add(mPathPoints.get(i));
        }
        HaloLogger.logI(TAG,"屏幕坐标点为："+newObjects);

        moveScale= newPointList.size()/100;
        newObjects.clear();
        for (int i = 0; i <newPointList.size() ; i=i+moveScale) {
            newObjects.add(newPointList.get(i));
        }
        HaloLogger.logI(TAG,"转换后数据为："+newObjects);
    }*/

    private List<Point> naviLatLngs2Points(List<NaviLatLng> naviLatLngList) {
        if (naviLatLngList == null || naviLatLngList.size() <= 0) {
            return null;
        }
        List<Point> pointList = new ArrayList<Point>();
        for (NaviLatLng naviLatLng : naviLatLngList) {
            LatLngOutSide latLng = new LatLngOutSide(naviLatLng.getLatitude(),naviLatLng.getLongitude());
            pointList.add(mAMapProjection.toScreenLocation(latLng,SCREEN_SCALE_LEVEL));
        }
        return pointList;
    }

	/*private Bitmap drawRoadCondition(final Canvas canvas,final List<HudPathStep> pathSteps,final Bitmap bitmap){
        if(pathSteps == null || pathSteps.size()<=0){
			return bitmap;
		}
		CornerPathEffect cornerPathEffect = new CornerPathEffect(80);
		Paint pathPaint  = new Paint();
		pathPaint.setStrokeWidth(6);
		pathPaint.setColor(Color.BLUE);
		pathPaint.setStyle(Paint.Style.STROKE);
		pathPaint.setPathEffect(cornerPathEffect);
		pathPaint.setAntiAlias(true);

		Path path  = new Path();
		for (int step = 0; step <pathSteps.size() ; step++){
			HudPathStep hudPathStep = pathSteps.get(step);
			if (hudPathStep.getPoints() != null && hudPathStep.getRoadStatuses() != null) {
				int roadStatusCnt = hudPathStep.getRoadStatuses().size();
				int totalDistance = 0;
				int totalStepPoint = hudPathStep.getPoints().size();
				//计算step的总距离
				for (HudPathStep.RoadStatus roadStatus: hudPathStep.getRoadStatuses()){
					totalDistance += roadStatus.getDistance();
				}
				List<List<Point>> stepsPoints = new LinkedList<>();
				Integer[] stepColors = new Integer[roadStatusCnt];
				int stepCnts[] = new int[roadStatusCnt];
				//记录步长
				int sumDistance = 0;
				for (int i = 0; i <roadStatusCnt ; i++) {
					HudPathStep.RoadStatus roadStatus = hudPathStep.getRoadStatuses().get(i);
					sumDistance += roadStatus.getDistance();
					stepCnts[i] = (sumDistance*totalStepPoint)/totalDistance;
				}
				HaloLogger.logI(TAG,"路况状态个数："+roadStatusCnt+"，位置集合个数："+totalStepPoint+"，所有步长为"+stepCnts);
				int statusIndex = 0;
				List<Point> stepPoints = new LinkedList<>();
				for (int j = 0; j < totalStepPoint; j++) {
					Point point = hudPathStep.getPoints().get(j);
					if(j<=stepCnts[statusIndex]){
						stepPoints.add(new Point(point));
					}else {
						if((j+1)<totalStepPoint){//保证线连接的持续
							Point nextPoint = hudPathStep.getPoints().get(j+1);
							stepPoints.add(new Point(nextPoint));
						}
						stepsPoints.add(stepPoints);
						stepPoints = new LinkedList<>();
						stepColors[statusIndex] = roadConditionColor(hudPathStep.getRoadStatuses().get(statusIndex).getStatus());
						if(++statusIndex>roadStatusCnt){
							statusIndex=0;
							break;
						}
					}
				}
				//连接相邻的step
//				if((step+1)<pathSteps.size()){
//					Point point = pathSteps.get(step+1).getPoints().get(0);
//					stepPoints.add(point);
//				}
				//记录最后一个状态
				stepsPoints.add(stepPoints);
				stepColors[statusIndex] = roadConditionColor(hudPathStep.getRoadStatuses().get(statusIndex).getStatus());
				//画路径
				for (int k = 0; k < stepsPoints.size(); k++) {
					if (stepColors[k] != null) {
						path.reset();
						List<Point> pointList = stepsPoints.get(k);
						PointUtils.points2MovePath(path,pointList,0,0);
						pathPaint.setColor(stepColors[k]);
						canvas.drawPath(path,pathPaint);
					}

				}

				*//*stepPoints.clear();
                for (Point point :hudPathStep.getPoints()){
					stepPoints.add(new Point(point));
				}
				List<Point> stepFilterPoints = PointUtils.filterPoint(stepPoints,6);
				path.reset();
				PointUtils.points2MovePath(path,stepFilterPoints,0,0);
				Point point = stepPoints.get(stepPoints.size()-1);
				path.lineTo(point.x,point.y);
				if(hudPathStep.getRoadStatuses() != null){
					List<HudPathStep.RoadStatus> roadStatuses = hudPathStep.getRoadStatuses();
					switch (roadStatuses.get(0).getStatus()){
						case "拥堵":
							pathPaint.setColor(Color.rgb(0xff,0x00,0x00));
							break;
						case "缓行":
							pathPaint.setColor(Color.rgb(0xff,0xb4,0x00));
							break;
						default:
							pathPaint.setColor(Color.rgb(0x00,0x7a,0xff));
							break;

					}
				}else {
					pathPaint.setColor(Color.rgb(0x00,0x7a,0xff));//0x007aff
				}
				canvas.drawPath(path,pathPaint);*//*
			}
		}
		return bitmap;

	}*/

    private void drawDestination(String dest, List<Point> mRemapPathPoints, Canvas canvas) {
        int bitmapWidth = mRoutePathBitmap.getWidth();
        int bitmapHeight = mRoutePathBitmap.getHeight();
    //    String distance = new DecimalFormat("##0.0").format((float) (Math.round(mCurrentDrivePath.getDistance() / 10)) / 100) + "km";
        String distance = "";
        String destination = "";//TODO 20161117 去掉策略图中距离地点的显示
        /*
        *   1、根据这些文字获取矩形。
        *   2、判断这些矩形是否可以放在某个角落。
        */

        mTextPaint.setTextSize(DisplayUtil.dip2px(mContext, 12f));
        Rect destinationTextRect = PathRectManager.getTextRect("", mTextPaint);
        Rect distanceTextRect = PathRectManager.getTextRect("", mTextPaint);

        int destWidth = destinationTextRect.width();
        int destHeight = destinationTextRect.height();
        int distWidth = distanceTextRect.width();
        int distHeight = distanceTextRect.height();
        int textRectWidth = destWidth > distWidth ? destWidth : distWidth;
        int textRectHeight = destHeight + distHeight;
        if(mRemapPathPoints.get(0).x < bitmapWidth / 2 || mRemapPathPoints.get(0).y < bitmapHeight / 2){
            // 右下角。

            Rect rect = getDrawableRect(bitmapWidth - textRectWidth, bitmapHeight - textRectHeight, bitmapWidth, bitmapHeight, DestinationDirection.RIGHTBOTTOM);
            if ( rect!= null) {
                int missdistance = destWidth > distWidth ? destWidth - distWidth : 0;
                canvas.drawText(distance,rect.left + missdistance - 5, rect.top + destHeight - 10, mTextPaint);
                int missDistance = destWidth > distWidth ? 0 : distWidth - destWidth;
                canvas.drawText(destination, rect.left + missDistance - 5, rect.top + destHeight + distHeight, mTextPaint);
                mPathRectManager.addUndrawRegion(new Point(rect.left, rect.top), rect.right - rect.left, rect.bottom - rect.top);
                return;
            }
        }
        // 右上角
        Rect rect = getDrawableRect(bitmapWidth - textRectWidth, 0, bitmapWidth, textRectHeight, DestinationDirection.RIGHTUP);
        if (rect != null) {
            int missdistance = destWidth > distWidth ? 0 : distWidth - destWidth;
            canvas.drawText(destination, rect.left + missdistance - 5, rect.top + destHeight, mTextPaint);
            int missDistance = destWidth > distWidth ? destWidth - distWidth : 0;
            canvas.drawText(distance, rect.left + missDistance - 5, rect.top + destHeight + destHeight, mTextPaint);
            mPathRectManager.addUndrawRegion(new Point(rect.left, rect.top), rect.right - rect.left, rect.bottom - rect.top);
            return;
        }

        // 左上角。
        Rect rect1 = getDrawableRect(0, 0, textRectWidth, textRectHeight, DestinationDirection.LEFTUP);
        if (rect1 != null) {
            canvas.drawText(destination, rect1.left, rect1.top + destHeight, mTextPaint);
            canvas.drawText(distance, rect1.left, rect1.top + destHeight + destHeight, mTextPaint);
            mPathRectManager.addUndrawRegion(new Point(rect1.left, rect1.top), rect1.right - rect1.left, rect1.bottom - rect1.top);
            return;
        }

        // 左下角。
        Rect rect2 = getDrawableRect(0, bitmapHeight - textRectHeight, textRectWidth, bitmapHeight, DestinationDirection.LEFTBOTTOM);
        if (rect2 != null) {
            canvas.drawText(distance, rect2.left, rect2.top + destHeight, mTextPaint);
            canvas.drawText(destination, rect2.left, rect2.top + destHeight + distHeight, mTextPaint);
            mPathRectManager.addUndrawRegion(new Point(rect2.left, rect2.top), rect2.right - rect2.left, rect2.bottom - rect2.top);
            return;
        }
    }

    public Rect getDrawableRect(int left, int top, int right, int bottom, DestinationDirection direction){
        ArrayList<PathRectManager.RectRequest> outrectRequests = new ArrayList<>();
        PathRectManager.RectRequest rectRequest = new PathRectManager.RectRequest();
        Rect rect = new Rect(left, top, right, bottom);
        rectRequest.setType(PathRectManager.RectType.TextRect);
        int pointX = 0;
        int pointY = 0;
        switch (direction){
            case RIGHTBOTTOM:
                pointX = right;
                pointY = bottom;
                break;
            case RIGHTUP:
                pointX = right;
                pointY = 0;
                break;
            case LEFTUP:
                pointX = 0;
                pointY = 0;
                break;
            case LEFTBOTTOM:
                pointX = 0;
                pointY = bottom;
                break;
            // TODO: 2016/9/1  
        }
        rectRequest.setPoint(new Point(pointX, pointY));
        rectRequest.setMinRect(rect);
        outrectRequests.clear();
        outrectRequests.add(rectRequest);
        mPathRectManager.addUndrawRegion(mRoutePathBitmap.getWidth(),mRoutePathBitmap.getHeight());
        List<PathRectManager.RectResponse> rectResponseList = mPathRectManager.findRect(outrectRequests,10,10);
        if (rectResponseList.size() > 0) {
            return rectResponseList.get(0).getRect();
        } else {
            return null;
        }
    }

    public enum DestinationDirection{
        RIGHTBOTTOM, RIGHTUP, LEFTUP, LEFTBOTTOM
    }
}