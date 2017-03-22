package com.haloai.hud.hudendpoint.upgrade.policy;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.StatFs;

import com.fota.iport.MobAgentPolicy;
import com.fota.iport.info.PolicyMapInfo;
import com.fota.utils.Trace;

import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;

/**
 * Created by zhangrui on 16/12/13.
 */


/**
 * 策略信息管理类，主要接管对下发策略的判断与使用
 */
public class PolicyManager implements PolicyInter {

    private static final String KEY_DOWNLOAD_WIFI = "download_wifi";
    private static final String KEY_DOWNLOAD_STORAGE_SIZE = "download_storageSize";
    private static final String KEY_DOWNLOAD_STORAGE_PATH = "download_storagePath";
    private static final String KEY_NOTIFICATION_POP = "notification_pop";
    private static final String KEY_NOTIFICATION_STATUSBAR = "notification_statusbar";
    private static final String KEY_INSTALL_BATTERY = "install_battery";
    private static final String KEY_INSTALL_FORCE = "install_force";
    private static final String KEY_CHECK_CYCLE = "check_cycle";
    private static final String TAG = "PolicyManager";

    public PolicyManager() {
    }

    /**
     * 下载对wifi要求
     *
     * @return true if need wifi.
     */
    public boolean is_request_wifi() {
       // if (!PolicyConfig.getInstance().wifi) return false;
        PolicyMapInfo wifi_info = MobAgentPolicy.getPolicyHashMap().get(KEY_DOWNLOAD_WIFI);
        if (wifi_info != null) {
            if ("required".equals(wifi_info.key_value))
                return true;
            Trace.d(TAG, "is_request_wifi()");
        }
        return false;
    }

    /**
     * 下载对剩余空间的要求
     *
     * @param path 下载文件的父目录 绝对路径
     * @return true if has more space
     */
    public boolean is_storage_space_enough(String path) {
      //  if (!PolicyConfig.getInstance().storage_size) return true;
        PolicyMapInfo size_info = MobAgentPolicy.getPolicyHashMap().get(KEY_DOWNLOAD_STORAGE_SIZE);
        if (size_info != null) {
            try {
                long free_size = getStorageSpace(path);
                Trace.i(TAG, String.format("is_storage_space_enough() need_size = %s,free_size = %s,path = %s",
                        size_info.key_value, free_size, path));
                if (Long.parseLong(size_info.key_value) <= free_size) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
        }
        return true;
    }

    /**
     * 升级包存放路径
     *
     * @return 所配置的路径，未做任何逻辑处理。若没有配，则返回null
     */
    public String get_storage_path() {
       // if (!PolicyConfig.getInstance().storage_path) return null;
        PolicyMapInfo path_info = MobAgentPolicy.getPolicyHashMap().get(KEY_DOWNLOAD_STORAGE_PATH);
        if (path_info != null) {
            return path_info.key_value;
        }
        return null;
    }

    public boolean is_notify_pop() {
       // if (!PolicyConfig.getInstance().notification) return false;
        PolicyMapInfo statusbar_info = MobAgentPolicy.getPolicyHashMap().get(KEY_NOTIFICATION_POP);
        if (statusbar_info != null) {
            return true;
        }
        return false;
    }

    public boolean is_notification_always() {
       // if (!PolicyConfig.getInstance().notification) return false;
        PolicyMapInfo statusbar_info = MobAgentPolicy.getPolicyHashMap().get(KEY_NOTIFICATION_STATUSBAR);
        if (statusbar_info != null) {
            if ("always".equals(statusbar_info.key_value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 升级电量要求,应该在返回true的时候，去升级
     *
     * @return false 配置了电量要求字段，并且当前手机电量小于配置电量，否则 true
     */
    public boolean is_battery_enough(Context ctx) {
      //  if (!PolicyConfig.getInstance().battery) return true;
        PolicyMapInfo battery_info = MobAgentPolicy.getPolicyHashMap().get(KEY_INSTALL_BATTERY);
        if (battery_info != null) {
            IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryIntent = ctx.registerReceiver(null, filter);
            int batteryLevel = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            Trace.d(TAG, "batteryLevel mobile = " + batteryLevel + "  config = " + battery_info.key_value);
            try {
                if (batteryLevel >= Integer.parseInt(battery_info.key_value)) {
                    return true;
                } else {
                    return false;
                }
            } catch (NumberFormatException e) {
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return true;
        }
    }

    /**
     * 下载完成后，是否强制升级
     *
     * @return
     */
    public boolean is_auto_upgrade() {
       // if (!PolicyConfig.getInstance().install_force) return false;
        PolicyMapInfo force_info = MobAgentPolicy.getPolicyHashMap().get(KEY_INSTALL_FORCE);
        if (force_info != null) {
            int from_time;
            int to_time;
            try {
                JSONObject obj = new JSONObject(force_info.key_value);
                from_time = Integer.parseInt(obj.getString("from"));
                to_time = Integer.parseInt(obj.getString("to"));
                Calendar calendar = Calendar.getInstance();
                int cur_time = calendar.get(Calendar.HOUR_OF_DAY);
                Trace.d(TAG, String.format("cur = %s,from = %s,to = %s", cur_time, from_time, to_time));
                if (cur_time > from_time && cur_time < to_time) {
                    return true;
                }
                return false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 检测版本周期。单位：分钟。最小值60.
     *
     * @return -1：没有配置该项，-2：该项配置错误
     */
    public int get_check_cycle() {
     //   if (!PolicyConfig.getInstance().check_cycle) return -1;
        PolicyMapInfo cycle_info = null;
        try {
            cycle_info = MobAgentPolicy.getPolicyHashMap().get(KEY_CHECK_CYCLE);
        } catch (Exception e) {
            return -1;
        }
        if (cycle_info != null) {
            try {
                int cycle = Integer.parseInt(cycle_info.key_value);
                return cycle > 60 ? cycle : 60;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return -2;
            }
        } else {
            return -1;
        }
    }

    private long getStorageSpace(String path) {
        Trace.d(TAG, "getStorageSpace() [path] " + path);
        File file = new File(path);
        StatFs statfs = new StatFs(file.getPath());
        long blockSize = statfs.getBlockSize();
        long blockCount = statfs.getAvailableBlocks();
        return blockSize * blockCount;
    }

/*    public String displayPolicy() {
        StringBuilder displayStr = new StringBuilder();
        if (MobAgentPolicy.getPolicyHashMap() != null) {
            for (Map.Entry<String, PolicyMapInfo> mapInfo : MobAgentPolicy.getPolicyHashMap().entrySet()) {
                displayStr.append(mapInfo.getKey()).append(":").append(mapInfo.getValue()).append("\n");
            }
        } else {
            displayStr.append("null");
        }
        return displayStr.toString();
    }*/

}
