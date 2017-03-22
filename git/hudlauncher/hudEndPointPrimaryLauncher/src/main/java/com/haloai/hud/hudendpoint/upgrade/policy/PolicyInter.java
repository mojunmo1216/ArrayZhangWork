package com.haloai.hud.hudendpoint.upgrade.policy;

import android.content.Context;

/**
 * Created by zhangrui on 16/12/13.
 */
public interface PolicyInter {

    /**
     * 下载对wifi要求
     *
     * @return true if need wifi.
     */
    boolean is_request_wifi();

    /**
     * 下载对剩余空间的要求
     *
     * @param path 下载文件的父目录 绝对路径
     * @return true if has more space
     */
    boolean is_storage_space_enough(String path);


    /**
     * 升级包存放路径
     *
     * @return 所配置的路径，未做任何逻辑处理。若没有配，则返回null
     */
    String get_storage_path();


    boolean is_notify_pop();

    boolean is_notification_always();

    /**
     * 升级电量要求,应该在返回true的时候，去升级
     *
     * @return false 配置了电量要求字段，并且当前手机电量小于配置电量，否则 true
     */
    boolean is_battery_enough(Context ctx);

    /**
     * 下载完成后，是否强制升级
     *
     * @return
     */
    boolean is_auto_upgrade();

    /**
     * 检测版本周期。单位：分钟。最小值60.
     *
     * @return -1：没有配置该项，-2：该项配置错误
     */
    int get_check_cycle();


}
