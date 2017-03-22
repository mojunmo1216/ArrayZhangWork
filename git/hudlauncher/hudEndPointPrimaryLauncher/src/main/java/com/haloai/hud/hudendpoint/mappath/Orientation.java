package com.haloai.hud.hudendpoint.mappath;

/**
 * Created by secon on 2016/5/28.
 */

public class Orientation {
    public static final int HORIZONTAL_ALIGN_MASK = 0x00000001;
    public static final int VERTICAL_ALIGN_MASK = 0x00000002;

    public static final int LEFT_ALIGN= 0x00;
    public static final int RIGHT_ALIGN= HORIZONTAL_ALIGN_MASK;
    public static final int TOP_ALIGN = 0x00;
    public static final int BOTTOM_ALIGN = VERTICAL_ALIGN_MASK;


    public enum Basic{
        Horizontal,
        Vertical
    }

    public enum RectOrientation{
        LETT,
        UP,
        RIGHT,
        BOTTOM,
        CENTER,
    }


}
