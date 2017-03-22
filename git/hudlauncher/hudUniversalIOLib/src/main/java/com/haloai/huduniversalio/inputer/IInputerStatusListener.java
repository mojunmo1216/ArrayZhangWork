package com.haloai.huduniversalio.inputer;

import com.haloai.huduniversalio.HudIOConstants.*;

/*
 * Inputer(输入源)状态回调
 */
public interface IInputerStatusListener {
     void onWakeupPrompt(HudInputerType hudInputerType); //已唤醒，但只收到基础唤醒词，未进入one-shot，切换为普通唤醒模式

}
