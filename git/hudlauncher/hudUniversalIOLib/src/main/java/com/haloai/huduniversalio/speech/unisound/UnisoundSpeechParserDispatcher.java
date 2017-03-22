package com.haloai.huduniversalio.speech.unisound;

import com.haloai.huduniversalio.speech.ISpeechParserDispatcher;
import com.haloai.huduniversalio.utils.JsonTool;

import org.json.JSONObject;

/**
 * Created by zhangrui on 16/5/19.
 */
public class UnisoundSpeechParserDispatcher implements ISpeechParserDispatcher{
    @Override
    public int parseSpeechText(String speechText) {
        JSONObject obj = JsonTool.parseToJSONObject(speechText);
        String text = JsonTool.getJsonValue(obj,"text","");
        if(text.equalsIgnoreCase("导航")){
            return PARSE_RES_CODE_NAVI_PROMAT;
        }else if(text.contains("电话")){
            return PARSE_RES_CODE_CAll_PROMAT;
        }else if(text.equalsIgnoreCase("音乐")){
            return  PARSE_RES_CODE_MUSIC_PROMAT;
        }else if(text.equalsIgnoreCase("退出")||text.equalsIgnoreCase("关闭")||text.equalsIgnoreCase("返回")||text.equalsIgnoreCase("取消")){
            return  PARSE_RES_CODE_EXIT_PROMAT;
        }else if(text.contains("回家")){
            return PARSE_RES_CODE_HOME;
        }else if(text.contains("公司")){
            return PARSE_RES_CODE_COMPANY;
        }else if(text.contains("关机")){
            return PARSE_RES_CODE_SHUTDOWN;
        }else if(text.contains("日志")){
            return PARSE_RES_CODE_AMAP;
        }/*else if(text.contains("极简模式")){
            return PARSE_RES_CODE_ARWAY;
        }*/else if(text.contains("打开录像")){
            return PARSE_RES_CODE_OPEN_RECORDER;
        }else if(text.contains("关闭录像")){
            return PARSE_RES_CODE_CLOSE_RECORDER;
        }
        return PARSE_RES_CODE_INTERNAL_ERROR;

    }
}
