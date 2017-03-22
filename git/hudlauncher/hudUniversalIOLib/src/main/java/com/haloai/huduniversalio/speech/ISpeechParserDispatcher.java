package com.haloai.huduniversalio.speech;

/**
 * Created by zhangrui on 16/5/19.
 */
public interface ISpeechParserDispatcher extends ISpeechParser{
    public static final int PARSE_RES_CODE_NAVI_PROMAT = PARSE_RES_CODE_BASE + 1;
    public static final int PARSE_RES_CODE_CAll_PROMAT = PARSE_RES_CODE_BASE + 2;
    public static final int PARSE_RES_CODE_MUSIC_PROMAT = PARSE_RES_CODE_BASE + 3;
    public static final int PARSE_RES_CODE_SHUTDOWN = PARSE_RES_CODE_BASE + 4;
    public static final int PARSE_RES_CODE_EXIT_PROMAT = PARSE_RES_CODE_BASE + 5;
    public static final int PARSE_RES_CODE_HOME = PARSE_RES_CODE_BASE + 6;
    public static final int PARSE_RES_CODE_COMPANY = PARSE_RES_CODE_BASE + 7;
    public static final int PARSE_RES_CODE_AMAP = PARSE_RES_CODE_BASE + 8;
    public static final int PARSE_RES_CODE_ARWAY = PARSE_RES_CODE_BASE + 9;
    public static final int PARSE_RES_CODE_CLOSE_RECORDER = PARSE_RES_CODE_BASE + 10;
    public static final int PARSE_RES_CODE_OPEN_RECORDER = PARSE_RES_CODE_BASE + 11;
}
