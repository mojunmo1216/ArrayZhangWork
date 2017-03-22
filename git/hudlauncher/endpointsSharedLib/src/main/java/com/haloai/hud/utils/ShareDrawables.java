package com.haloai.hud.utils;


import com.haloai.hud.sharedlibs.R;

/**
 * Created by wangshengxing on 16/6/22.
 */
public class ShareDrawables {
    /**
     * 获取转向标的ID资源
     * @param iconType 高德导航的转向标类型
    * */
    public static int getNaviDirectionId(int iconType){
        int result = 0;
        switch (iconType) {
            case 15:
                result = R.drawable.greenline_direction__icon_turn_dest;
                break;
            case 13:
                result = R.drawable.greenline_direction__icon_turn_service_area;
                break;
            case 14:
                result = R.drawable.greenline_direction__icon_turn_tollgate;
                break;
            case 16:
                result = R.drawable.greenline_direction__icon_turn_tunnel;
                break;
            case 10:
                result = R.drawable.greenline_direction__icon_turn_passing_point;
                break;
            case 24:
                // TODO 通过索道
                break;
            case 26:
                // TODO 通过通道、建筑物穿越通道
                break;
            case 17:
                // TODO 通过人行横道
                break;
            case 28:
                // TODO 通过游船路线
                break;
            case 1:
                // TODO 自车图标
                break;
            case 11:
                result = R.drawable.greenline_direction__icon_turn_ring;
                break;
            case 31:
                // TODO 通过阶梯
                break;
            case 2:
                result = R.drawable.greenline_direction__icon_turn_left;
                break;
            case 6:
                result = R.drawable.greenline_direction__icon_turn_left_back;
                break;
            case 4:
                result = R.drawable.greenline_direction__icon_turn_left_front;
                break;
            case 8:
                result = R.drawable.greenline_direction__icon_turn_back;
                break;
            case 23:
                // TODO 通过直梯
                break;
            case 0:
                // TODO 无定义
                break;
            case 12:
                result = R.drawable.greenline_direction__icon_turn_ring_out;
                break;
            case 18:
                // TODO 通过过街天桥
                break;
            case 21:
                // TODO 通过公园
                break;
            case 3:
                result = R.drawable.greenline_direction__icon_turn_right;
                break;
            case 7:
                result = R.drawable.greenline_direction__icon_turn_right_back;
                break;
            case 5:
                result = R.drawable.greenline_direction__icon_turn_right_front;
                break;
            case 29:
                // TODO 通过观光车路线
                break;
            case 25:
                // TODO 通过空中通道
                break;
            case 30:
                // TODO 通过滑道
                break;
            case 20:
                // TODO 通过广场
                break;
            case 22:
                // TODO 通过扶梯
                break;
            case 9:
                result = R.drawable.greenline_direction__icon_turn_front;
                break;
            case 19:
                // TODO 通过地下通道
                break;
            case 27:
                // TODO 通过行人道路
                break;
        }
        return result;
    }
}
