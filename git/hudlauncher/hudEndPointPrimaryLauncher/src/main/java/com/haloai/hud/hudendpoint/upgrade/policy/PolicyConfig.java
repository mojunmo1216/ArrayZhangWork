package com.haloai.hud.hudendpoint.upgrade.policy;

/**
 * Created by zhangrui on 16/12/13.
 */
public class PolicyConfig {
    public boolean wifi = true;
    public boolean storage_size = true;
    public boolean storage_path = true;
    public boolean notification = true;
    public boolean battery = true;
    public boolean check_cycle = true;
    public boolean install_force = true;
    public boolean self_update = true;

    private static PolicyConfig m_instance;

    private PolicyConfig() {
    }

    public static PolicyConfig getInstance() {
        if (m_instance == null) {
            synchronized (PolicyInter.class) {
                if (m_instance == null) {
                    m_instance = new PolicyConfig();
                }
            }
        }
        return m_instance;
    }

    /**
     * 配置自升级功能
     *
     * @param value
     * @return
     */
    public PolicyConfig request_self_update(boolean value) {
        self_update = value;
        return this;
    }

    /**
     * 配置是否需要wifi环境，才能下载功能
     *
     * @param value
     * @return
     */
    public PolicyConfig request_wifi(boolean value) {
        wifi = value;
        return this;
    }

    /**
     * 配置本地存储大小必须大于给定值才能下载功能
     *
     * @param value
     * @return
     */
    public PolicyConfig request_storage_size(boolean value) {
        storage_size = value;
        return this;
    }

    /**
     * 配置下载包的下载路径
     *
     * @param value
     * @return
     */
    public PolicyConfig request_storage_path(boolean value) {
        storage_path = value;
        return this;
    }

    /**
     * 配置当检测到新版本时，弹出通知功能
     *
     * @param value
     * @return
     */
    public PolicyConfig request_notification(boolean value) {
        notification = value;
        return this;
    }

    /**
     * 配置最低电量要求功能
     *
     * @param value
     * @return
     */
    public PolicyConfig request_battery(boolean value) {
        battery = value;
        return this;
    }

    /**
     * 配置循环检测版本周期
     *
     * @param value
     * @return
     */
    public PolicyConfig request_check_cycle(boolean value) {
        check_cycle = value;
        return this;
    }

    /**
     * 配置
     * @param value
     * @return
     */
    public PolicyConfig request_install_force(boolean value) {
        install_force = value;
        return this;
    }
}
