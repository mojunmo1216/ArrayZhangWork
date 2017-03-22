package com.haloai.hud.model.v2;

import com.haloai.hud.proto.v2.CommonMessagesProtoDef;

/**
 * 项目名称：hudlauncher
 * 类描述：
 * 创建人：zhang
 * 创建时间：2016/6/12 20:49
 * 修改人：zhang
 * 修改时间：2016/6/12 20:49
 * 修改备注：
 */
public class HudDataDefine {
    private int hud_data_type;
    private boolean bool_param;
    private int int_param;
    private double double_param;
    private String extra_param;
    private String extra_param1;

    public HudDataDefine() {
    }

    public HudDataDefine(CommonMessagesProtoDef.HudDataDefineProto proto) {
        hud_data_type = proto.getHudDataType();

        if (proto.hasBoolParam()) {
            bool_param = proto.getBoolParam();
        }
        if (proto.hasIntParam()) {
            int_param = proto.getIntParam();
        }
        if (proto.hasDoubleParam()) {
            double_param = proto.getDoubleParam();
        }
        if (proto.hasExtraParam()) {
            extra_param = proto.getExtraParam();
        }
        if (proto.hasExtraParam1()) {
            extra_param1 = proto.getExtraParam1();
        }
    }

    public CommonMessagesProtoDef.HudDataDefineProto encapsulate() {
        CommonMessagesProtoDef.HudDataDefineProto.Builder builder = CommonMessagesProtoDef.HudDataDefineProto.newBuilder();
        builder.setHudDataType(hud_data_type);
        builder.setBoolParam(bool_param);
        builder.setIntParam(int_param);
        builder.setDoubleParam(double_param);
        if (extra_param != null) {
            builder.setExtraParam(extra_param);
        }
        if (extra_param1 != null) {
            builder.setExtraParam1(extra_param1);
        }
        return builder.build();
    }

    public int getHud_data_type() {
        return hud_data_type;
    }

    public void setHud_data_type(int hud_data_type) {
        this.hud_data_type = hud_data_type;
    }

    public boolean getBool_param() {
        return bool_param;
    }

    public void setBool_param(boolean bool_param) {
        this.bool_param = bool_param;
    }

    public int getInt_param() {
        return int_param;
    }

    public void setInt_param(int int_param) {
        this.int_param = int_param;
    }

    public double getDouble_param() {
        return double_param;
    }

    public void setDouble_param(double double_param) {
        this.double_param = double_param;
    }

    public String getExtra_param() {
        return extra_param;
    }

    public void setExtra_param(String extra_param) {
        this.extra_param = extra_param;
    }

    public String getExtra_param1() {
        return extra_param1;
    }

    public void setExtra_param1(String extra_param1) {
        this.extra_param1 = extra_param1;
    }
}
